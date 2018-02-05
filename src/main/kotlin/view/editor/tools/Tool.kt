package view.editor.tools

import javafx.event.Event
import javafx.event.EventDispatchChain
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.input.MouseEvent
import view.editor.PCBEditor
import java.util.*

abstract class Tool(val editor: PCBEditor) : Observable() {

    open val onMousePressed = EventHandler<MouseEvent> {}
    open val onMouseMoved = EventHandler<MouseEvent> {}
    open val onMouseReleased = EventHandler<MouseEvent> {}
    open val onMouseDragged = EventHandler<MouseEvent> {}

    abstract fun refreshEventHandlers()

}