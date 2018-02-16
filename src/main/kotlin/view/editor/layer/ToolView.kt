package view.editor.layer

import javafx.event.EventHandler
import javafx.scene.canvas.Canvas
import javafx.scene.effect.GaussianBlur
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import model.geom.Point
import tornadofx.CssRule.Companion.c
import tornadofx.c
import view.editor.PCBEditor
import view.editor.tools.SelectionTool
import java.util.*
import kotlin.system.measureNanoTime

class ToolView(val editor: PCBEditor) : Canvas() {

    val gc = graphicsContext2D

    var crosshairVisible: Boolean = false

    init {
        widthProperty().bind(editor.widthProperty())
        heightProperty().bind(editor.heightProperty())
        widthProperty().addListener({ _ -> redraw() })
        heightProperty().addListener({ _ -> redraw() })

        this.onKeyPressed = EventHandler { println("lol") }

        editor.viewport.addObserver(Observer { _, _ -> redraw() })
        editor.activeTool.addObserver(Observer { _, _ -> redraw() })
    }

    fun redraw() {
        val time = measureNanoTime {

            gc.setTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
            gc.clearRect(0.0, 0.0, width, height)

            val scale = editor.viewport.getScale()
            val pan = editor.viewport.getPan()
            gc.setTransform(scale.x, 0.0, 0.0, scale.y, pan.x, pan.y)

            gc.lineWidth = 1.0 / scale.x
            when (editor.activeTool) {
                is SelectionTool -> {
                    val tool = editor.activeTool as SelectionTool
                    val painter = SelectionPainter(gc)

                    //draw selection bounds
                    if (tool.isSelecting) {

                        var p1 = tool.selectionBox.p1.copy()
                        var size = tool.selectionBox.getSize()

                        gc.save()
                        gc.fill = Color.web("rgba(0,255,255, 0.25)")

                        if (size.x < 0) {
                            p1.x += size.x
                            size.x *= -1
                            gc.fill = Color.web("rgba(255,255,0, 0.25)")
                        }
                        if (size.y < 0) {
                            p1.y += size.y
                            size.y *= -1
                        }

                        gc.fillRect(p1.x, p1.y, size.x, size.y)
                        gc.restore()
                    }

                    tool.selection.keys.forEach {
                        it.accept(painter)
                    }
                }
            }
            editor.pcb?.let {pcb ->
                //draw origin marker
                drawOrigin(pcb.origin)

                gc.setTransform(1.0,0.0,0.0,1.0,0.0,0.0)
                drawOverlay()
            }


        }
        //println("toolview redraw. time:  time: ${time.toDouble() / 1000} µs")
    }


    /**
     * Draws the origin marker at the Point given by [origin]
     *
     * @param a point denoting the origin of the PCB
     */
    private fun drawOrigin(origin: Point) {
        gc.setLineDashes()
        gc.stroke = Color.WHITE
        gc.fill = Color.WHITE

        val scale = editor.viewport.getScale().x
        gc.lineWidth = 0.5 / editor.viewport.getScale().x

        val offset = 20 / scale

        gc.strokeLine(origin.x, origin.y, origin.x + offset, origin.y)
        gc.strokeLine(origin.x, origin.y, origin.x, origin.y + offset)
        gc.fillPolygon(doubleArrayOf(origin.x - offset/8, origin.x + offset/8, origin.x),
                doubleArrayOf(origin.y + offset, origin.y + offset, origin.y + offset*1.25),3)
        gc.fillPolygon(doubleArrayOf(origin.x + offset, origin.x + offset, origin.x + offset*1.25),
                doubleArrayOf(origin.y - offset/8, origin.y + offset/8, origin.y),3)

        gc.fillOval(origin.x - offset/8, origin.y - offset/8, offset/4, offset/4)
    }

    fun setCrosshairVisibility(visible: Boolean) {
        crosshairVisible = visible

    }

    private fun drawOverlay() {
        gc.save()
        val crosshair = editor.crosshair
        gc.stroke = Color.WHITE
        gc.textAlign = TextAlignment.LEFT
        gc.lineWidth = 1.0
        gc.fill = c(255, 255, 255, opacity = 0.2)
        gc.setEffect(GaussianBlur(5.0))
        gc.fillRoundRect(10.0, 10.0, 200.0, 24.0, 24.0, 24.0)
        gc.setEffect(null)
        gc.strokeText("X: %.2f, Y: %.2f".format(crosshair.x, crosshair.y), 22.0, 22.0)
        gc.restore()
    }

    private fun drawCrosshair() {
        val crosshair = editor.crosshair
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