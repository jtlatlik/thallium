package controller

import model.pcb.PCB
import model.pcb.findTopLayer
import model.primitives.Via
import tornadofx.Controller
import view.EditorWindow
import java.io.File

class EditorController : Controller() {

    var pcb: PCB? = null

    val editorWindow: EditorWindow by inject()

}