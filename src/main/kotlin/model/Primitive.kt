package model

import model.geom.Point
import model.geom.plus
import model.geom.times

interface PrimitiveVisitor {

    fun visitLine(line: Line)
    fun visitPad(pad: Pad)
    fun visitVia(via: Via)
}

abstract class Primitive(var net: String? =  null) {

    abstract fun accept(visitor: PrimitiveVisitor)
}

data class Line(var start: Point, var end: Point, var width: Double) : Primitive() {

    fun center() = (start + end)*0.5
    fun angle() = Math.toDegrees(Math.atan2(end.y - start.y, end.x - start.x))

    override fun accept(visitor: PrimitiveVisitor) {
        visitor.visitLine(this)
    }
}

data class Via(var center: Point, var diameter: Double, var holeSize: Double): Primitive() {
    override fun accept(visitor: PrimitiveVisitor) {
        visitor.visitVia(this)
    }
}

data class Pad(val center: Point, val width: Double, val height: Double) : Primitive() {
    override fun accept(visitor: PrimitiveVisitor) {
        visitor.visitPad(this)
    }
}
