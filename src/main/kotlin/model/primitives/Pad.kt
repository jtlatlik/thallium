package model.primitives

import model.geom.Point
import model.geom.Rectangle
import model.geom.plus
import model.geom.times


data class Pad(override var center: Point, val width: Double, val height: Double) : Primitive() {

    override fun accept(visitor: PrimitiveVisitor) {
        visitor.visitPad(this)
    }

    override fun isPointInside(point: Point): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isContained(rectangle: Rectangle, touching: Boolean): Boolean {
        return false
    }

    override fun getBoundingRect(): Rectangle {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}