package model.pcb.importers

import com.sun.javaws.exceptions.InvalidArgumentException
import com.sun.media.sound.InvalidFormatException
import javafx.scene.paint.Color
import model.geom.Box
import model.geom.Point
import model.geom.minus
import model.nets.Net
import model.pcb.*
import model.primitives.*
import model.pcb.AbstractLayer.*

import tornadofx.SortedFilteredList
import java.io.*
import java.nio.charset.Charset
import java.util.*
import javax.naming.InvalidNameException

object AltiumImporter : Importer {

    private const val EXCEPTION_STRING = "Invalid Altium ASCII PcbDoc file"

    override val name = "Altium PCB"
    override val description = "This importer loads Altium ASCII PcbDoc files"

    override val extensions = listOf("*.PcbDoc")

    override fun load(file: File): PCB {

        val nets = mutableMapOf<Int, Net>()
        val stackup = SortedFilteredList<Layer>()
        var origin = Point(0.0, 0.0)
        var size = Point(0.0, 0.0)

        val boardShape: BoardShape = mutableListOf()
        val pcbObjects: MutableList<Primitive> = mutableListOf()

        var layerIndex = 0
        var lineNumber = 0
        //fall back to ASCII file format
        file.forEachLine(Charset.defaultCharset())
        { line ->
            ++lineNumber
            var tokens = line.split("|").dropWhile { it == "" }
            val record: Map<String, String> = tokens.associateBy({ it.substringBefore("=") }, { it.substringAfter("=") })

            fun parseRegionPath(): List<Primitive> {

                var vertexIndex = 1
                var path = mutableListOf<Primitive>()

                while ("VX$vertexIndex" in record) {
                    val kind = record["KIND$vertexIndex"]?.toInt()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val vx: Double = record["VX$vertexIndex"]?.fromUnit()
                            ?: throw  InvalidFormatException(EXCEPTION_STRING)
                    val vy = record["VY$vertexIndex"]?.fromUnit()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val cx = record["CX$vertexIndex"]?.fromUnit()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val cy = record["CY$vertexIndex"]?.fromUnit()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val sa = record["SA$vertexIndex"]?.toDouble()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val ea = record["EA$vertexIndex"]?.toDouble()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val r = record["R$vertexIndex"]?.fromUnit()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)

                    val kindLast = record["KIND${vertexIndex - 1}"]?.toInt()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val vxLast: Double = record["VX${vertexIndex - 1}"]?.fromUnit()
                            ?: throw  InvalidFormatException(EXCEPTION_STRING)
                    val vyLast = record["VY${vertexIndex - 1}"]?.fromUnit()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val cxLast = record["CX${vertexIndex - 1}"]?.fromUnit()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val cyLast = record["CY${vertexIndex - 1}"]?.fromUnit()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val saLast = record["SA${vertexIndex - 1}"]?.toDouble()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val eaLast = record["EA${vertexIndex - 1}"]?.toDouble()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val rLast = record["R${vertexIndex - 1}"]?.fromUnit()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)

                    val prim = when (kindLast) {
                        0 -> Line(AuxiliaryLayer.BoardShape, Point(vxLast, vyLast), Point(vx, vy))
                        1 -> Arc(AuxiliaryLayer.BoardShape, Point(cxLast, cyLast), rLast, saLast, eaLast)
                        else -> throw InvalidFormatException("Unexpected board shape")
                    }
                    path.add(prim)
                    pcbObjects.add(prim)
                    ++vertexIndex
                }

                return path

            }

            when (record["RECORD"]) {
            //board records contain general information about the PCB such as the stackup and the shape of the board
                "Board" -> {
                    origin.x = record["ORIGINX"]?.fromUnit() ?: origin.x
                    origin.y = record["ORIGINY"]?.fromUnit() ?: origin.y

                    val min = Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
                    val max = Point(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)

                    val path = parseRegionPath()
                    if (path.isNotEmpty()) {
                        boardShape.add(Pair(path, false))

                        path.forEach {
                            val (start, end) = when (it) {
                                is Line -> Pair(it.start, it.end)
                                is Arc -> Pair(it.start, it.end)
                                else -> throw Exception("Unexpected path segment")
                            }

                            min.x = minOf(start.x, min.x)
                            min.y = minOf(start.y, min.y)
                            max.x = maxOf(start.x, max.x)
                            max.y = maxOf(start.y, max.y)
                            min.x = minOf(end.x, min.x)
                            min.y = minOf(end.y, min.y)
                            max.x = maxOf(end.x, max.x)
                            max.y = maxOf(end.y, max.y)
                        }
                        size = max - min
                        println("Board size: $size")
                    }

                    while ("V9_STACK_LAYER${layerIndex}_NAME" in record) {
                        val name = record["V9_STACK_LAYER${layerIndex}_NAME"] ?: ""

                        val type = when {
                            "V9_STACK_LAYER${layerIndex}_DIELTYPE" in record -> {
                                val dieltype = record["V9_STACK_LAYER${layerIndex}_DIELTYPE"]!!.toInt()
                                if (dieltype == 3) LayerType.SOLDER_MASK else LayerType.DIELECTRIC
                            }
                            "V9_STACK_LAYER${layerIndex}_PULLBACKDISTANCE" in record -> LayerType.PLANE
                            "V9_STACK_LAYER${layerIndex}_COMPONENTPLACEMENT" in record -> LayerType.SIGNAL
                            record.keys.find { it.endsWith("CONTEXT") } != null -> LayerType.SILK
                            else -> LayerType.PASTE_MASK
                        }

                        val thickness: Double = when (type) {
                            LayerType.DIELECTRIC, LayerType.SOLDER_MASK -> {
                                record["V9_STACK_LAYER${layerIndex}_DIELHEIGHT"]?.fromUnit()?.times(1000.0)
                                        ?: throw InvalidFormatException()
                            }
                            LayerType.PLANE, LayerType.SIGNAL -> {
                                record["V9_STACK_LAYER${layerIndex}_COPTHICK"]?.fromUnit()?.times(1000.0)
                                        ?: throw InvalidFormatException()
                            }
                            LayerType.SILK -> 0.01
                            else -> 0.0
                        }

                        val rnd = Random()
                        val color = Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

                        stackup.add(Layer(name, type, Box(origin.x, origin.y, size.x, size.y), thickness, color))

                        ++layerIndex
                    }
                }
                "Net" -> {
                    val id = record["ID"]?.toInt()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    record["NAME"]?.let {
                        nets.put(id, Net(it))
                    }
                }
                "Component" -> {
                    val id = record["ID"]?.toInt()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val x = record["X"]?.fromUnit()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val y = record["Y"]?.fromUnit()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val rot = record["ROTATION"]?.toDouble()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                }
                "Pad" -> {
                    val componentId = record["ID"]?.toInt() ?: -1
                    val net = nets.get(record["NET"] ?: -1)
                    val name = record["NAME"]
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val x = record["X"]?.fromUnit()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val y = record["Y"]?.fromUnit()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)


                    val rot = record["ROTATION"]?.toDouble()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val holeSize = record["HOLESIZE"]?.fromUnit()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val holeLength = record["HOLEWIDTH"]?.fromUnit()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val plated = record["PLATED"]?.toBoolean() ?: throw InvalidFormatException(EXCEPTION_STRING)

                    val holeRotation = record["HOLEROTATION"]?.toDouble()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val holeTypeNum = record["HOLETYPE"]?.toInt() ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val padMode = record["PADMODE"]?.toInt() ?: throw InvalidFormatException(EXCEPTION_STRING)


                    val padPrimitives: MutableList<Primitive> = mutableListOf()

                    when (padMode) {
                        0 -> listOf("")                     //SIMPLE
                        1 -> listOf("TOP", "MID", "BOT")    //TOP MIDDLE BOTTOM
                        2 -> TODO("full stack pads not yet supported")  //FULL STACK
                        else -> throw InvalidFormatException(EXCEPTION_STRING)
                    }.forEach {
                        val width = record["${it}XSIZE"]?.fromUnit() ?: record["XSIZE"]?.fromUnit()
                        ?: throw InvalidFormatException(EXCEPTION_STRING + " in line $lineNumber")
                        val height = record["${it}YSIZE"]?.fromUnit() ?: record["YSIZE"]?.fromUnit()
                        ?: throw InvalidFormatException(EXCEPTION_STRING)

                        val shape = record["${it}SHAPE"] ?: record["SHAPE"]
                        ?: throw InvalidFormatException(EXCEPTION_STRING + " in line $lineNumber")

                        val cornerRadius = when (shape) {
                            "ROUND" -> 1.0
                            "ROUNDEDRECTANGLE" -> {
                                val layerCRString = when (it) {
                                    "", "TOP" -> "TOPLAYERCRPCT"
                                    "MID" -> "MIDLAYER1CRPCT"
                                    "BOT" -> "BOTLAYERCRPCT"
                                    else -> TODO("full stack pads not yet supported")
                                }
                                record[layerCRString]?.toDouble()?.div(100)
                                        ?: throw InvalidFormatException(EXCEPTION_STRING)
                            }
                            else -> 0.0
                        }

                        if (shape == "OCTAGONAL") {
                            //octagon is represented as octagonal polygon with 45° corners.
                            //corner length in x and y = 1/4 of shorter edge
                            TODO("implement me as polygon")
                        } else {
                            val layer = when (it) {
                                "" -> SIGNAL_LAYERS
                                "TOP" -> CopperLayer.Top
                                "MID" -> MID_LAYERS
                                "BOT" -> CopperLayer.Bottom
                                else -> TODO("full stack pads not yet supported")
                            }

                            val copperpad = Rectangle(layer, Point(x, y), Point(width, height), cornerRadius)
                            padPrimitives.add(copperpad)
                        }
                    }

                    if (holeSize > 0.0) {
                        val holeType = when (holeTypeNum) {
                            0 -> Hole.HoleType.ROUND
                            1 -> Hole.HoleType.RECTANGLE
                            2 -> Hole.HoleType.SLOT
                            else -> throw InvalidFormatException(EXCEPTION_STRING)
                        }
                        val hole = Hole(ALL_LAYERS, Point(x, y), holeType, holeSize / 2, holeLength, holeRotation, plated)
                        padPrimitives.add(hole)
                    }

                    pcbObjects.add(Pad(padPrimitives, name, rot, net))

                }
                "Track" -> {
                    val layer = getLayerOrNull(record["LAYER"] ?: "")

                    layer?.let {
                        val x1 = record["X1"]?.fromUnit()
                                ?: throw InvalidFormatException(EXCEPTION_STRING)
                        val x2 = record["X2"]?.fromUnit()
                                ?: throw InvalidFormatException(EXCEPTION_STRING)
                        val y1 = record["Y1"]?.fromUnit()
                                ?: throw InvalidFormatException(EXCEPTION_STRING)
                        val y2 = record["Y2"]?.fromUnit()
                                ?: throw InvalidFormatException(EXCEPTION_STRING)
                        val width = record["WIDTH"]?.fromUnit()
                                ?: throw InvalidFormatException(EXCEPTION_STRING)
                        val net = nets.get(record["NET"] ?: -1)

                        pcbObjects.add(Line(layer, Point(x1, y1), Point(x2, y2), width, net))
                    }

                }
                "Via" -> {
                    assert(record["LAYER"] == "MULTILAYER")
                    val x = record["X"]?.fromUnit()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val y = record["Y"]?.fromUnit()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val diameter = record["DIAMETER"]?.fromUnit()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val holeSize = record["HOLESIZE"]?.fromUnit()
                            ?: throw InvalidFormatException(EXCEPTION_STRING)

                    val startLayer = record["STARTLAYER"] ?: throw InvalidFormatException(EXCEPTION_STRING)
                    val endLayer = record["ENDLAYER"] ?: throw InvalidFormatException(EXCEPTION_STRING)

                    val layer = if (startLayer == "TOP" && endLayer == "BOTTOM") {
                        PHYSICAL_LAYERS
                    } else {
                        CopperLayer.Range(getCopperLayer(startLayer), getCopperLayer(endLayer))
                    }

                    val net = nets.get(record["NET"] ?: -1)

                    pcbObjects.add(Via(layer, Point(x, y), diameter / 2, holeSize / 2, net))
                }
                "Region" -> {
                    val isBoardCutout = record["ISBOARDCUTOUT"]?.toBoolean() ?: false

                    if (isBoardCutout) {
                        println("found board cutout")
                        val path = parseRegionPath()
                        boardShape.add(Pair(path, true))
                    }
                }


            }

        }

        val pcb = PCB(stackup, nets.values.toMutableSet(), origin, size, boardShape)

        pcb.objects.addAll(pcbObjects)

        return pcb
    }


    private fun getCopperLayer(name: String): CopperLayer = when (name) {
        "TOP" -> CopperLayer.Top
        "BOTTOM" -> CopperLayer.Bottom
        else -> when {
            name.startsWith("MID") -> CopperLayer.Mid(name.substringAfter("MID").toInt())
            name.startsWith("PLANE") -> CopperLayer.Plane(name.substringAfter("PLANE").toInt())
            else -> throw InvalidNameException("Invalid copper layer: $name")
        }
    }

    private fun getLayerOrNull(name: String): AbstractLayer? = when (name) {
        "TOP" -> CopperLayer.Top
        "BOTTOM" -> CopperLayer.Bottom
        "TOPOVERLAY" -> DielectricLayer.TopSilk
        "BOTTOMOVERLAY" -> DielectricLayer.BottomSilk
        "TOPSOLDER" -> DielectricLayer.TopSolderMask
        "BOTTOMSOLDER" -> DielectricLayer.BottomSolderMask
        "KEEPOUT" -> AuxiliaryLayer.Keepout

        else -> when {
            name.startsWith("MID") -> CopperLayer.Mid(name.substringAfter("MID").toInt())
            name.startsWith("PLANE") -> CopperLayer.Plane(name.substringAfter("PLANE").toInt())
            name.startsWith("MECHANICAL") -> AuxiliaryLayer.Aux(name.substringAfter("MECHANICAL").toInt())
            else -> null
        }
    }


}

