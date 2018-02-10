package model.primitives

interface PrimitiveVisitor {

    fun visitLine(line: Line)
    fun visitPad(pad: Pad)
    fun visitVia(via: Via)
    fun visitPolygon(poly: Polygon)
    fun visitRectangle(rect: Rectangle)
    fun visitHole(hole: Hole)



}
