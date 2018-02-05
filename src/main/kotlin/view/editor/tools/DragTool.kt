package view.editor.tools

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import model.geom.Point
import model.geom.div
import model.geom.plus
import model.primitives.Primitive
import model.primitives.Via
import view.editor.PCBEditor

class DragTool(editor: PCBEditor, val selection: MutableSet<Primitive>, oldTool: Tool) : Tool(editor) {


    override val onMouseDragged = EventHandler<MouseEvent> {

        var center = Point(0.0, 0.0)

        selection.forEach { p ->
            when (p) {
                is Via -> {
                    center += p.center
                }
            }
        }

        center /= selection.size

        
        selection.forEach { p ->
            when (p) {
                is Via -> {
                    p.center = center
                }
            }
        }

        editor.layerView.redraw()
        editor.toolView.redraw()

    }

    override val onMouseReleased = EventHandler<MouseEvent> {
        editor.activeTool = oldTool
    }

    init {
        refreshEventHandlers()
    }

    override fun refreshEventHandlers() {
        editor.onMouseDragged = this.onMouseDragged
        editor.onMouseReleased = this.onMouseReleased

    }

}