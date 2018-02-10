package model.geom

data class Point(var x: Double, var y: Double) {
    
        fun distTo(b: Point): Double {
            val d = b - this
            return Math.sqrt(d.x*d.x + d.y*d.y)
        }

    fun normalized(): Point {
        val length = Math.sqrt(x*x + y*y)
        return Point(x/length, y/length)
    }
}

operator fun Point.unaryMinus() = Point(-x,-y)
operator fun Point.plus(b: Point) = Point(x + b.x, y+ b.y)
operator fun Point.minus(b: Point) = Point(x - b.x, y - b.y)
operator fun Point.times(s: Double) = Point(s*x, s*y)
operator fun Point.times(s: Int) = Point(s.toDouble()*x, s.toDouble()*y)
operator fun Point.div(s: Double) = Point(x/s, y/s)
operator fun Point.div(s: Int) = Point(x/s.toDouble(), y/s.toDouble())
//dot product for points
operator fun Point.times(p:Point): Double = x*p.x + y*p.y



/**
 * Returns the point sum of all values produced by [selector] function applied to each element in the collection.
 */
public inline fun <T> Iterable<T>.sumByPoint(selector: (T) -> Point): Point {
    var sum = Point(0.0,0.0)
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

public fun Iterable<Point>.minMax() : Pair<Point,Point> {
    var min = Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
    var max = Point(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)

    for(element in this) {
        min.x = minOf(min.x, element.x)
        min.y = minOf(min.y, element.y)
        max.x = maxOf(max.x, element.x)
        max.y = maxOf(max.y, element.y)
    }

    return Pair(min,max)
}