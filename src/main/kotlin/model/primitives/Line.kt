package model.primitives

import model.geom.*


data class Line(var start: Point, var end: Point, var width: Double) : Primitive() {

    override var center: Point
        get() = (start + end) * 0.5
        set(value) {
            val diff = value - center
            start += diff
            end += diff
        }

    fun angle() = Math.toDegrees(Math.atan2(end.y - start.y, end.x - start.x))

    override fun accept(visitor: PrimitiveVisitor) {
        visitor.visitLine(this)
    }

    override fun isPointInside(point: Point): Boolean {
        //val halfWidth = width / 2

        return getBoundingRect().contains(point)
    }

    override fun isContained(rectangle: Rectangle, touching: Boolean): Boolean {
        return false
    }

    override fun getBoundingRect(): Rectangle {
        val halfWidth = width / 2
        val offset = Point(halfWidth, halfWidth)
        return when (angle()) {
            in 0.0..90.0 -> Rectangle(start - offset, end + offset)
            in -90.0..0.0 -> Rectangle(Point(start.x, end.y) - offset, Point(end.x, start.y) + offset)
            in 90.0..180.0 -> Rectangle(Point(end.x, start.y) - offset, Point(start.x, end.y) + offset)
            in -180.00..-90.0 -> Rectangle(end - offset, start + offset)
            else -> throw ArithmeticException("impossible angle returned for line")
        }
    }

}
