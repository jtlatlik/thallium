package model

import com.sun.org.apache.xpath.internal.operations.Bool
import model.geom.*

interface PrimitiveVisitor {

    fun visitLine(line: Line)
    fun visitPad(pad: Pad)
    fun visitVia(via: Via)
}

abstract class Primitive(var net: String? = null) {

    abstract fun isPointInside(point: Point): Boolean
    abstract fun isContained(rectangle: Rectangle, touching: Boolean = false): Boolean

    abstract fun accept(visitor: PrimitiveVisitor)
}

data class Line(var start: Point, var end: Point, var width: Double) : Primitive() {

    fun center() = (start + end) * 0.5
    fun angle() = Math.toDegrees(Math.atan2(end.y - start.y, end.x - start.x))

    override fun accept(visitor: PrimitiveVisitor) {
        visitor.visitLine(this)
    }

    override fun isPointInside(point: Point): Boolean {
        val widthFromCenter = width / 2

        return false
    }

    override fun isContained(rectangle: Rectangle, touching: Boolean): Boolean {
        return false
    }

}

data class Via(var center: Point, var radius: Double, var holeRadius: Double) : Primitive() {
    override fun accept(visitor: PrimitiveVisitor) {
        visitor.visitVia(this)
    }

    override fun isPointInside(point: Point): Boolean {
        return center.distTo(point) <= radius
    }


    override fun isContained(rectangle: Rectangle, touching: Boolean): Boolean {
        //TODO implement touching behavior
        var topLeft = center- Point(radius, radius)
        var bottomRight = center+ Point(radius, radius)

        val containedTopLeft: Boolean = topLeft.x >= rectangle.p1.x && topLeft.y >= rectangle.p1.y
        val containedBottomRight: Boolean = bottomRight.x <= rectangle.p2.x && bottomRight.y <= rectangle.p2.y
        return containedTopLeft && containedBottomRight
    }
}

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
}
