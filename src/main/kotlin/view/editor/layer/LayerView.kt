package view.editor.layer

import javafx.scene.canvas.Canvas
import javafx.scene.effect.BlurType
import javafx.scene.effect.Shadow
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import model.pcb.LayerType
import model.pcb.PCB
import model.geom.*
import model.primitives.Arc
import model.primitives.Line
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


                //draw board outline
                drawBoardOutline(pcb)

                //draw grids
                pcb.grids.forEach { drawGrid(it) }

                val p1 = editor.viewport.inverseTransform(Point(0.0, 0.0))
                val p2 = editor.viewport.inverseTransform(Point(width, height))
                val viewBounds = Box(p1, p2)

                pcb.stackup.filter { it.type != LayerType.DIELECTRIC }.reversed().forEach { layer ->

                    layer.primitives.retrieve(viewBounds).forEach {
                        gc.stroke = layer.color
                        gc.fill = layer.color
                        it.accept(painter)
                        gc.lineWidth = 1.0 / editor.viewport.getScale().x
                        //painter.drawBoundingRectangle(it)

                        ++drawn


                    }
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

    private fun drawBoardOutline(pcb: PCB) {
        gc.lineWidth = 1.0 / editor.viewport.getScale().x
        gc.fill = Color.BLACK
        gc.stroke = Color.GRAY

        pcb.boardShape.forEach { (path, subtract) ->
            var cursor = Point(0.0, 0.0)
            gc.fill = if (subtract == true) Color.gray(0.2) else Color.BLACK
            gc.beginPath()
            path.forEachIndexed { index, prim ->
                when (prim) {
                    is Line -> {
                        if (index == 0) {
                            gc.moveTo(prim.start.x, prim.start.y)
                            cursor = prim.start
                        }
                        gc.lineTo(prim.end.x, prim.end.y)
                        cursor = prim.end

                    }
                    is Arc -> {
                        if (index == 0) {
                            gc.moveTo(prim.start.x, prim.start.y)
                            cursor = prim.start
                        }
                        gc.lineTo(prim.end.x, prim.end.y)
                        //TODO implement me properly
                        //gc.arc(prim.center.x, prim.center.y, prim.radius, prim.radius, startAngle, baseline)
                    }
                    else -> throw Exception("invalid path segment")
                }
            }
            gc.closePath()
            gc.fill()
            gc.stroke()
        }

    }

    private fun drawGrid(grid: Grid) {
        gc.save()
        gc.lineWidth = 0.5 / editor.viewport.getScale().x
        gc.fill = grid.fineColor

        gc.translate(grid.origin.x, grid.origin.y)
        when (grid) {
            is CartesianGrid -> {
                if(grid.rotation != 0.0)
                    gc.rotate(grid.rotation)
                val (minX, minY) = Point(0.0, 0.0)
                val (maxX, maxY) = Point(grid.width, grid.height)


                var p = Point(minX, minY)

                if (!grid.dotted) {
                    var coarse = 0
                    while (p.y <= maxY) {
                        val isCoarse = coarse % grid.multiplier == 0

                        if (grid.step.y >= 8 * gc.lineWidth) {
                            gc.stroke = if (isCoarse) grid.coarseColor else grid.fineColor
                            gc.strokeLine(minX, p.y, maxX, p.y)
                        }
                        ++coarse
                        p.y += grid.step.y
                    }
                    coarse = 0
                    while (p.x <= maxX) {

                        val isCoarse = coarse % grid.multiplier == 0

                        if (grid.step.x >= 8* gc.lineWidth) {
                            gc.stroke = if (isCoarse) grid.coarseColor else grid.fineColor
                            gc.strokeLine(p.x, minY, p.x, maxY)
                        }
                        ++coarse
                        p.x += grid.step.x
                    }
                } else {
                    while (p.y <= maxY) {
                        p.x = minX
                        while (p.x <= maxX) {
                            p.x += grid.step.x
                            if (grid.step.x >= 5 * gc.lineWidth) {
                                gc.fillRect(p.x - gc.lineWidth, p.y - gc.lineWidth, gc.lineWidth * 2, gc.lineWidth * 2)
                            }
                        }
                        p.y += grid.step.y
                    }
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

        gc.restore()
    }


}