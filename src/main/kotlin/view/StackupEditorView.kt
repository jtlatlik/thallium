package view

import com.sun.javafx.embed.AbstractEvents
import controller.Stackup
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.MultipleSelectionModel
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableRow
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
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
        rowFactory = RowFactoryCallback()

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

        columns.forEach{
            it.isSortable = false
        }

        onEditCommit {
            totalThickness.invalidate()
            signalLayerCount.invalidate()
        }

        addEventFilter(KeyEvent.KEY_PRESSED, { event ->
            if (event.code == KeyCode.DELETE && selectionModel.selectedIndex != -1) {
                val index = selectionModel.selectedIndex
                stackup.removeLayers(selectionModel.selectedItems.asIterable())
                selectionModel.clearAndSelect(index)

            }

            if(event.isAltDown && event.code == KeyCode.UP  && selectionModel.selectedIndex != -1) {
                val index = selectionModel.selectedIndex
                stackup.moveLayerUp(selectionModel.selectedItem)
                selectionModel.clearAndSelect(index - 1)
            }

            if(event.isAltDown && event.code == KeyCode.DOWN && selectionModel.selectedIndex != -1) {
                val index = selectionModel.selectedIndex
                stackup.moveLayerDown(selectionModel.selectedItem)
                selectionModel.clearAndSelect(index + 1)
            }
        })

    }

    override val root = borderpane {

        top = hbox(20.0) {

            style {
                alignment = Pos.TOP_RIGHT
            }
            padding = Insets(10.0)

            button("Delete Selected") {
                action {
                    stackup.removeLayers(table.selectionModel.selectedItems.asIterable())
                }

            }
            menubutton("_Add Layer") {

                val mnemonicMap = hashMapOf(
                        LayerType.SIGNAL to Pair("_Signal", "S"),
                        LayerType.DIELECTRIC to Pair("_Dielectric", "D"),
                        LayerType.SILK to Pair("S_ilk Screen", "I"),
                        LayerType.PLANE to Pair("_Plane", "P"),
                        LayerType.PASTE_MASK to Pair("Paste _Mask", "M"),
                        LayerType.SOLDER_MASK to Pair("So_lder Mask", "L")
                )

                LayerType.values().forEach {
                    item(mnemonicMap[it]!!.first) {
                        action {
                            val index = table.selectionModel.selectedIndex
                            if (index > -1) {
                                stackup.insertLayer(index, it, 35.0)
                            } else {
                                stackup.addLayer(it, 35.0)
                            }
                            table.requestFocus()
                        }
                        accelerator = KeyCombination.valueOf("Ctrl+" + mnemonicMap[it]!!.second)
                    }
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
                            println(layer.name)
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


