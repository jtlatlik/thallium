package model.geom

data class Box(var p1: Point, var p2: Point) {

    constructor(p1: Point, width: Double, height: Double) : this(p1, p1 + Point(width, height))
    constructor(x: Double, y: Double, width: Double, height: Double) : this(Point(x, y), Point(x, y) + Point(width, height))

    val width: Double
        get() = p2.x - p1.x

    val height: Double
        get() = p2.y - p1.y


    fun getSize(): Point {
        return Point(width, height)
    }

    fun copy() = Box(p1.copy(), p2.copy())

    fun canonical(): Box {
        var canonicalBox = this.copy()

        if (p2.x < p1.x) {
            canonicalBox.p1.x = p2.x
            canonicalBox.p2.x = p1.x
        }
        if (p2.y < p1.y) {
            canonicalBox.p1.y = p2.y
            canonicalBox.p2.y = p1.y
        }
        return canonicalBox
    }

    infix fun intersectsWith(box: Box): Boolean {
        //two rectangles do not overlap when one is above/below, or to the left/right of the other rectangle.
        return p2.y >= box.p1.y && p1.y <= box.p2.y && p2.x >= box.p1.x && p1.x <= box.p2.x
    }

    fun contains(box: Box): Boolean {
        return p1.x <= box.p1.x && p1.y <= box.p1.y && p2.x >= box.p2.x && p2.y >= box.p2.y
    }

    fun contains(p: Point): Boolean {
        return p1.x <= p.x && p1.y <= p.y && p2.x >= p.x && p2.y >= p.y
    }

    override fun toString(): String {
        return "(${p1.x},${p1.y}|${p2.x},${p2.y})"
    }
}