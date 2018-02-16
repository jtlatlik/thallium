package model.primitives

import com.sun.javafx.geom.transform.Affine2D
import model.geom.*
import model.pcb.AbstractLayer

/**
 * This represents a hole through the whole PCB. Usually, this is not used directly, but rather as a base primitive
 * for a Pad (which e.g. can combine a Hole with a Circle to represent a pad with annular ring).
 * A hole can be round, rectangular, or a slot (basically a rounded rectangle with corner radius equal to 100%)
 *
 * @property center the center location of the hole
 * @property radius the radius of the hole
 * @property length the linear dimension the hole. Values greater than 0.0 mean an oblong hole
 * @property rotation the rotation in degrees of a slot or a rectangular hole
 * @property plated whether to hole is plated or not
 *
 */
class Hole(layer: AbstractLayer,
           override var center: Point,
           var type: HoleType,
           var radius: Double,
           var length: Double,
           override var rotation: Double,
           plated: Boolean = true) : Primitive(layer) {

    enum class HoleType(val cornerRadius: Double) {
        ROUND(1.0), RECTANGLE(0.0), SLOT(1.0)
    }

    override fun accept(visitor: PrimitiveVisitor) {
        visitor.visitHole(this)
    }

    override fun isPointInside(point: Point): Boolean {
        //TODO this does not yet take rotation and hole length into account :(
        //one could do this in a similar manner as in Line.isPointInside
        return center.distTo(point) <= radius
    }

    override fun getBoundingBox(): Box {
        //TODO this needs additional work for rounded rectangles (slots)
        val angle = Math.toRadians(rotation)

        val rotatePoint = { point: Point, angle: Double ->
            Point(Math.cos(angle) * point.x - Math.sin(angle) * point.y,
                    Math.sin(angle) * point.x + Math.cos(angle) * point.y)
        }

        val xOff = radius+length/2
        val yOff =  radius
        val p1 =rotatePoint(Point(-xOff, -yOff), angle)
        val p2 =rotatePoint(Point(+xOff, -yOff), angle)
        val p3 =rotatePoint(Point(-xOff, +yOff), angle)
        val p4 =rotatePoint(Point(+xOff, +yOff), angle)
        val rotatedCorners = listOf(p1, p2, p3, p4)
        val (min,max) = rotatedCorners.minMax()

        return Box(center +min, center+ max)
    }
}