package view.layer

import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import model.Layer


class LayerView(var layer: Layer? = null) : Canvas() {

    val gc = graphicsContext2D
    val painter = PrimitivePainter(gc)

    var zoomFactor: Double = 1.0
    var panX: Double = 0.0
    var panY: Double = 0.0

    init {
        widthProperty().addListener({_ -> redraw() })
        heightProperty().addListener({_ -> redraw()})
    }

    fun panView(deltaX: Double, deltaY: Double) {
        panX += deltaX
        panY += deltaY
        redraw()
    }

    fun zoomView(zoomDelta: Double, mouseX :Double, mouseY: Double) {
        val oldZoomFactor = zoomFactor
        this.zoomFactor *= zoomDelta

        // the pan has to be adjusted so that the location under the mouse cursor is a fix point
        // in the view coordinate system
        panX += ((mouseX - panX) / oldZoomFactor) * (oldZoomFactor - zoomFactor)
        panY += ((mouseY - panY) / oldZoomFactor) * (oldZoomFactor - zoomFactor)
        redraw()
    }

    fun redraw() {
        gc.setTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)

        gc.clearRect(0.0, 0.0, width, height)
        gc.fill = Color.BLACK
        gc.fillRect(0.0, 0.0, width, height)

        gc.setTransform(zoomFactor, 0.0, 0.0, zoomFactor, panX, panY)

        if (layer == null)
            return
        else {
            gc.stroke = layer!!.color
            gc.fill = layer!!.color
            layer!!.primitives.forEach {
                it.accept(painter)
            }
        }
    }

    override fun isResizable(): Boolean = true

    override fun prefWidth(height: Double): Double = width
    override fun prefHeight(width: Double): Double = height

}