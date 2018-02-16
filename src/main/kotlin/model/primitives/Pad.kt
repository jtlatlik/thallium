package model.primitives

import model.geom.Point
import model.geom.Box
import model.geom.div
import model.geom.sumByPoint
import model.nets.Net
import model.pcb.AbstractLayer

class Pad(val basePrimitives: List<Primitive>,
        val name: String,
        override var rotation: Double,
        net: Net? = null) : Primitive(AbstractLayer.MULTIPLE_LAYERS, net) {

    override var center: Point
        get() {
            val centerOfMass = basePrimitives.sumByPoint { it.center } / basePrimitives.size
            return centerOfMass
        }
        set(value) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.}
        }

    override fun accept(visitor: PrimitiveVisitor) {
        visitor.visitPad(this)
    }

    override fun isPointInside(point: Point): Boolean {
        return basePrimitives.fold(false, { acc, it -> acc || it.isPointInside(point)})
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