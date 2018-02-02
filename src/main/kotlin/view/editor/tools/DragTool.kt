package view.editor.tools

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import model.Primitive
import view.editor.PCBEditor

class DragTool(editor: PCBEditor, val selection: MutableSet<Primitive>):  Tool(editor) {

    override val onMouseDragged = EventHandler<MouseEvent> {
        println("passing over to me. $selection")
    }

    override val onMouseReleased = EventHandler<MouseEvent> {
        editor.activeTool = SelectionTool(editor)
    }

    init {
        editor.onMouseDragged = this.onMouseDragged
        editor.onMouseReleased = this.onMouseReleased

    }
}