package model.geom

data class Rectangle(var p1: Point, var p2: Point) {

    constructor(p1: Point, width: Double, height: Double) : this(p1, p1 + Point(width, height))
    constructor(x: Double, y: Double, width: Double, height: Double) : this(Point(x, y), Point(x, y) + Point(width, height))

    val width: Double
        get() = p2.x - p1.x

    val height: Double
        get() = p2.y - p1.y


    fun getSize(): Point {
        return Point(width, height)
    }

    fun copy() = Rectangle(p1.copy(), p2.copy())

    fun canonical(): Rectangle {
        var canonicalRect = this.copy()

        if (p2.x < p1.x) {
            canonicalRect.p1.x = p2.x
            canonicalRect.p2.x = p1.x
        }
        if (p2.y < p1.y) {
            canonicalRect.p1.y = p2.y
            canonicalRect.p2.y = p1.y
        }
        return canonicalRect
    }

    fun overlapsWith(rect: Rectangle): Boolean {
        //two rectangles do not overlap when one is above/below, or to the left/right of the other rectangle.
        return p2.y >= rect.p1.y && p1.y <= rect.p2.y && p2.x >= rect.p1.x && p1.x <= rect.p2.x
    }

    fun contains(rect: Rectangle): Boolean {
        return p1.x <= rect.p1.x && p1.y <= rect.p1.y && p2.x >= rect.p2.x && p2.y >= rect.p2.y
    }

    fun contains(p: Point): Boolean {
        return p1.x <= p.x && p1.y <= p.y && p2.x >= p.x && p2.y >= p.y
    }

    override fun toString(): String {
        return "(${p1.x},${p1.y}|${p2.x},${p2.y})"
    }
}