import controller.EditorController
import javafx.scene.input.DataFormat
import javafx.stage.Stage
import tornadofx.App
import tornadofx.UIComponent
import view.EditorWindow
import view.StackupEditorView

class Application : App(EditorWindow::class) {


    override fun start(stage: Stage) {
        super.start(stage)

        stage.width = 800.0
        stage.height = 600.0
        stage.centerOnScreen()


    }
}