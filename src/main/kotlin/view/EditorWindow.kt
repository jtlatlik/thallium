package view

import controller.EditorController
import javafx.event.EventHandler
import javafx.scene.input.KeyCombination
import javafx.stage.StageStyle
import tornadofx.*
import view.editor.PCBEditor

class EditorWindow : View("Thallium") {

    val editorController: EditorController by inject()

    val editor = PCBEditor()


    override val root = borderpane {

        top = menubar {
            menu("_File") {
                item("_New PCB", "Ctrl+N") {

                }
                item("_Open", "Ctrl+O")
                item("_Save", "Ctrl+S")
                item("Quit")
            }
            menu("_Edit") {
                item("_Undo", "Ctrl+Z")
                item("_Redo", "Ctrl+Y")
                separator()
                item("Cu_t", "Ctrl+X")
                item("_Copy", "Ctrl+C")
                item("_Paste", "Ctrl+V") {
                    action {
                        editor.paste()
                    }
                }
                item("_Delete", "Delete")
                separator()
                item("Find", "Ctrl+F")
            }
            menu("_PCB") {
                item("Nets")
                item("Grids", "Ctrl+G")
                separator()
                item("_Stackup", "Ctrl+L") {
                    action {
                        find(StackupEditorView::class).openModal(stageStyle = StageStyle.UTILITY, block = true)
                        //editor.refresh()
                    }
                }
            }
            menu("_View") {
                item("_Fit", "Ctrl+F") {
                    action {
                        editor.fitView()
                    }
                }
            }
            menu("Help")
        }

        center = editor
        editor.fitView()

    }
}