package view

import tornadofx.*
import LayerView
import model.PCB

class EditorWindow: View("Thallium" ) {

    val leftView: StackupEditorView by inject()

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
        left = leftView.root
        //center = pcbView

    }
}