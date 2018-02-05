package model.primitives

import model.geom.Point
import model.geom.Rectangle


data class Pad(val center: Point, val width: Double, val height: Double) : Primitive() {
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