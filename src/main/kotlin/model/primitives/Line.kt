package model.primitives

import model.geom.*
import model.nets.Net


class Line(var start: Point, var end: Point, var width: Double, net: Net? = null) : Primitive(net) {

    override var center: Point
        get() = (start + end) * 0.5
        set(value) {
            val diff = value - center
            start += diff
            end += diff
        }

    val angle: Double
        get() = Math.atan2(end.y - start.y, end.x - start.x)

    val normal: Point
        get() = Point(start.y - end.y, end.x - start.x).normalized()

    override fun accept(visitor: PrimitiveVisitor) {
        visitor.visitLine(this)
    }

    override fun isPointInside(point: Point): Boolean {
        //trivial case first
        if (!getBoundingBox().contains(point))
            return false

        val halfWidth = width / 2
        //three things to check:
        // start point and end point circles (1,2)
        if (start.distTo(point) < halfWidth || end.distTo(point) < halfWidth)
            return true

        //(rotated) rectangle between start and end (3)
        //compute the normal of the line
        val offset = normal * halfWidth

        //calculate corner points of the rectangle
        // a------------b
        // | s        e |
        // d------------+
        val a = start - offset
        val b = end - offset
        val d = start + offset

        //project point onto a-d and a-b (dot product)
        val AP = point - a
        val AB = b - a
        val AD = d - a
        val projAB = AP * AB
        val projAD = AP * AD

        //if projAB lies between a and b and projAD lies betwwen a and d then point is inside the rectangle
        return projAB in 0.0..(AB * AB) && projAD in 0.0..(AD * AD)
    }

    override fun getBoundingBox(): Box {
        val halfWidth = width / 2
        val offset = Point(halfWidth, halfWidth)
        return when (Math.toDegrees(angle)) {
            in 0.0..90.0 -> Box(start - offset, end + offset)
            in -90.0..0.0 -> Box(Point(start.x, end.y) - offset, Point(end.x, start.y) + offset)
            in 90.0..180.0 -> Box(Point(end.x, start.y) - offset, Point(start.x, end.y) + offset)
            in -180.00..-90.0 -> Box(end - offset, start + offset)
            else -> throw ArithmeticException("impossible angle returned for line")
        }
    }

}
