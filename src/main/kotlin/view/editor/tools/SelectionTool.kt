package view.editor.tools

import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import model.adt.QuadTree
import model.primitives.Primitive
import model.geom.Point
import model.geom.Box
import view.editor.PCBEditor
import kotlin.system.measureNanoTime

class SelectionTool(editor: PCBEditor) : Tool(editor) {

    val viewport = editor.viewport

    val selection: MutableMap<Primitive, QuadTree<Primitive>> = mutableMapOf()
    var isSelecting: Boolean = false
    var selectionBox: Box = Box(Point(0.0, 0.0), Point(0.0, 0.0))

    override val onMousePressed = EventHandler<MouseEvent> {

        //first check if there is already some primitive under the cursor
        //if so, then don't set isSelecting
        val location = viewport.inverseTransform(Point(it.x, it.y))
        val viewBox = Box(viewport.inverseTransform(Point(0.0,0.0)), viewport.inverseTransform(Point(editor.width , editor.height)))

        editor.pcb?.let {pcb ->

            pcb.allPrimitives.forEach { qt ->

                qt.retrieve(viewBox).forEach { p ->
                    if (p.isPointInside(location)) {

                        if (selection.containsKey(p) && it.isControlDown)
                            selection.remove(p)
                        else
                            selection.put(p, qt)
                        setChanged()
                        notifyObservers()
                        return@EventHandler
                    }
                }
            }
        }


        if (!it.isControlDown)
            selection.clear()

        selectionBox.p1 = location
        selectionBox.p2 = location
        isSelecting = true

        setChanged()
        notifyObservers()
    }

    override val onMouseDragged = EventHandler<MouseEvent> {
        if (it.isPrimaryButtonDown) {
            var location = viewport.inverseTransform(Point(it.x, it.y))
            if (isSelecting) {
                selectionBox.p2 = location
                setChanged()
                notifyObservers()
            } else {
                //change tool
                editor.toolStack.push(this)
                editor.activeTool = DragTool(editor, selection)
            }
        }
    }

    override val onMouseReleased = EventHandler<MouseEvent> {
        if (isSelecting) {
            isSelecting = false
            val alsoSelectTouching = selectionBox.getSize().x < 0
            val canonicalBox = selectionBox.canonical()

            val time = measureNanoTime {
                editor.pcb?.let {

                    it.allPrimitives.forEach { qt ->
                        qt.retrieve(canonicalBox).forEach { p ->
                            if (canonicalBox.contains(p.getBoundingBox()))
                                selection.put(p, qt)
                        }
                    }
                }
            }

            //println("selection time: ${time/1000.0} µs")

            setChanged()
            notifyObservers()
        }

    }

    override val onMouseMoved = EventHandler<MouseEvent> {
        val location = viewport.inverseTransform(Point(it.x, it.y))


        editor.scene.cursor = Cursor.DEFAULT
        if (!selection.isEmpty()) {
            selection.keys.forEach {
                if (it.isPointInside(location)) {
                    editor.scene.cursor = Cursor.MOVE
                }
            }
        }
    }

    val onKeyPressed = EventHandler<KeyEvent> {
        println("keypressed")
        if(it.isControlDown && it.code == KeyCode.V)
        {
            println("i bims")
        }
    }

    init {
        refreshEventHandlers()
    }

    override fun refreshEventHandlers() {
        editor.onMousePressed = this.onMousePressed
        editor.onMouseReleased = this.onMouseReleased
        editor.onMouseDragged = this.onMouseDragged
        //editor.onMouseMoved = this.onMouseMoved
        editor.onKeyPressed = this.onKeyPressed
    }

}