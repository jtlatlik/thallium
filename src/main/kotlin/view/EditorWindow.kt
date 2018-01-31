package view

import javafx.event.EventHandler
import javafx.stage.StageStyle
import model.Via
import model.geom.Point
import tornadofx.*
import view.layer.LayerView

class EditorWindow : View("Thallium") {



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
                item("_Stackup", "Ctrl+L") {
                    action {
                        find(StackupEditorView::class).openModal(stageStyle = StageStyle.UTILITY, block = true)
                        editor.refresh()
                    }
                }
            }
            menu("Help")
        }

        center = editor



    }
}