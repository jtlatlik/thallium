package model.geom

import javafx.scene.paint.Color


abstract class Grid(open var origin: Point) {

    var coarseColor: Color = Color.WHITE
    var fineColor: Color = Color.GRAY

    var multiplier: Int = 10

    abstract fun snap(point: Point): Point

}

data class CartesianGrid(
        override var origin: Point,
        var step: Point,
        var width: Double,
        var height: Double) : Grid(origin) {

    override fun snap(point: Point): Point {

        val localPoint = point - origin
        val gridPoint = Point(
                Math.round(localPoint.x / step.x) * step.x,
                Math.round(localPoint.y / step.y) * step.y)
        return gridPoint + origin
    }
}

data class PolarGrid(
        override var origin: Point,
        var angularStep: Double,
        var radialStep: Double,
        var startRadius: Double,
        var stopRadius: Double,
        var startAngle: Double = 0.0,
        var stopAngle: Double = 90.0) : Grid(origin) {

    override fun snap(point: Point): Point {

        var localPoint = point - origin
        val r = Math.sqrt(localPoint.x*localPoint.x + localPoint.y*localPoint.y)
        val theta = Math.atan2(localPoint.y, localPoint.x)

        val snapR = Math.round(r / radialStep) * radialStep
        val snapTheta = Math.round(theta / Math.toRadians(angularStep)) * Math.toRadians(angularStep)

        val gridPoint = Point(
                snapR * Math.cos(snapTheta),
                snapR * Math.sin(snapTheta))
        return gridPoint + origin
    }
}