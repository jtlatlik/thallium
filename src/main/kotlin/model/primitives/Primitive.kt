package model.primitives

import model.geom.Bounded
import model.geom.Point
import model.geom.Box
import model.nets.Net

/**
 * Abstract base class for all primitives on a PCB. This class defines common functions and properties for all derived
 * primitives, for instance the primitive's net, center point as well as some geometrical functions
 *
 * @property net The net which this primitive belongs to
 */
abstract class Primitive(var net: Net? = null) : Bounded {

    /**
     * @property center the point which represents the primitive's center. The center is used, for instance, as a
     * reference point, when the primitive is moved around
     */
    abstract var center: Point

    /**
     * @property rotation an angle (in degrees) which represents a rotation of the primitive around the center point
     */
    open var rotation: Double
        get() = 0.0
        set(_) {}

    /**
     * Implements the visitor pattern for primitives
     *
     * @param visitor an instance of [PrimitiveVisitor] which implements a visit function for each primitive
     */
    abstract fun accept(visitor: PrimitiveVisitor)

    /**
     * @return true, iff the given [point] lies within the primitive
     */
    abstract fun isPointInside(point: Point): Boolean

    /**
     * @return a rectangle which spans a bounding box around this primitive
     */
    override abstract fun getBoundingBox(): Box
}
