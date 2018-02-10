package model.primitives

import model.geom.Point
import model.geom.Box

class Pad(val basePrimitives: List<Primitive>) : Primitive() {

    override var center: Point
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        set(value) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.}
        }

    override var rotation: Double
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override fun accept(visitor: PrimitiveVisitor) {
        visitor.visitPad(this)
    }

    override fun isPointInside(point: Point): Boolean {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun getBoundingBox(): Box {
        var min = Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
        var max = Point(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)

        basePrimitives.forEach {
            val bb = it.getBoundingBox()
            min.x = minOf(min.x, bb.p1.x)
            min.y = minOf(min.y, bb.p1.y)
            max.x = maxOf(max.x, bb.p2.x)
            max.y = maxOf(max.y, bb.p2.y)
        }
        return Box(min, max)
    }
}