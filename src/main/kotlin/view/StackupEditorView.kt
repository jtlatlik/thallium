package view

import controller.Stackup
import javafx.collections.FXCollections
import javafx.util.StringConverter
import model.Layer
import model.LayerType
import tornadofx.*

class StackupEditorView : View("Stackup Editor") {
    val stackup: Stackup by inject()
    val layertypes = FXCollections.observableArrayList("A", "B")


    override val root = tableview(stackup.layers) {

        isEditable = true

        column("Layer Name", Layer::nameProperty).makeEditable()
        column("Type", Layer::typeProperty).useTextField(LayerTypeConverter())
        column("Thickness (µm)", Layer::thicknessProperty).makeEditable()
    }
}

class LayerTypeConverter : StringConverter<LayerType>() {
    override fun fromString(string: String?): LayerType {
        if(string == null) {
            return LayerType.Signal
        } else {
            return LayerType.valueOf(string)
        }
    }

    override fun toString(`object`: LayerType?): String {
        return `object`.toString()
    }
}