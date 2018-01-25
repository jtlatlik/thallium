package view

import controller.Stackup
import javafx.collections.FXCollections
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.util.StringConverter
import model.Layer
import model.LayerType
import tornadofx.*

class StackupEditorView : View("Stackup Editor") {
    val stackup: Stackup by inject()
    val layertypes = FXCollections.observableArrayList(LayerType.values().asList())
    val totalThickness = doubleBinding(stackup.layers.items) { sumByDouble { it.thickness } }

    override val root = borderpane {

        center = tableview(stackup.layers) {

            isEditable = true

            column("Color", Layer::colorProperty)
                    .fixedWidth(50.0)
                    .cellFragment(ColorPickerCellFragment::class)


            column("Layer Name", Layer::nameProperty)
                    .makeEditable()
                    .weightedWidth(1.0, minContentWidth = true, padding = 10.0)

            column("Type", Layer::typeProperty)
                    .useComboBox(layertypes)
                    .weightedWidth(0.5, minContentWidth = true, padding = 20.0)

            column("Thickness (µm)", Layer::thicknessProperty)
                    .makeEditable()
                    .weightedWidth(0.2)

            column("Components", Layer::allowComponentPlacementProperty)
                    .useCheckbox()
                    .weightedWidth(0.1)

            columnResizePolicy = SmartResize.POLICY
        }

        bottom = hbox(20.0) {

            label(stringBinding(totalThickness) { "Total thickness: $value µm" })
            buttonbar {

                button("OK") {
                    action {
                        for (layer in stackup.layers) {
                            println(layer.thickness)

                        }
                    }
                }
                button("Cancel") {
                    isCancelButton = true
                    action {

                    }
                }
                button("Apply") {

                    action {

                    }
                }
            }
        }
    }
}


