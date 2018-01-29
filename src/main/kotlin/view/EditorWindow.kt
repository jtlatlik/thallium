package view

import javafx.event.EventHandler
import javafx.stage.StageStyle
import model.Via
import model.geom.Point
import tornadofx.*
import view.layer.LayerView

class EditorWindow : View("Thallium") {



    val layerview = LayerView()




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
                        layerview.redraw()
                    }
                }
            }
            menu("Help")
        }

        center = stackpane {

            add(layerview)
            layerview.widthProperty().bind(this.widthProperty())
            layerview.heightProperty().bind(this.heightProperty())

        }

        layerview.onMouseClicked = EventHandler {

            //to place correctly, the currently applied affine transformation has to be inverted
            val targetX = (it.x - layerview.panX) / layerview.zoomFactor
            val targetY = (it.y - layerview.panY) / layerview.zoomFactor

            layerview.layer!!.primitives.add(Via(Point(targetX, targetY), 10.0, 4.0))
            layerview.redraw()
        }

        layerview.onScroll = EventHandler {
            if (it.isControlDown) {
                layerview.zoomView(if(it.deltaY < 0) 0.8 else 1.2, it.x, it.y)
            } else {
                layerview.panView(it.deltaX, it.deltaY)
            }
        }

    }
}