package controller

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.paint.Color
import model.LayerType
import model.PCB
import model.findTopLayer
import model.geom.Point
import model.geom.plus
import model.primitives.Hole
import model.primitives.Line
import model.primitives.Polygon
import model.primitives.Via
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.setValue
import view.EditorWindow
import java.io.File

class EditorController : Controller() {
    val pcb: PCB = PCB.loadFromAltiumFile(File("Backplane_RM175.PcbDoc"))

    val pcbFile: File? = null
    val fileOpenedProperty = SimpleBooleanProperty(false)
    var fileOpened by fileOpenedProperty

    val editorWindow: EditorWindow by inject()

    init {
//        with(pcb) {
//            addLayer("Top Silk", LayerType.SILK, 15.0, Color.YELLOW)
//            addLayer("Top Solder Mask", LayerType.SOLDER_MASK, 15.0, Color.DARKMAGENTA)
//            val topLayer = addLayer("Top Layer", LayerType.SIGNAL, 35.0, Color.RED)
//            addLayer("Prepreg", LayerType.DIELECTRIC, 360.0)
//            addLayer("Internal Layer 1", LayerType.SIGNAL, 18.0, Color.ORANGE)
//            addLayer("Core", LayerType.DIELECTRIC, 700.0)
//            addLayer("Internal Layer 2", LayerType.SIGNAL, 18.0, Color.LIGHTBLUE)
//            addLayer("Prepreg", LayerType.DIELECTRIC, 360.0)
//            val bottomLayer = addLayer("Bottom Layer", LayerType.SIGNAL, 35.0, Color.BLUE)
//            addLayer("Bottom Solder Mask", LayerType.SOLDER_MASK, 15.0, Color.DARKBLUE)
//            addLayer("Bottom Silk", LayerType.SILK, 15.0, Color.GREEN)
//
//            topLayer.allowComponentPlacement = true
//            bottomLayer.allowComponentPlacement = true
//
//            val w = 0.15
//
//
//            topLayer.primitives.add(Line(Point(10.0, 10.0), Point(10.5, 10.5), w))
//            topLayer.primitives.add(Line(Point(10.0, 12.0), Point(10.5, 11.5), w))
//            topLayer.primitives.add(Line(Point(10.5, 10.5), Point(20.0, 10.5), w))
//            topLayer.primitives.add(Line(Point(10.5, 11.5), Point(20.0, 11.5), w))
//            topLayer.primitives.add(Via(Point(10.0, 8.0), radius = 0.55 / 2, holeRadius = 0.1))
//            topLayer.primitives.add(Via(Point(10.0, 12.0), radius = 0.55 / 2, holeRadius = 0.1))
//
//            topLayer.primitives.add(Via(Point(29.0, 19.0), radius = 0.55 / 2, holeRadius = 0.1))
//
//            println(topLayer.primitives)
//
//            //bottomLayer.primitives.add(Line(Point(2.0, 2.0), Point(30.0, 20.0), w))
//        }


        pcb.stackup.findTopLayer()?.let {
            //val points = mutableListOf(Point(1.0, 1.0) + pcb.origin, Point(2.0, 0.0), Point(2.0, 2.0), Point(1.0, 2.0))
            //it.primitives.add(Polygon(points))

            val hole = Hole(Point(2.0, 2.0)+ pcb.origin, Hole.HoleType.ROUND, 0.5, 1.0, 0.0, false)
            val pad = Line(pcb.origin + Point(1.5, 2.0), pcb.origin + Point(2.5,2.0), 1.6)

            it.primitives.add(pad)
            it.primitives.add(hole)


        }

        println("i bims")
        editorWindow.editor.setPCB(pcb)
        editorWindow.editor.fitView()
    }
}