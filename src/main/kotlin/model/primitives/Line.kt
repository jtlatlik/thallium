package model.primitives

import model.geom.Point

data class Line(
        val x1: Point,
        val x2: Point,
        val width: Int
): Primitive() {
    override fun draw() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}