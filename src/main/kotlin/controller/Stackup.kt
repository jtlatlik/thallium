package controller

import javafx.scene.paint.Color
import model.Layer
import model.LayerType
import tornadofx.*
import java.util.*

class Stackup : Controller() {

    val layers = SortedFilteredList<Layer>()

    fun insertLayer(index: Int, name: String, type: LayerType, thickness: Double, color: Color = Color.BLACK) {
        layers.add(index, Layer(name, type, thickness, color))
    }

    fun insertLayer(index: Int, type: LayerType, thickness: Double) {
        val name = type.toString() + " Layer"
        val rnd = Random()
        val color = Color.rgb(rnd.nextInt(256),rnd.nextInt(256),rnd.nextInt(256))

        insertLayer(index, name, type, thickness, color)
    }

    fun addLayer(name: String, type: LayerType, thickness: Double, color: Color = Color.BLACK) : Layer {
        val layer = Layer(name, type, thickness, color)
        layers.add(layer)
        return layer
    }

    fun addLayer(type: LayerType, thickness: Double) {
        val name = type.toString() + " Layer"
        val rnd = Random()
        val color = Color.rgb(rnd.nextInt(256),rnd.nextInt(256),rnd.nextInt(256))
        addLayer(name, type, thickness, color)
    }

    fun removeLayer(layer: Layer) = layers.items.remove(layer)

    fun removeLayers(list: Iterable<Layer>) = layers.items.removeAll(list)

    fun moveLayerDown(layer: Layer) = layers.items.moveDown(layer)
    fun moveLayerUp(layer: Layer) = layers.items.moveUp(layer)

    init {
        addLayer("Top Silk", LayerType.SILK, 15.0, Color.YELLOW)
        addLayer("Top Solder Mask", LayerType.SOLDER_MASK, 15.0, Color.DARKMAGENTA)
        addLayer("Top Layer", LayerType.SIGNAL, 35.0, Color.RED).allowComponentPlacement = true
        addLayer("Prepreg", LayerType.DIELECTRIC, 360.0)
        addLayer("Internal Layer 1", LayerType.SIGNAL, 18.0, Color.ORANGE)
        addLayer("Core", LayerType.DIELECTRIC, 700.0)
        addLayer("Internal Layer 2", LayerType.SIGNAL, 18.0, Color.LIGHTBLUE)
        addLayer("Prepreg", LayerType.DIELECTRIC, 360.0)
        addLayer("Bottom Layer", LayerType.SIGNAL, 35.0, Color.BLUE).allowComponentPlacement = true
        addLayer("Bottom Solder Mask", LayerType.SOLDER_MASK, 15.0, Color.DARKBLUE)
        addLayer("Bottom Silk", LayerType.SILK, 15.0, Color.GREEN)

    }

}