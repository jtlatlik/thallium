package model

import sun.text.resources.ro.CollationData_ro
import java.awt.Color

enum class LayerMaterial {
    COPPER, PREPREG, CORE, SOLDER_MASK
}

abstract class Layer(val name: String) {
    var allowsComponentPlacement: Boolean = false
    abstract var color: Color
}

class PhysicalLayer(name: String, val material: LayerMaterial, val thickness: Int, override var color: Color = Color.RED) : Layer(name)

class AuxiliaryLayer(name: String) : Layer(name) {
    override var color = Color.BLACK
}