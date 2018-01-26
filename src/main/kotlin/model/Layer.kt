package model

import javafx.beans.property.*
import javafx.scene.paint.Color
import javafx.util.StringConverter
import tornadofx.*
import java.util.*

class Layer(name: String, type: LayerType, thickness: Double = 35.0, color: Color = Color.BLACK) {

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

}

class LayerViewModel(property: ObjectProperty<Layer>) : ItemViewModel<Layer>(itemProperty = property) {
    val name = bind(autocommit = true) { item?.nameProperty }
    val type = bind(autocommit = true) { item?.typeProperty}
}

enum class LayerType(val text: String) {
    SIGNAL("Signal"),
    DIELECTRIC("Dielectric"),
    SOLDER_MASK("Solder Mask"),
    PASTE_MASK("Paste Mask"),
    PLANE("Internal Plane"),
    SILK("Silk Screen");

    override fun toString(): String {
        return text
    }
}