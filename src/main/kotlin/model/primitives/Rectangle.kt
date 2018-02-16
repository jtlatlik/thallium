package model.primitives

import model.geom.*
import model.nets.Net
import model.pcb.AbstractLayer

/**
 * Represents a rectangular shape with rounded corners.
 *
 * @property center the center point of the rectangle
 * @property size a [Point] denoting width and height af the rectangle
 * @property cornerRadius a value from 0.0 to 1.0 which controls the roundness of the corners where a value of 0.0
 * means not round at all and 1.0 means a circle if the rectangle would be square
 */
class Rectangle(layer: AbstractLayer,
        override var center: Point,
        var size: Point,
        var cornerRadius: Double,
        override var rotation: Double = 0.0,
        net: Net? = null) : Primitive(layer, net) {


    override fun accept(visitor: PrimitiveVisitor) {
        visitor.visitRectangle(this)
    }

    override fun isPointInside(point: Point): Boolean {
        return false //TODO implement me
    }

    override fun getBoundingBox(): Box {
         //TODO respect rotation of the rectangle
        val p1 = center - (Point(size.x, size.y) / 2)

        return Box(p1, p1 + size)
    }

}