package view.editor.layer

import javafx.scene.Cursor
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import model.Line
import model.geom.Point
import view.editor.PCBEditor
import view.editor.tools.SelectionTool
import java.util.*

class ToolView(val editor: PCBEditor) : Canvas() {

    val gc = graphicsContext2D

    var crosshair: Point = Point(0.0,0.0)
    var crosshairVisible: Boolean = false


    init {
        widthProperty().bind(editor.widthProperty())
        heightProperty().bind(editor.heightProperty())

        widthProperty().addListener({ _ -> redraw() })
        heightProperty().addListener({ _ -> redraw() })
        editor.viewport.addObserver(Observer { _, _ -> redraw() })
        editor.activeTool.addObserver(Observer { _, _ -> redraw() })
    }


    fun redraw() {
        gc.setTransform(1.0, 0.0,0.0,1.0,0.0,0.0)
        gc.clearRect(0.0, 0.0, width, height)

        val scale = editor.viewport.getScale()
        val pan = editor.viewport.getPan()
        gc.setTransform(scale, 0.0, 0.0, scale, pan.x, pan.y)

        gc.lineWidth = 1.0 / scale
        when(editor.activeTool) {
            is SelectionTool -> {
                val tool = editor.activeTool as SelectionTool
                val painter = SelectionPainter(gc)

                //draw selection rectangle
                if(tool.isSelecting) {

                    var p1 = tool.selectionRectangle.p1.copy()
                    var size = tool.selectionRectangle.getSize()

                    gc.save()
                    gc.fill = Color.web("rgba(0,255,255, 0.25)")

                    if(size.x < 0) {
                        p1.x += size.x
                        size.x *= -1
                        gc.fill = Color.web("rgba(255,255,0, 0.25)")
                    }
                    if(size.y < 0) {
                        p1.y += size.y
                        size.y *= -1
                    }

                    gc.fillRect(p1.x, p1.y, size.x, size.y)
                    gc.restore()
                }

                tool.selection.forEach { it.accept(painter) }
            }
        }

    }


    fun setCrosshairVisibility(visible: Boolean) {
        crosshairVisible = visible

        redraw()
    }

//    private fun drawOverlay() {
//        gc.save()
//        gc.stroke = Color.WHITE
//        gc.textAlign = TextAlignment.LEFT
//        gc.lineWidth = 1.0
//        gc.fill = c(255, 255, 255, opacity = 0.2)
//        gc.setEffect(GaussianBlur(5.0))
//        gc.fillRoundRect(10.0, 10.0, 200.0, 24.0, 24.0, 24.0)
//        gc.setEffect(null)
//        gc.strokeText("X: %.2f, Y: %.2f".format(crosshair.x, crosshair.y), 22.0, 22.0)
//        gc.restore()
//    }

    private fun drawCrosshair() {
        gc.lineWidth = 1.0
        val crosshairLength = 20
        gc.stroke = Color.LIGHTBLUE
        gc.strokeLine(crosshair.x - crosshairLength, crosshair.y, crosshair.x + crosshairLength, crosshair.y)
        gc.strokeLine(crosshair.x, crosshair.y - crosshairLength, crosshair.x, crosshair.y + crosshairLength)
    }


    override fun isResizable(): Boolean = true

    override fun prefWidth(height: Double): Double = width
    override fun prefHeight(width: Double): Double = height
}