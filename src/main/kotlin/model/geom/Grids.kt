package model.geom

import javafx.scene.paint.Color

/**
 * An abstract base class for any actual grid type
 *
 * @param origin the origin of the grid
 */
abstract class Grid(open var origin: Point) {

    /**
     * The multiplier which sets the ratio between coarse and fine grid lines
     */
    var multiplier: Int = 10

    /**
     * The color for coarse grid lines
     */
    var coarseColor: Color = Color.LIGHTGRAY

    /**
     * The color for fine grid lines
     */
    var fineColor: Color = Color.GRAY

    /**
     * Whether the grid is drawn as dots or solid lines
     */
    var dotted: Boolean = false

    /**
     * Snaps a given point to a point on the grid line intersections
     *
     * @param point an arbitrary point
     * @return a point snapped to the nearest grid line intersection
     */
    abstract fun snap(point: Point): Point

}

/**
 * A rectangular cartesian grid of given size and location
 *
 * @property origin the origin of the grid
 * @property step the resolution of the grid
 * @property width width of the grid
 * @property height height of the grid
 * @property rotation the grid's rotation around the origin
 */
data class CartesianGrid(
        override var origin: Point,
        var step: Point,
        var width: Double,
        var height: Double,
        var rotation: Double = 0.0) : Grid(origin) {

    override fun snap(point: Point): Point {
        //compute local point in rotated coordinate system
        val localPoint = (point - origin).rotate(-rotation)

        //snap local point to grid point
        val gridPoint = Point(
                Math.round(localPoint.x / step.x) * step.x,
                Math.round(localPoint.y / step.y) * step.y)

        //undo the transformation
        return gridPoint.rotate(rotation) + origin
    }
}

/**
 * A polar coordinate grid
 *
 * @property origin the origin of the grid
 * @property angularStep the angular resolution of the grid
 * @property radialStep the radial resolution of the grid
 * @property startRadius first radius from which lines will be drawn
 * @property stopRadius the last radius for which lines will be drawn
 * @property startAngle the first angle from which lines will be drawn
 * @property stopAngle the last angle from which lines will be drawn
 */
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
        val r = Math.sqrt(localPoint.x * localPoint.x + localPoint.y * localPoint.y)
        val theta = Math.atan2(localPoint.y, localPoint.x)

        val snapR = Math.round(r / radialStep) * radialStep
        val snapTheta = Math.round(theta / Math.toRadians(angularStep)) * Math.toRadians(angularStep)

        val gridPoint = Point(
                snapR * Math.cos(snapTheta),
                snapR * Math.sin(snapTheta))
        return gridPoint + origin
    }
}