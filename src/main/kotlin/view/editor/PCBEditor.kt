package view.editor

import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.layout.StackPane
import model.Layer
import model.Line
import model.geom.*

import tornadofx.add
import view.editor.layer.LayerView
import view.editor.layer.ToolView

import view.editor.ViewportTransformation.*
import view.editor.tools.SelectionTool
import view.editor.tools.Tool
import java.util.Observer

class PCBEditor : StackPane() {


    var viewport: ViewportTransformation = ViewportTransformation()
    var activeTool: Tool = SelectionTool(this)

    var grid: Grid = CartesianGrid(Point(0.0, 0.0), Point(0.5, 0.5), 50.0, 30.0)

    //var grid: Grid = PolarGrid(Point(0.0, 0.0), 5.0, 10.0, 100.0, 600.0)

    val layerView = LayerView(this)
    val toolView = ToolView(this)

    init {

        requestFocus()

        add(layerView)
        add(toolView)

        layerView.grid = this.grid


        onMouseEntered = EventHandler {
            toolView.setCrosshairVisibility(true)
        }

        onMouseExited = EventHandler {
            toolView.setCrosshairVisibility(false)
        }

        onScroll = EventHandler {
            if (it.isControlDown) {
                val scaleAdjust =if (it.deltaY < 0) 0.8 else 1.2
                val oldScale = viewport.getScale()
                viewport *= scaleAdjust

                // the pan has to be adjusted so that the location under the mouse cursor is a fix point
                // in the view coordinate system
                viewport += (Point(it.x, it.y) - viewport.getPan()) / oldScale * (oldScale - viewport.getScale())

            } else {
                viewport += Point(it.deltaX, it.deltaY)
            }

        }
    }

    fun fitView() {
        val viewportSize = Point(width, height)
        val grid = grid as CartesianGrid
        val gridSize = Point(grid.width, grid.height)

        val p1 = grid.origin - grid.step
        val p2 = grid.origin + gridSize + grid.step

        val scale = viewportSize.x / (p2 - p1).x

        val pan = -p1*scale
        viewport.setScalePan(scale, pan)
    }

    fun refresh() {
        layerView.redraw()
        toolView.redraw()
    }

    fun setPCB(layer: Layer) {
        layerView.layer = layer
        refresh()
    }
}