package model.primitives

import model.geom.Point
import model.geom.Rectangle

data class Polygon(val points: List<Point>) : Primitive() {
    override var center: Point
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override fun accept(visitor: PrimitiveVisitor) {
        visitor.visitPolygon(this)
    }

    override fun isPointInside(point: Point): Boolean {

        //return true if point is a convex combination of the polygons vertices
        return false
    }

    override fun isContained(rectangle: Rectangle, touching: Boolean): Boolean {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBoundingRect(): Rectangle {

        var min = Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
        var max = Point(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)

        points.forEach {
            min.x = minOf(min.x, it.x)
            max.x = maxOf(max.x, it.x)
            min.y = minOf(min.y, it.y)
            max.y= maxOf(max.y, it.y)
        }
        return Rectangle(min,max)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}