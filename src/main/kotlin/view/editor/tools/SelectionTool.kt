package view.editor.tools

import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import model.Primitive
import model.Via
import model.geom.Point
import model.geom.Rectangle
import view.editor.PCBEditor

class SelectionTool(editor: PCBEditor) : Tool(editor) {

    val viewport = editor.viewport

    val selection: MutableSet<Primitive> = mutableSetOf()
    var isSelecting: Boolean = false
    var selectionRectangle: Rectangle = Rectangle(Point(0.0, 0.0), Point(0.0, 0.0))

    override val onMousePressed = EventHandler<MouseEvent> {

        //first check if there is already some primitive under the cursor
        //if so, then don't set isSelecting
        val location = viewport.inverseTransform(Point(it.x, it.y))

        editor.layerView.layer!!.primitives.forEach { p ->
            if(p.isPointInside(location)) {
                if(selection.contains(p) && it.isControlDown)
                    selection.remove(p)
                else
                    selection.add(p)
                setChanged()
                notifyObservers()
                return@EventHandler
            }
        }
        if(!it.isControlDown)
            selection.clear()

        selectionRectangle.p1 = location
        selectionRectangle.p2 = location
        isSelecting = true

        setChanged()
        notifyObservers()
    }

    override val onMouseDragged = EventHandler<MouseEvent> {
        if (it.isPrimaryButtonDown) {
            var location = viewport.inverseTransform(Point(it.x, it.y))
            if(isSelecting) {
                selectionRectangle.p2 = location
                setChanged()
                notifyObservers()
            } else {
                //change tool
                editor.activeTool = DragTool(editor, selection)
            }
       }
    }

    override val onMouseReleased = EventHandler<MouseEvent> {
        if(isSelecting) {
            isSelecting = false
            val alsoSelectTouching = selectionRectangle.getSize().x < 0
            val canonicalRect = selectionRectangle.canonical()

            editor.layerView.layer!!.primitives.forEach {

                if (it.isContained(canonicalRect, alsoSelectTouching)) {
                    selection.add(it)
                }
            }

            setChanged()
            notifyObservers()
        }

    }

    override val onMouseMoved = EventHandler<MouseEvent> {
        val location = viewport.inverseTransform(Point(it.x, it.y))
        editor.scene.cursor = Cursor.DEFAULT
        if(!selection.isEmpty()) {
            selection.forEach {
                if(it.isPointInside(location)) {
                    editor.scene.cursor = Cursor.MOVE
                }
            }
        }
    }

    init {
        editor.onMousePressed = this.onMousePressed
        editor.onMouseReleased = this.onMouseReleased
        editor.onMouseDragged = this.onMouseDragged
        editor.onMouseMoved  = this.onMouseMoved
    }

}