package view

import javafx.event.EventType
import javafx.scene.paint.Color
import model.Layer
import model.LayerType
import tornadofx.*

class ColorPickerCellFragment : TableCellFragment<Layer, Color>() {

    override val root = vbox {


        colorpicker(item) {
            this.valueProperty().bindBidirectional(rowItemProperty.select { it.colorProperty })

            style {
                backgroundColor += Color.TRANSPARENT
                colorLabelVisible = false
            }

            onEdit {
                show()

            }



            visibleProperty().bind(rowItemProperty.select { it.typeProperty.isNotEqualTo(LayerType.DIELECTRIC) })
            enableWhen { visibleProperty() }
        }
    }
}