package controller

import model.Layer
import model.LayerType
import tornadofx.Controller
import tornadofx.SortedFilteredList

class Stackup : Controller() {

    val layers = SortedFilteredList<Layer>()

    fun addLayer(name: String, type: LayerType, thickness: Double) = layers.add(Layer(name, type, thickness))
    fun removeLayer(layer: Layer) = layers.remove(layer)

    init {
        addLayer("Top Layer", LayerType.Signal, 35.0)
        addLayer("Ground Plane 1", LayerType.Plane, 18.0)
        addLayer("Internal Layer 1", LayerType.Signal, 18.0)
        addLayer("Ground Plane 2", LayerType.Plane, 18.0)
        addLayer("Internal Layer 2", LayerType.Signal, 18.0)
        addLayer("Ground Plane 3", LayerType.Plane, 18.0)
        addLayer("Bottom Layer", LayerType.Signal, 35.0)
    }

}