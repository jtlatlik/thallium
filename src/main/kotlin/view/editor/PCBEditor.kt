package view.editor

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.input.Clipboard
import javafx.scene.input.DataFormat
import javafx.scene.layout.StackPane
import model.PCB
import model.geom.*

import tornadofx.add
import view.editor.layer.LayerView
import view.editor.layer.ToolView

import view.editor.tools.SelectionTool
import view.editor.tools.Tool
import java.nio.ByteBuffer
import java.util.*

class PCBEditor : StackPane() {

    var viewport: Viewport = Viewport()

    val toolStack = Stack<Tool>()

    var activeTool: Tool = SelectionTool(this)
        set(tool: Tool) {
            tool.refreshEventHandlers()
        }

    var pcb: PCB? = null

    val layerView = LayerView(this)
    val toolView = ToolView(this)

    init {

        setMinSize(0.0, 0.0) //this allows clipping

        viewport.widthProperty.bind(widthProperty())
        viewport.heightProperty.bind(heightProperty())

        requestFocus()

        add(layerView)
        add(toolView)

        onMouseEntered = EventHandler {
            toolView.setCrosshairVisibility(true)
        }

        onMouseExited = EventHandler {
            toolView.setCrosshairVisibility(false)
        }

        onScroll = EventHandler {

            if (it.isControlDown) {
                val scaleAdjust = if (it.deltaY < 0) 0.8 else 1.2
                val oldScale = viewport.getScale().x
                viewport *= scaleAdjust

                // the pan has to be adjusted so that the location under the mouse cursor is a fix point
                // in the view coordinate system
                viewport += (Point(it.x, it.y) - viewport.getPan()) / oldScale * (oldScale - viewport.getScale().x)
            } else {
                viewport += Point(it.deltaX, it.deltaY)
            }

        }


    }

    fun paste() {
        
    }


    fun fitView() {

        if (pcb == null)
            return

        val viewportSize = Point(width, height)

        val ar = width / height
        val grid = pcb?.grids?.get(0) as CartesianGrid

        val gridSize = Point(grid.width, grid.height)

        val p1 = grid.origin - grid.step
        val p2 = grid.origin + gridSize + grid.step

        val scale = if (ar <= 1.0) {
            viewportSize.x / (p2 - p1).x
        } else {
            viewportSize.y / (p2 - p1).y
        }
        val pan = -p1 * scale
        viewport.setScalePan(scale, pan)
    }

    fun refresh() {
        layerView.redraw()
        toolView.redraw()
    }

    fun setPCB(pcb: PCB) {
        this.pcb = pcb
        refresh()
    }

    companion object {
        val altiumDataFormat = DataFormat("Protel-PCB") //register altium clipboard data format
    }
}