package view.editor.layer

import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import model.LayerType
import model.geom.*
import view.editor.PCBEditor
import java.util.*
import kotlin.system.measureNanoTime

class LayerView(val editor: PCBEditor) : Canvas() {

    val gc = graphicsContext2D

    val painter = PrimitivePainter(gc)

    init {
        widthProperty().bind(editor.widthProperty())
        heightProperty().bind(editor.heightProperty())
        widthProperty().addListener({ _ -> redraw() })
        heightProperty().addListener({ _ -> redraw() })
        editor.viewport.addObserver(Observer { _, _ -> redraw() })
    }

    fun redraw() {
        var drawn = 0
        val time = measureNanoTime {


            gc.setTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
            gc.fill = Color.gray(0.2)
            gc.fillRect(0.0, 0.0, width, height)

            //apply viewport transformation
            val scale = editor.viewport.getScale()
            val pan = editor.viewport.getPan()
            gc.setTransform(scale.x, 0.0, 0.0, scale.y, pan.x, pan.y)

            editor.pcb?.let { pcb ->

                //draw grids
                pcb.grids.forEach { drawGrid(it) }

                //draw origin marker
                drawOrigin(pcb.origin)
            }


            val p1 = editor.viewport.inverseTransform(Point(0.0, 0.0))
            val p2 = editor.viewport.inverseTransform(Point(width, height))
            val viewBounds = Rectangle(p1, p2)

            editor.pcb?.stackup?.filter { it.type != LayerType.DIELECTRIC }?.forEach { layer ->

                layer.primitives.retrieve(viewBounds).forEach {
                    gc.stroke = layer.color
                    gc.fill = layer.color
                    it.accept(painter)
                    gc.lineWidth = 1.0 / editor.viewport.getScale().x
                    painter.drawBoundingRectangle(it)
                    ++drawn
                }
            }
        }
        //draw multilayer primitives (via, pads with holes, etc.) at last
        editor.pcb?.multiLayerPrimitives?.forEach {
            it.accept(painter)
            ++drawn
        }

        println("layerview redraw. primitives: $drawn. time: ${time.toDouble() / 1000} µs")

    }


    override fun isResizable(): Boolean = true

    override fun prefWidth(height: Double): Double = width
    override fun prefHeight(width: Double): Double = height

    /**
     * Draws the origin marker at the Point given by [origin]
     *
     * @param a point denoting the origin of the PCB
     */
    private fun drawOrigin(origin: Point) {
        gc.stroke = Color.WHITE
        gc.fill = Color.WHITE

        val scale = editor.viewport.getScale().x
        gc.lineWidth = 3 / scale
        val offset = 0.25

        gc.strokeLine(origin.x, origin.y, origin.x + offset, origin.y)
        gc.strokeLine(origin.x, origin.y, origin.x, origin.y + offset)
        gc.fillPolygon(doubleArrayOf(origin.x - offset/8, origin.x + offset/8, origin.x),
                doubleArrayOf(origin.y + offset, origin.y + offset, origin.y + offset*1.25),3)
        gc.fillPolygon(doubleArrayOf(origin.x + offset, origin.x + offset, origin.x + offset*1.25),
                doubleArrayOf(origin.y - offset/8, origin.y + offset/8, origin.y),3)
    }

    private fun drawGrid(grid: Grid) {

        gc.lineWidth = 0.5 / editor.viewport.getScale().x
        gc.stroke = grid.fineColor

        when (grid) {
            is CartesianGrid -> {
                val (minX, minY) = Point(grid.origin.x, grid.origin.y)
                val (maxX, maxY) = Point(minX + grid.width, minY + grid.height)

                gc.fill = Color.BLACK
                gc.fillRect(minX, minY, grid.width, grid.height)

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