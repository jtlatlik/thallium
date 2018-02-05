package view.editor.layer

import javafx.geometry.VPos
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.text.TextAlignment
import model.primitives.Line
import model.primitives.Pad
import model.primitives.PrimitiveVisitor
import model.primitives.Via
import model.geom.Point

class PrimitivePainter(val gc: GraphicsContext) : PrimitiveVisitor {


    fun contrastColor(color: Color) : Color
    {
        // Counting the perceptive luminance - human eye favors green color...
        val a : Double = 1 - ( 0.299 * color.red + 0.587 * color.green + 0.114 * color.blue)/255

        val d = if (a < 0.5) 0 else 255

        return Color.rgb(d,d,d)
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitVia(via: Via) {
        gc.save()
        val (x1,y1) = Point(via.center.x - via.radius, via.center.y - via.radius)
        val (x2,y2) = Point(via.center.x - via.holeRadius, via.center.y - via.holeRadius)
        val diameter = via.radius * 2
        val holeSize = via.holeRadius * 2
        gc.fill = Color.SILVER
        gc.fillOval(x1, y1, diameter, diameter)
        gc.fill = Color.CHOCOLATE
        gc.fillOval(x2,y2, holeSize, holeSize)

//        if(via.net != null) {
//            gc.lineWidth = 1.0
//            gc.stroke = contrastColor(gc.fill as Color)
//            gc.strokeText(via.net, via.center.x, via.center.y)
//        }
        gc.restore()
    }

}