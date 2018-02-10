package model.primitives

import model.geom.*

data class Polygon(val vertices: MutableList<Point>) : Primitive() {

    override var rotation: Double
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override var center: Point
        get() {
            return vertices.sumByPoint { it } / vertices.size
        }
        set(value) {
            val offset = value - center
            vertices.forEachIndexed { i, _ ->
                vertices[i] += offset
            }
        }

    override fun accept(visitor: PrimitiveVisitor) {
        visitor.visitPolygon(this)
    }

    override fun isPointInside(point: Point): Boolean {

        //check whether point lies in the bounding box of the primitive first
        if (!getBoundingBox().contains(point))
            return false

        //apply ray casting algorithm
        var inside = false
        var j = vertices.size - 1
        for (i in 0 until vertices.size) {

            if ((vertices[i].y > point.y) != (vertices[j].y > point.y) &&
                    (point.x < (vertices[j].x - vertices[i].x) * (point.y - vertices[i].y) / (vertices[j].y - vertices[i].y) + vertices[i].x)) {
                inside = !inside
            }
            j = i
        }
        return inside
    }

    override fun getBoundingBox(): Box {

        val (min,max) = vertices.minMax()
        return Box(min, max)
    }
}