package model.primitives

import model.geom.Bounded
import model.geom.Point
import model.geom.Rectangle

abstract class Primitive : Bounded {

    abstract var center: Point

    abstract fun accept(visitor: PrimitiveVisitor)

    abstract fun isPointInside(point: Point): Boolean

    abstract fun isContained(rectangle: Rectangle, touching: Boolean): Boolean

    override abstract fun getBoundingRect(): Rectangle
}
