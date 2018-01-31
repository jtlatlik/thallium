package model.geom

data class Point(var x: Double, var y: Double) {

        fun transform(scale: Double, translation: Point): Point {
            return this*scale + translation
        }

        fun inverseTransform(scale: Double, translation: Point) : Point {
            return (this - translation)/scale
        }
}

operator fun Point.unaryMinus() = Point(-x,-y)
operator fun Point.plus(b: Point) = Point(x + b.x, y+ b.y)
operator fun Point.minus(b: Point) = Point(x - b.x, y - b.y)
operator fun Point.times(s: Double) = Point(s*x, s*y)
operator fun Point.div(s: Double) = Point(x/s, y/s)

