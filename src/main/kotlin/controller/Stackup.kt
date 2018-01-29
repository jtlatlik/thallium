package controller

import javafx.scene.paint.Color
import model.Layer
import model.LayerType
import tornadofx.*
import java.io.File
import java.util.*

class Stackup : Controller() {

    val layers = SortedFilteredList<Layer>()

    fun insertLayer(index: Int, name: String, type: LayerType, thickness: Double, color: Color = Color.BLACK) {
        layers.add(index, Layer(name, type, thickness, color))
    }

    fun insertLayer(index: Int, type: LayerType, thickness: Double) {
        val name = type.toString() + " Layer"
        val rnd = Random()
        val color = Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

        insertLayer(index, name, type, thickness, color)
    }

    fun addLayer(name: String, type: LayerType, thickness: Double, color: Color = Color.BLACK): Layer {
        val layer = Layer(name, type, thickness, color)
        layers.add(layer)
        return layer
    }

    fun addLayer(type: LayerType, thickness: Double) {
        val name = type.toString() + " Layer"
        val rnd = Random()
        val color = Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        addLayer(name, type, thickness, color)
    }

    fun removeLayer(layer: Layer) = layers.items.remove(layer)

    fun removeLayers(list: Iterable<Layer>) = layers.items.removeAll(list)

    fun moveLayerDown(layer: Layer) = layers.items.moveDown(layer)
    fun moveLayerUp(layer: Layer) = layers.items.moveUp(layer)

    fun saveToFile(file: File) {

        val json = JsonBuilder()
        json.add("stackup", layers.toJSON())
        file.createNewFile()
        file.writeText(json.build().toString())
    }

    fun loadFromFile(file: File) {
        val text = file.readText()
        TODO("Implement me")
    }

    init {


    }


}