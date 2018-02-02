package model.geom

data class Rectangle(var p1: Point, var p2: Point) {

    fun getSize(): Point {
        return Point(p2.x - p1.x, p2.y - p1.y)
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
}