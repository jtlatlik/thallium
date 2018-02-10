package model.primitives

import model.geom.Point
import model.geom.Box
import model.nets.Net

/**
 * Represents a rectangular shape with rounded corners.
 *
 * @property center the center point of the rectangle
 * @property size and [Point] denoting width and height af the rectangle
 * @property cornerRadius a value from 0.0 to 1.0 which controls the roundness of the corners where a value of 0.0
 * means not round at all and 1.0 means a circle if the rectangle would be square
 */
class Rectangle(override var center: Point,  var cornerRadius: Double, net: Net? = null) : Primitive(net) {

    override var rotation: Double
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override fun accept(visitor: PrimitiveVisitor) {
        visitor.visitRectangle(this)
    }

    override fun isPointInside(point: Point): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBoundingBox(): Box {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}