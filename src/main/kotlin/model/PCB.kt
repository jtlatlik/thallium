package model

import com.sun.media.sound.InvalidFormatException
import javafx.scene.paint.Color
import model.adt.QuadTree
import model.geom.*
import model.nets.Net
import model.primitives.*
import model.primitives.Hole.HoleType
import tornadofx.SortedFilteredList
import tornadofx.moveDown
import tornadofx.moveUp
import java.io.File
import java.nio.charset.Charset
import java.util.*

typealias BoardShape = MutableList<Pair<List<Primitive>, Boolean>>

fun String.fromUnit(): Double {
    if (this.endsWith("mil"))
        return this.substringBefore("mil").toDouble() * 0.0254
    if (this.endsWith("mm"))
        return this.substringBefore("mm").toDouble()
    throw NumberFormatException("illegal unit or no unit found in number: $this")
}

const val EXCEPTION_STRING = "Invalid Altium ASCII PcbDoc file"

fun SortedFilteredList<Layer>.findTopLayer() = this.find { it.type == model.LayerType.SIGNAL }
fun SortedFilteredList<Layer>.findBottomLayer() = this.findLast { it.type == model.LayerType.SIGNAL }

data class PCB(val stackup: SortedFilteredList<Layer>,
               val nets: MutableSet<Net>,
               var origin: Point = Point(0.0, 0.0),
               var size: Point = Point(DEFAULT_PCB_WIDTH, DEFAULT_PCB_HEIGHT), val boardShape: BoardShape) {

    val allPrimitives: List<QuadTree<Primitive>>
        get() = listOf(multiLayerPrimitives) + stackup.items.map { it.primitives }


    val grids: ArrayList<Grid> = arrayListOf(CartesianGrid(origin, Point(DEFAULT_GRID_STEP, DEFAULT_GRID_STEP), size.x, size.y))

    private val bounds = Box(origin, size.x, size.y)

    val multiLayerPrimitives = QuadTree<Primitive>(bounds)


    fun insertLayer(index: Int, name: String, type: LayerType, thickness: Double, color: Color = Color.BLACK) {
        stackup.add(index, Layer(name, type, bounds, thickness, color))
    }

    fun insertLayer(index: Int, type: LayerType, thickness: Double) {
        val name = type.toString() + " Layer"
        val rnd = Random()
        val color = Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

        insertLayer(index, name, type, thickness, color)
    }

    fun addLayer(name: String, type: LayerType, thickness: Double, color: Color = Color.BLACK): Layer {
        val layer = Layer(name, type, bounds, thickness, color)
        stackup.add(layer)
        return layer
    }

    fun addLayer(type: LayerType, thickness: Double) {
        val name = type.toString() + " Layer"
        val rnd = Random()
        val color = Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        addLayer(name, type, thickness, color)
    }

    fun removeLayer(layer: Layer) = stackup.items.remove(layer)

    fun removeLayers(list: Iterable<Layer>) = stackup.items.removeAll(list)

    fun moveLayerDown(layer: Layer) = stackup.items.moveDown(layer)
    fun moveLayerUp(layer: Layer) = stackup.items.moveUp(layer)


    companion object {
        const val DEFAULT_PCB_WIDTH = 424.0
        const val DEFAULT_PCB_HEIGHT = 85.0
        const val DEFAULT_GRID_STEP = 0.5

        fun loadFromAltiumFile(file: File): PCB {

            val nets = mutableMapOf<Int, Net>()
            val stackup = SortedFilteredList<Layer>()
            var origin = Point(0.0, 0.0)
            var size = Point(0.0, 0.0)

            val boardShape: BoardShape = mutableListOf()

            var multiLayerPrimitives: QuadTree<Primitive>? = null


            fun getLayerOrNull(layerString: String) = when (layerString) {
                "TOP" -> stackup.firstOrNull { it.type == LayerType.SIGNAL }
                "BOTTOM" -> stackup.lastOrNull { it.type == LayerType.SIGNAL }
                "TOPOVERLAY" -> stackup.firstOrNull { it.type == LayerType.SILK }
                "BOTTOMOVERLAY" -> stackup.lastOrNull { it.type == LayerType.SILK }

                else -> {
                    if (layerString.startsWith("MID")) {
                        val index = layerString.substringAfter("MID").toInt()
                        stackup.filter { it.type == LayerType.SIGNAL }.drop(1).getOrNull(index)
                    } else {
                        null
                    }
                }
            }

            var layerIndex = 0


            file.forEachLine(Charset.defaultCharset()) { line ->

                val tokens = line.split("|").drop(1)
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
                            0 -> Line(Point(vxLast, vyLast), Point(vx, vy))
                            1 -> Arc(Point(cxLast, cyLast), rLast, saLast, eaLast)
                            else -> throw InvalidFormatException("Unexpected board shape")
                        }
                        path.add(prim)
                        println(prim)
                        ++vertexIndex
                    }

                    return path

                }

                when (record["RECORD"]) {
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
                            if (multiLayerPrimitives == null) {
                                multiLayerPrimitives = QuadTree<Primitive>(Box(origin, size.x, size.y))
                            }
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
//                        val width = record["XSIZE"]?.fromUnit()
//                                ?: throw InvalidFormatException(EXCEPTION_STRING)
//                        val height = record["YSIZE"]?.fromUnit()
//                                ?: throw InvalidFormatException(EXCEPTION_STRING)
                        val rot = record["ROTATION"]?.toDouble()
                                ?: throw InvalidFormatException(EXCEPTION_STRING)
                        val holeSize = record["HOLESIZE"]?.fromUnit()
                                ?: throw InvalidFormatException(EXCEPTION_STRING)
                        val holeLength = record["HOLEWIDTH"]?.fromUnit()
                                ?: throw InvalidFormatException(EXCEPTION_STRING)
                        val plated = record["PLATED"]?.toBoolean()
                                ?: throw InvalidFormatException(EXCEPTION_STRING)
                        val shape = record["SHAPE"]
                                ?: throw InvalidFormatException(EXCEPTION_STRING)
                        val holeRotation = record["HOLEROTATION"]?.toDouble()
                                ?: throw InvalidFormatException(EXCEPTION_STRING)
                        val holeTypeNum = record["HOLETYPE"]?.toInt()
                                ?: throw InvalidFormatException(EXCEPTION_STRING)

                        val padPrimitives: MutableList<Primitive> = mutableListOf()

                        val cornerRadius = when (shape) {
                            "ROUND" -> 1.0
                            "ROUNDEDRECTANGLE" -> {
                                record["TOPLAYERCRPCT"]?.toDouble()?.div(100)
                                        ?: throw InvalidFormatException(EXCEPTION_STRING)
                            }
                            else -> 0.0
                        }

                        if (holeSize > 0.0) {
                            val holeType = when (holeTypeNum) {
                                0 -> HoleType.ROUND
                                1 -> HoleType.RECTANGLE
                                2 -> HoleType.SLOT
                                else -> throw InvalidFormatException(EXCEPTION_STRING)
                            }
                            val hole = Hole(Point(x, y), holeType, holeSize / 2, holeLength, holeRotation, plated)
                            padPrimitives.add(hole)
                        }

                        //multiLayerPrimitives.add(Pad(padPrimitives))
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

                            it.primitives.add(Line(Point(x1, y1), Point(x2, y2), width, net))
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

                        val net = nets.get(record["NET"] ?: -1)

                        multiLayerPrimitives?.add(Via(Point(x, y), diameter / 2, holeSize / 2, net))
                    }
                    "Region" -> {
//                        val isBoardCutout = record["ISBOARDCUTOUT"]?.toBoolean() ?: false
//
//                        if (isBoardCutout) {
//                            val path = parseRegionPath()
//                        }
                    }


                }

            }

            val pcb = PCB(stackup, nets.values.toMutableSet(), origin, size, boardShape)

            multiLayerPrimitives?.let {
                pcb.multiLayerPrimitives.addAll(it)
            }

            return pcb
        }
    }

}