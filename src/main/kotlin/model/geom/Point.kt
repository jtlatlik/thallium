package model.geom

data class Point(var x: Double, var y: Double) {
    
        fun distTo(b: Point): Double {
            val d = b - this
            return Math.sqrt(d.x*d.x + d.y*d.y)
        }
}

operator fun Point.unaryMinus() = Point(-x,-y)
operator fun Point.plus(b: Point) = Point(x + b.x, y+ b.y)
operator fun Point.minus(b: Point) = Point(x - b.x, y - b.y)
operator fun Point.times(s: Double) = Point(s*x, s*y)
operator fun Point.times(s: Int) = Point(s.toDouble()*x, s.toDouble()*y)
operator fun Point.div(s: Double) = Point(x/s, y/s)
operator fun Point.div(s: Int) = Point(x/s.toDouble(), y/s.toDouble())

