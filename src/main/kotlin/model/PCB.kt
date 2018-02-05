package model

import javafx.scene.paint.Color
import model.geom.CartesianGrid
import model.geom.Grid
import model.geom.Point
import model.geom.Rectangle
import model.primitives.Line
import model.primitives.Via
import tornadofx.SortedFilteredList
import tornadofx.moveDown
import tornadofx.moveUp
import java.io.File
import java.util.*

fun String.fromMil(): Double {
    return this.toDouble() * 0.0254
}

class PCB {

    companion object {
        const val DEFAULT_PCB_WIDTH = 424.0
        const val DEFAULT_PCB_HEIGHT = 85.0
        const val DEFAULT_GRID_STEP = 0.1

        fun loadFromAltiumFile(file: File): PCB {

            val pcb = PCB()
            pcb.addLayer("Top Layer", LayerType.SIGNAL, 35.0, Color.RED)
            val primitives = pcb.stackup.get(0).primitives
            var origin = Point(0.0, 0.0)
            file.forEachLine { line ->


                with(line) {
                    when {
                        startsWith("|RECORD=Board") -> {
                            if (line.contains("|ORIGINX=")) {
                                origin.x = line.substring(line.indexOf("|ORIGINX=") + 9, line.indexOf("mil|ORIGINY")).fromMil()
                                origin.y = line.substring(line.indexOf("|ORIGINY=") + 9, line.indexOf("mil|BIGVISIBL")).fromMil()
                            }
                        }
                        startsWith("|RECORD=Track") -> {
                            if (line.contains("LAYER=MID1|")) {
                                val x1 = line.substring(line.indexOf("X1=") + 3, line.indexOf("mil|Y1")).fromMil()
                                val y1 = line.substring(line.indexOf("Y1=") + 3, line.indexOf("mil|X2")).fromMil()
                                val x2 = line.substring(line.indexOf("X2=") + 3, line.indexOf("mil|Y2")).fromMil()
                                val y2 = line.substring(line.indexOf("Y2=") + 3, line.indexOf("mil|WIDTH")).fromMil()
                                val w = line.substring(line.indexOf("|WIDTH=") + 7, line.indexOf("mil|SUBPOLYINDEX")).fromMil()
                                val track = Line(Point(x1 - origin.x, y1 - origin.y), Point(x2 - origin.x, y2 - origin.y), w)
                                primitives.add(track)
                            }
                        }
                        startsWith("|RECORD=Via") -> {
                            if (line.contains("LAYER=MULTILAYER")) {
                                val x1 = line.substring(line.indexOf("|X=") + 3, line.indexOf("mil|Y=")).fromMil() - origin.x
                                val y1 = line.substring(line.indexOf("|Y=") + 3, line.indexOf("mil|DIA")).fromMil() - origin.y
                                val r = line.substring(line.indexOf("|DIAMETER=") + 10, line.indexOf("mil|HOLE")).fromMil() / 2
                                val h = line.substring(line.indexOf("|HOLESIZE=") + 10, line.indexOf("mil|STARTLAYER")).fromMil() / 2

                                val via = Via(Point(x1, y1), r, h)
                                primitives.add(via)
                            }
                        }
                    }
                }
            }
            println(primitives.size)
            return pcb
        }
    }

    val stackup = SortedFilteredList<Layer>()
    var origin: Point = Point(0.0, 0.0)
    var size: Point = Point(DEFAULT_PCB_WIDTH, DEFAULT_PCB_HEIGHT)
    val grids: ArrayList<Grid> = arrayListOf(CartesianGrid(origin, Point(DEFAULT_GRID_STEP, DEFAULT_GRID_STEP), size.x, size.y))

    private val bounds = Rectangle(origin, size.x, size.y)

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


}