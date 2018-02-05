package view.editor.tools

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import model.geom.*
import model.primitives.Primitive
import model.primitives.Via
import view.editor.PCBEditor



class DragTool(editor: PCBEditor, val selection: MutableSet<Primitive>, oldTool: Tool) : Tool(editor) {

    val centerOfMass = selection.sumByPoint { it.center } / selection.size

    init {
        refreshEventHandlers()
    }

    override val onMouseDragged = EventHandler<MouseEvent> {
        val pos = editor.viewport.inverseTransform(Point(it.x, it.y))

        editor.pcb?.let {
            val cursor = it.grids.get(0).snap(pos)
            selection.forEach {p ->
                p.center = cursor
            }
        }

        editor.layerView.redraw()
        editor.toolView.redraw()

    }

    override val onMouseReleased = EventHandler<MouseEvent> {
        editor.activeTool = oldTool
    }

    init {

    }

    override fun refreshEventHandlers() {
        editor.onMouseDragged = this.onMouseDragged
        editor.onMouseReleased = this.onMouseReleased

    }

}