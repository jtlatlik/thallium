package view.layer

import javafx.scene.Cursor
import javafx.scene.canvas.Canvas
import javafx.scene.effect.GaussianBlur
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import model.geom.Point
import tornadofx.c

class ToolView : Canvas() {

    val gc = graphicsContext2D

    var crosshair: Point = Point(0.0,0.0)
    var crosshairVisible: Boolean = false

    init {
        widthProperty().addListener({ _ -> redraw() })
        heightProperty().addListener({ _ -> redraw() })
    }

    fun redraw() {
        gc.clearRect(0.0, 0.0, width, height)
        drawCrosshair()
    }


    fun setCrosshairVisibility(visible: Boolean) {
        crosshairVisible = visible
        scene.cursor = if (visible) Cursor.NONE else Cursor.DEFAULT
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