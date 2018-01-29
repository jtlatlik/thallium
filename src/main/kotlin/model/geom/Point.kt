package model.geom

data class Point(val x: Double, val y: Double)

operator fun Point.unaryMinus() = Point(-x,-y)
operator fun Point.plus(b: Point) = Point(x + b.x, y+ b.y)
operator fun Point.minus(b: Point) = Point(x - b.x, y - b.y)
operator fun Point.times(s: Double) = Point(s*x, s*y)