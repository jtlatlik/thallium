import controller.EditorController
import javafx.scene.image.Image
import javafx.scene.input.DataFormat
import javafx.stage.Stage
import model.geom.Point
import model.geom.plus
import model.pcb.findTopLayer
import model.pcb.importers.AltiumImporter
import model.primitives.Rectangle
import tornadofx.App
import tornadofx.UIComponent
import view.EditorWindow
import view.StackupEditorView
import tornadofx.*
import java.io.File

class Application : App(EditorWindow::class) {

    val editorController: EditorController by inject()

    override fun start(stage: Stage) {
        super.start(stage)

        stage.width = 800.0
        stage.height = 600.0

        stage.centerOnScreen()

        editorController.pcb = AltiumImporter.load(File("PCB1.PcbDoc"))

        editorController.pcb?.run {
            stackup.findTopLayer()?.let {
                //val points = mutableListOf(Point(1.0, 1.0) + pcb.origin, Point(2.0, 0.0), Point(2.0, 2.0), Point(1.0, 2.0))
                //it.primitives.add(Polygon(points))

//                    val hole = Hole(Point(2.0, 2.0) + origin, Hole.HoleType.ROUND, 0.5, 1.0, 0.0, false)
//                    val pad = Line(origin + Point(1.5, 2.0), origin + Point(2.5, 2.0), 1.6)
//
//                    it.primitives.add(pad)
//                    it.primitives.add(hole)

                //println("pcb origin is at: $origin")
                //it.primitives.add(Rectangle(origin + Point(2.0,2.0), Point(4.0, 2.0), 1.0, 45.0))
                //it.primitives.add(model.primitives.Via(origin, 0.2, 0.1))
            }
            editorController.editorWindow.editor.setPCB(this)
            editorController.editorWindow.editor.fitView()
        }

    }
}