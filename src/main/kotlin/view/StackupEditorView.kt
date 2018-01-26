package view

import controller.Stackup
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.MultipleSelectionModel
import javafx.scene.control.SelectionMode
import javafx.scene.layout.Priority
import model.Layer
import model.LayerType
import tornadofx.*

class StackupEditorView : View("Stackup Editor") {
    val stackup: Stackup by inject()
    val layertypes = FXCollections.observableArrayList(LayerType.values().asList())
    val totalThickness = doubleBinding(stackup.layers) { sumByDouble { it.thickness } }
    val layerCount = integerBinding(stackup.layers) { count() }
    val signalLayerCount = integerBinding(stackup.layers) { count { it.type == LayerType.SIGNAL } }

    val table = tableview(stackup.layers) {

        isEditable = true
        columnResizePolicy = SmartResize.POLICY
        selectionModel.selectionMode = SelectionMode.MULTIPLE


        column("Color", Layer::colorProperty)
                .fixedWidth(50.0)
                .cellFragment(ColorPickerCellFragment::class)

        column("Layer Name", Layer::nameProperty)
                .makeEditable()
                .weightedWidth(0.5, minContentWidth = true, padding = 10.0)

        column("Type", Layer::typeProperty)
                .useComboBox(layertypes)
                .weightedWidth(0.3, minContentWidth = true, padding = 20.0)

        column("Thickness (µm)", Layer::thicknessProperty)
                .makeEditable()
                .weightedWidth(0.2)

        column("Components", Layer::allowComponentPlacementProperty)
                .useCheckbox()
                .weightedWidth(0.2)

        onEditCommit {
            totalThickness.invalidate()
            signalLayerCount.invalidate()
        }

        onUserDelete {
            stackup.removeLayer(it)
            println("USER DELETE")
        }

    }

    override val root = borderpane {

        top = hbox(20.0) {

            style {
                alignment = Pos.TOP_RIGHT
            }
            padding = Insets(10.0)

            button("Move Layer Up") {
                action {
                    val target = table.selectionModel.selectedItem
                    stackup.moveLayerUp(target)
                    table.selectionModel.clearSelection()
                    table.selectionModel.select(target)
                    table.requestFocus()
                }
            }
            button("Move Layer Down") {
                action {
                    val target = table.selectionModel.selectedItem
                    stackup.moveLayerDown(target)
                    table.selectionModel.clearSelection()
                    table.selectionModel.select(target)
                    table.requestFocus()
                }
            }

            button("Delete Selected") {
                action {
                    stackup.removeLayers(table.selectionModel.selectedItems.asIterable())
                }

            }
            menubutton("Add Layer") {
                LayerType.values().forEach {
                    item(it.toString()) { action { stackup.addLayer(it, 35.0) } }
                }
            }

        }

        center = table

        bottom = hbox(20.0) {

            padding = Insets(10.0)
            label(
                    stringBinding(layerCount) { "$value layers " } +
                            stringBinding(signalLayerCount) { "($value signal layers)" }
            )
            label(stringBinding(totalThickness) { "Total thickness: $value µm" }) {
                hgrow = Priority.ALWAYS
            }
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


