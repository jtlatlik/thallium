package controller

import javafx.scene.paint.Color
import model.Layer
import model.LayerType
import model.geom.Box
import tornadofx.*
import java.io.File
import java.util.*

class Stackup : Controller() {
    val editorController: EditorController by inject()
    val pcb = editorController.pcb
    val bounds = Box(pcb.origin, pcb.size.x, pcb.size.y)

    fun insertLayer(index: Int, name: String, type: LayerType, thickness: Double, color: Color = Color.BLACK) {
        pcb.stackup.add(index, Layer(name, type, bounds, thickness, color))
    }

    fun insertLayer(index: Int, type: LayerType, thickness: Double) {
        val name = type.toString() + " Layer"
        val rnd = Random()
        val color = Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

        insertLayer(index, name, type, thickness, color)
    }

    fun addLayer(name: String, type: LayerType, thickness: Double, color: Color = Color.BLACK): Layer {
        val layer = Layer(name, type, bounds, thickness, color)
        pcb.stackup.add(layer)
        return layer
    }

    fun addLayer(type: LayerType, thickness: Double) {
        val name = type.toString() + " Layer"
        val rnd = Random()
        val color = Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        addLayer(name, type, thickness, color)
    }

    fun removeLayer(layer: Layer) = pcb.stackup.items.remove(layer)

    fun removeLayers(list: Iterable<Layer>) = pcb.stackup.items.removeAll(list)

    fun moveLayerDown(layer: Layer) = pcb.stackup.items.moveDown(layer)
    fun moveLayerUp(layer: Layer) = pcb.stackup.items.moveUp(layer)

    fun saveToFile(file: File) {

        val json = JsonBuilder()
        json.add("stackup", pcb.stackup.toJSON())
        file.createNewFile()
        file.writeText(json.build().toString())
    }

    fun loadFromFile(file: File) {
        //val text = file.readText()
        TODO("Implement me")
    }



}