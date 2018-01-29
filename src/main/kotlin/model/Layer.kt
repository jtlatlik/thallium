package model

import javafx.beans.property.*
import javafx.scene.paint.Color
import tornadofx.*
import java.util.*
import javax.json.JsonObject
import kotlin.collections.ArrayList

class Layer(name: String, type: LayerType, thickness: Double = 35.0, color: Color = Color.BLACK) : JsonModel {

    val id = UUID.randomUUID()

    val nameProperty = SimpleStringProperty(name)
    var name by nameProperty

    val typeProperty = SimpleObjectProperty<LayerType>(type)
    var type by typeProperty

    val thicknessProperty = SimpleDoubleProperty(thickness)
    var thickness by thicknessProperty

    val colorProperty = SimpleObjectProperty<Color>(color)
    var color by colorProperty

    val allowComponentPlacementProperty = SimpleBooleanProperty(false)
    var allowComponentPlacement by allowComponentPlacementProperty

    val primitives = ArrayList<Primitive>()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Layer

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("name", name)
            add("type", type.name)
            add("thickness", thickness)
            add("color", color.toString())
            add("components", allowComponentPlacement)
        }
    }

    override fun updateModel(json: JsonObject) {

        with(json) {
            name = string("name")
            type = LayerType.valueOf(string("type")!!)
            thickness = double("thickness")!!
            color = Color.valueOf(string("color")!!)
            allowComponentPlacement = bool("components")!!
        }
    }

}

