package controller

import javafx.scene.paint.Color
import model.Layer
import model.LayerType
import tornadofx.Controller
import tornadofx.SortedFilteredList

class Stackup : Controller() {

    val layers = SortedFilteredList<Layer>()

    fun addLayer(name: String, type: LayerType, thickness: Double, color: Color) = layers.add(Layer(name, type, thickness, color))
    fun removeLayer(layer: Layer) = layers.remove(layer)

    init {
        addLayer("Top Silk", LayerType.SILK, 15.0, Color.YELLOW)
        addLayer("Top Layer", LayerType.SIGNAL, 35.0, Color.RED)
        addLayer("Ground Plane 1", LayerType.PLANE, 18.0, Color.GRAY)
        addLayer("Internal Layer 1", LayerType.SIGNAL, 18.0, Color.ORANGE)
        addLayer("Ground Plane 2", LayerType.PLANE, 18.0, Color.DARKGRAY)
        addLayer("Internal Layer 2", LayerType.SIGNAL, 18.0, Color.LIGHTBLUE)
        addLayer("Ground Plane 3", LayerType.PLANE, 18.0, Color.LIGHTGRAY)
        addLayer("Bottom Layer", LayerType.SIGNAL, 35.0, Color.BLUE)
        addLayer("Bottom Silk", LayerType.SILK, 15.0, Color.GREEN)

    }

}