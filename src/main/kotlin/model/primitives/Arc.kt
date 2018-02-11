package model.primitives

import model.geom.Box
import model.geom.Point
import model.geom.plus
import model.geom.times

class Arc(override var center: Point, var radius: Double, var startAngle: Double, var endAngle: Double, var width: Double = 0.0) : Primitive() {

    val start: Point
        get() {
            val radStart = Math.toRadians(startAngle)
            return Point(Math.cos(radStart), Math.sin(radStart)) * radius + center
        }

    val end: Point
        get() {
            val radEnd = Math.toRadians(endAngle)
            return Point(Math.cos(radEnd), Math.sin(radEnd)) * radius + center
        }

    override fun toString(): String {
        return "Arc: $start --> $end, radius: $radius, angle: $startAngle --> $endAngle"
    }

    override fun accept(visitor: PrimitiveVisitor) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isPointInside(point: Point): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBoundingBox(): Box {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}