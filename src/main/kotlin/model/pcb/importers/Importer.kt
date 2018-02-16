package model.pcb.importers

import model.pcb.PCB
import java.io.File

interface Importer {

    fun load(file: File): PCB

    abstract val name: String
    abstract val description : String

    abstract val extensions: List<String>
}