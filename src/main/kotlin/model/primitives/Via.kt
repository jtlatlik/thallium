package model.primitives

import model.geom.Point
import model.geom.Rectangle
import model.geom.minus
import model.geom.plus

data class Via(override var center: Point, var radius: Double, var holeRadius: Double) : Primitive() {

    override fun accept(visitor: PrimitiveVisitor) {
        visitor.visitVia(this)
    }

    override fun isPointInside(point: Point): Boolean {
        return center.distTo(point) <= radius
    }

    override fun isContained(rectangle: Rectangle, touching: Boolean): Boolean {
        //TODO implement touching behavior
        var topLeft = center - Point(radius, radius)
        var bottomRight = center + Point(radius, radius)

        val containedTopLeft: Boolean = topLeft.x >= rectangle.p1.x && topLeft.y >= rectangle.p1.y
        val containedBottomRight: Boolean = bottomRight.x <= rectangle.p2.x && bottomRight.y <= rectangle.p2.y
        return containedTopLeft && containedBottomRight
    }

    override fun getBoundingRect(): Rectangle {
        return Rectangle(center - Point(radius, radius), center + Point(radius, radius))
    }
}