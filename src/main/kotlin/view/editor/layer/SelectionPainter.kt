package view.editor.layer;

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import javafx.scene.shape.StrokeLineCap
import model.geom.Point
import model.geom.times
import model.primitives.*

class SelectionPainter(val gc: GraphicsContext) : PrimitiveVisitor {

    init {
        gc.stroke = Color.WHITE
        gc.fill = Color.web("rgba(73, 72, 62,0.5)")

        //gc.setLineDashes(0.02, 0.01)
    }

    override fun visitLine(line: Line) {
        val oldLineCap = gc.lineCap
        val oldLineWidth = gc.lineWidth
        val oldStroke = gc.stroke

        gc.lineCap = StrokeLineCap.ROUND
        gc.lineWidth = line.width
        gc.stroke = gc.fill
        gc.strokeLine(line.start.x, line.start.y, line.end.x, line.end.y)

        gc.lineCap = oldLineCap
        gc.lineWidth = oldLineWidth
        gc.stroke = oldStroke

        val off = line.normal * (line.width / 2)
        val angle = 90.0 - Math.toDegrees(line.angle)

        gc.strokeLine(line.start.x - off.x, line.start.y - off.y, line.end.x - off.x, line.end.y - off.y)
        gc.strokeLine(line.start.x + off.x, line.start.y + off.y, line.end.x + off.x, line.end.y + off.y)
        gc.strokeArc(line.start.x - line.width / 2, line.start.y - line.width / 2, line.width, line.width, angle, 180.0, ArcType.OPEN);
        gc.strokeArc(line.end.x - line.width / 2, line.end.y - line.width / 2, line.width, line.width, angle, -180.0, ArcType.OPEN);

    }

    override fun visitPolygon(poly: Polygon) {
        val xPoints = poly.vertices.map { it.x }.toDoubleArray()
        val yPoints = poly.vertices.map { it.y }.toDoubleArray()
        gc.fillPolygon(xPoints, yPoints, xPoints.size)
        gc.strokePolygon(xPoints, yPoints, xPoints.size)
    }

    override fun visitPad(pad: Pad) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitVia(via: Via) {

        val (x1, y1) = Point(via.center.x - via.radius, via.center.y - via.radius)
        val diameter = via.radius * 2
        gc.fillOval(x1, y1, diameter, diameter)
        gc.strokeOval(x1, y1, diameter, diameter)
    }

    override fun visitHole(hole: Hole) {
        gc.save()
        gc.translate(hole.center.x, hole.center.y)
        gc.rotate(hole.rotation)

        val pos = Point( - hole.radius - hole.length / 2,- hole.radius)
        val size = Point(hole.radius * 2 + hole.length,hole.radius * 2)
        val arcSize = Point(hole.radius * 2 * hole.type.cornerRadius,hole.radius * 2 * hole.type.cornerRadius)

        gc.fillRoundRect(pos.x, pos.y, size.x, size.y, arcSize.x, arcSize.y)
        gc.strokeRoundRect(pos.x, pos.y, size.x, size.y, arcSize.x, arcSize.y)
        gc.restore()
    }


    override fun visitRectangle(rect: Rectangle) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
