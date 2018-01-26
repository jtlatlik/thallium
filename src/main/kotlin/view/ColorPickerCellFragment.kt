package view

import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.paint.Color
import model.Layer
import model.LayerType
import tornadofx.*

class ColorPickerCellFragment : TableCellFragment<Layer, Color>() {

    override val root = vbox {

        colorpicker(item) {
            bind(itemProperty)
            style {
                backgroundColor += Color.TRANSPARENT
                colorLabelVisible = false
            }

            onEdit {
                show()
            }

            removeWhen {
                rowItemProperty.booleanBinding() {
                    if (it == null)
                        false
                    else
                        it.type == LayerType.DIELECTRIC
                }
            }

        }
    }
}