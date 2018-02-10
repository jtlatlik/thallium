package view.editor.layer

import javafx.geometry.VPos
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.text.TextAlignment
import model.geom.Point
import model.primitives.*

class PrimitivePainter(val gc: GraphicsContext) : PrimitiveVisitor {

    fun drawBoundingRectangle(p: Primitive) {
        gc.stroke = Color.WHITE
        p.getBoundingBox().let {
            gc.strokeRect(it.p1.x, it.p1.y, it.width, it.height)
        }
    }

    fun contrastColor(color: Color): Color {
        // Counting the perceptive luminance - human eye favors green color...
        val a: Double = 1 - (0.299 * color.red + 0.587 * color.green + 0.114 * color.blue) / 255

        val d = if (a < 0.5) 0 else 255

        return Color.rgb(d, d, d)
    }


    init {
        gc.lineCap = StrokeLineCap.ROUND
        gc.lineJoin = StrokeLineJoin.ROUND
        gc.textAlign = TextAlignment.CENTER
        gc.textBaseline = VPos.CENTER
    }

    override fun visitLine(line: Line) {
        gc.lineWidth = line.width
        gc.strokeLine(line.start.x, line.start.y, line.end.x, line.end.y)

//        if(line.net != null) {
//            gc.save()
//
//            gc.stroke = contrastColor(gc.stroke as Color)
//            gc.lineWidth = 1.0
//
//            val (tx,ty) = line.center()
//            gc.translate(tx,ty)
//            gc.rotate(line.angle())
//            gc.strokeText(line.net,0.0,0.0)
//            gc.restore()
//        }
    }

    override fun visitPad(pad: Pad) {


    }

    override fun visitVia(via: Via) {
        gc.save()
        val (x1, y1) = Point(via.center.x - via.radius, via.center.y - via.radius)
        val (x2, y2) = Point(via.center.x - via.holeRadius, via.center.y - via.holeRadius)
        val diameter = via.radius * 2
        val holeSize = via.holeRadius * 2
        gc.fill = Color.SILVER
        gc.fillOval(x1, y1, diameter, diameter)
        gc.fill = Color.CHOCOLATE
        gc.fillOval(x2, y2, holeSize, holeSize)


        via.net?.let {
            gc.lineWidth = 1.0
            gc.stroke = contrastColor(gc.fill as Color)
            gc.strokeText(it.name, via.center.x, via.center.y)
        }

        gc.restore()
    }

    override fun visitPolygon(poly: Polygon) {
        val xPoints = poly.vertices.map { it.x }.toDoubleArray()
        val yPoints = poly.vertices.map { it.y }.toDoubleArray()
        gc.fillPolygon(xPoints, yPoints, xPoints.size)
    }

    override fun visitHole(hole: Hole) {
        gc.save()
        gc.fill = Color.gray(0.1)
        gc.translate(hole.center.x, hole.center.y)
        gc.rotate(hole.rotation)

        val pos = Point( - hole.radius - hole.length / 2,- hole.radius)
        val size = Point(hole.radius * 2 + hole.length,hole.radius * 2)
        val arcSize = Point(hole.radius * 2 * hole.type.cornerRadius,hole.radius * 2 * hole.type.cornerRadius)

        gc.fillRoundRect(pos.x, pos.y, size.x, size.y, arcSize.x, arcSize.y)
        gc.restore()
    }

    override fun visitRectangle(rect: Rectangle) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}