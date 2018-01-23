package view

import javafx.collections.FXCollections
import javafx.scene.control.Button
import model.Layer
import model.LayerMaterial
import model.PhysicalLayer
import tornadofx.*
import java.awt.Color

private val materials =  FXCollections.observableArrayList<String>( LayerMaterial.values().map { it.toString() })

private val defaultStackup = listOf<PhysicalLayer>(
        PhysicalLayer("Top Solder Mask", LayerMaterial.SOLDER_MASK, 10, color = Color.GREEN),
        PhysicalLayer("Top Layer", LayerMaterial.COPPER, 35, color = Color.RED),
        PhysicalLayer("Core", LayerMaterial.CORE, 1500, color = Color.GRAY),
        PhysicalLayer("Bottom Layer", LayerMaterial.COPPER, 35, color = Color.BLUE) ,
        PhysicalLayer("Bottom Solder Mask", LayerMaterial.SOLDER_MASK, 10,Color.GREEN)
).observable()

class StackupEditorView: View("Stackup Editor") {
    override val root = borderpane {
        center = tableview(defaultStackup) {
            column("Color", PhysicalLayer::color).cellFormat {
                style { backgroundColor += c(it.red, it.green, it.blue) }
            }
            column("Name", PhysicalLayer::name)
            column("Material", PhysicalLayer::material).cellFormat {

                graphic = cache {
                    combobox(values = materials)

                }
            }
            column("Thickness (µm)", PhysicalLayer::thickness)
            columnResizePolicy = SmartResize.POLICY
        }
    }
}