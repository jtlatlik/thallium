package controller

import model.PCB
import model.findTopLayer
import model.geom.Point
import model.geom.plus
import model.primitives.Hole
import model.primitives.Line
import tornadofx.Controller
import view.EditorWindow
import java.io.File

class EditorController : Controller() {

    var pcb: PCB? = null

    val editorWindow: EditorWindow by inject()

    fun loadPCBFromAltiumFile(file: File) {
        this.pcb = PCB.loadFromAltiumFile(file)

        pcb?.let {
            with(it) {
                stackup.findTopLayer()?.let {
                    //val points = mutableListOf(Point(1.0, 1.0) + pcb.origin, Point(2.0, 0.0), Point(2.0, 2.0), Point(1.0, 2.0))
                    //it.primitives.add(Polygon(points))

                    val hole = Hole(Point(2.0, 2.0) + origin, Hole.HoleType.ROUND, 0.5, 1.0, 0.0, false)
                    val pad = Line(origin + Point(1.5, 2.0), origin + Point(2.5, 2.0), 1.6)

                    it.primitives.add(pad)
                    it.primitives.add(hole)
                }
                editorWindow.editor.setPCB(it)
                editorWindow.editor.fitView()
            }


        }

    }
}