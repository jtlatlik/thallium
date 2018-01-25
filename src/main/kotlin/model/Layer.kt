package model

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.util.StringConverter
import tornadofx.*
import java.util.*

class Layer(name: String, type: LayerType, thickness: Double = 35.0) {

    val id = UUID.randomUUID()

    val nameProperty = SimpleStringProperty(name)
    var name by nameProperty

    val typeProperty = SimpleObjectProperty<LayerType>(type)
    val type by typeProperty

    val thicknessProperty = SimpleDoubleProperty(thickness)
    val thickness by typeProperty

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

enum class LayerType {
    Signal, Prepreg, Core, SolderMask, Plane

}