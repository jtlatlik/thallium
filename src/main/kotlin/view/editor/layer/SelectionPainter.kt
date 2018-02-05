package view.editor.layer;

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import model.primitives.Line
import model.primitives.Pad
import model.primitives.PrimitiveVisitor
import model.primitives.Via
import model.geom.Point

class SelectionPainter(val gc: GraphicsContext) : PrimitiveVisitor {

    init {
        gc.stroke = Color.WHITE
        gc.fill = Color.web("rgba(73, 72, 62, 0.5)")
    }

    override fun visitLine(line: Line) {
        gc.save()
        gc.lineWidth = line.width
        gc.stroke = gc.fill
        gc.strokeLine(line.start.x, line.start.y, line.end.x, line.end.y)
        gc.restore()

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


}
