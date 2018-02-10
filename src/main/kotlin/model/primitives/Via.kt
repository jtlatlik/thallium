package model.primitives

import model.geom.Point
import model.geom.Box
import model.geom.minus
import model.geom.plus
import model.nets.Net

class Via(override var center: Point, var radius: Double, var holeRadius: Double, net: Net? = null) : Primitive(net) {

    override fun accept(visitor: PrimitiveVisitor) {
        visitor.visitVia(this)
    }

    override fun isPointInside(point: Point): Boolean {
        return center.distTo(point) <= radius
    }

    override fun getBoundingBox(): Box {
        return Box(center - Point(radius, radius), center + Point(radius, radius))
    }
}