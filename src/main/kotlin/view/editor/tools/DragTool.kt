package view.editor.tools

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import model.adt.QuadTree
import model.geom.*
import model.primitives.Primitive
import model.primitives.Via
import view.editor.PCBEditor


class DragTool(editor: PCBEditor, val selection: MutableMap<Primitive, QuadTree<Primitive>>) : Tool(editor) {

    val centerOfMass = selection.keys.sumByPoint { it.center } / selection.size

    init {
        refreshEventHandlers()
    }

    override val onMouseDragged = EventHandler<MouseEvent> {
        val pos = editor.viewport.inverseTransform(Point(it.x, it.y))

        editor.pcb?.let {pcb ->

            val cursor = if(!it.isControlDown) pcb.grids.get(0).snap(pos) else pos
            selection.forEach { (p, qt) ->
                p.center = cursor
                qt.reinsert(p)
            }
        }

        editor.layerView.redraw()
        editor.toolView.redraw()

    }

    override val onMouseReleased = EventHandler<MouseEvent> {
        editor.activeTool = editor.toolStack.pop()
    }

    init {

    }

    override fun refreshEventHandlers() {
        editor.onMouseDragged = this.onMouseDragged
        editor.onMouseReleased = this.onMouseReleased

    }

}