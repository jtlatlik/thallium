package hello

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.input.KeyCodeCombination
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.*
import view.StackupEditorView

class MyApp: App(StackupEditorView::class)

class MyView: View() {
    override val root = borderpane {
        top = menubar {
            menu("File") {
                menu("New") {
                    item("PCB Project", "ShortCut+N")
                    separator()
                    item("Layer")
                }
                item("Open")
                item("Save")
                item("Quit")
            }
            menu("Edit")
            menu("Help")
        }
    }
}