package controller

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.paint.Color
import model.LayerType
import model.Line
import model.Via
import model.geom.Point
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.setValue
import view.EditorWindow
import java.io.File

class EditorController : Controller() {
    val stackup: Stackup by inject()
    val editorWindow: EditorWindow by inject()

    val pcbFile: File? = null

    val fileOpenedProperty = SimpleBooleanProperty(false)
    var fileOpened by fileOpenedProperty

    init {
        with(stackup) {
            addLayer("Top Silk", LayerType.SILK, 15.0, Color.YELLOW)
            addLayer("Top Solder Mask", LayerType.SOLDER_MASK, 15.0, Color.DARKMAGENTA)
            val topLayer = addLayer("Top Layer", LayerType.SIGNAL, 35.0, Color.LIGHTBLUE)
            addLayer("Prepreg", LayerType.DIELECTRIC, 360.0)
            addLayer("Internal Layer 1", LayerType.SIGNAL, 18.0, Color.ORANGE)
            addLayer("Core", LayerType.DIELECTRIC, 700.0)
            addLayer("Internal Layer 2", LayerType.SIGNAL, 18.0, Color.LIGHTBLUE)
            addLayer("Prepreg", LayerType.DIELECTRIC, 360.0)
            val bottomLayer = addLayer("Bottom Layer", LayerType.SIGNAL, 35.0, Color.BLUE)
            addLayer("Bottom Solder Mask", LayerType.SOLDER_MASK, 15.0, Color.DARKBLUE)
            addLayer("Bottom Silk", LayerType.SILK, 15.0, Color.GREEN)

            topLayer.allowComponentPlacement = true
            bottomLayer.allowComponentPlacement = true

            val w = 0.9*3.0

            topLayer.primitives.add(Line(Point(50.0, 50.0), Point(57.0, 57.0), w))
            topLayer.primitives.add(Line(Point(50.0, 70.0), Point(57.0, 63.0), w))
            topLayer.primitives.add(Line(Point(57.0, 57.0), Point(200.0, 57.0), w))
            topLayer.primitives.add(Line(Point(57.0, 63.0), Point(200.0, 63.0), w))
            topLayer.primitives.add(Via(Point(50.0,50.0), diameter = 16.5, holeSize = 6.0))
            topLayer.primitives.add(Via(Point(50.0,70.0), diameter = 16.5, holeSize = 6.0))

            editorWindow.layerview.layer = topLayer
            editorWindow.layerview.redraw()
        }
    }
}