package view.editor

import model.geom.Point
import java.util.*


class ViewportTransformation() : Observable() {
    private var mxx: Double = 1.0
    private var myy: Double = 1.0
    private var mxt: Double = 0.0
    private var myt: Double = 0.0

    fun setScale(scale: Double) {
        mxx = scale
        myy = scale
        setChanged()
        notifyObservers()
    }

    fun setPan(pan: Point) {
        mxt = pan.x
        myt = pan.y
        setChanged()
        notifyObservers()
    }

    fun setScalePan(scale: Double, pan: Point) {
        mxx = scale
        myy = scale
        mxt = pan.x
        myt = pan.y
        setChanged()
        notifyObservers()
    }

    fun getScale(): Point {
        return Point(mxx, myy)
    }

    fun getPan(): Point {
        return Point(mxt, myt)
    }

    fun transform(p:Point): Point {
        return Point(p.x * mxx + mxt, p.y * myy + myt)
    }

    fun inverseTransform(p:Point): Point {
        return Point((p.x - mxt)/mxx, (p.y - myt)/myy)
    }

    operator fun timesAssign(scale: Double) {
        mxx *= scale
        myy *= scale
        setChanged()
    }

    operator fun plusAssign(pan: Point) {
        mxt += pan.x
        myt += pan.y

        setChanged()
        notifyObservers()
    }
}

