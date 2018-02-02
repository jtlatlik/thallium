package view.editor.layer

import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import model.Layer
import model.geom.CartesianGrid
import model.geom.Grid
import model.geom.Point
import model.geom.PolarGrid
import view.editor.PCBEditor
import java.util.*

class LayerView(val editor: PCBEditor) : Canvas() {

    val gc = graphicsContext2D

    var layer: Layer? = null
    var grid: Grid? = null
    val painter = PrimitivePainter(gc)

    init {
        widthProperty().bind(editor.widthProperty())
        heightProperty().bind(editor.heightProperty())

        widthProperty().addListener({ _ -> redraw() })
        heightProperty().addListener({ _ -> redraw() })
        editor.viewport.addObserver(Observer { _, _ -> redraw() })
    }

    fun redraw() {
        gc.setTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
        gc.fill = Color.BLACK
        gc.fillRect(0.0, 0.0, width, height)

        val scale = editor.viewport.getScale()
        val pan = editor.viewport.getPan()
        gc.setTransform(scale, 0.0, 0.0, scale, pan.x, pan.y)

        if (grid != null)
            drawGrid()

        if (layer != null) {
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

    private fun drawGrid() {

        gc.lineWidth = 0.5 / editor.viewport.getScale()
        gc.stroke = grid!!.fineColor

        when (grid) {
            is CartesianGrid -> {
                val grid = grid as CartesianGrid

                val (minX, minY) = Point(grid.origin.x, grid.origin.y)
                val (maxX, maxY) = Point(minX + grid.width, minY + grid.height)
                var p = Point(minX, minY)

                while (p.y <= maxY) {
                    if (grid.step.y >= 5 * gc.lineWidth) {
                        gc.strokeLine(minX, p.y, maxX, p.y)
                    }
                    p.y += grid.step.y
                }
                while (p.x <= maxX) {
                    if (grid.step.x >= 5 * gc.lineWidth) {
                        gc.strokeLine(p.x, minY, p.x, maxY)
                    }
                    p.x += grid.step.x
                }
            }
            is PolarGrid -> {
                val grid = grid as PolarGrid
                val (minR, maxR) = Point(grid.startRadius, grid.stopRadius)
                val (minTheta, maxTheta) = Point(grid.startAngle, grid.stopAngle)
                var r: Double = minR
                var theta: Double = minTheta

                //draw arcs
                while (r <= maxR) {
                    gc.strokeArc(grid.origin.x - r, grid.origin.y - r, r * 2, r * 2,
                            minTheta, -maxTheta, ArcType.OPEN)
                    r += grid.radialStep
                }
                //draw angular lines
                while (theta <= Math.toRadians(maxTheta)) {
                    gc.strokeLine(
                            grid.origin.x + minR * Math.cos(theta), grid.origin.y + minR * Math.sin(theta),
                            grid.origin.x + maxR * Math.cos(theta), grid.origin.y + maxR * Math.sin(theta))

                    theta += Math.toRadians(grid.angularStep)
                }
            }
        }


    }


}