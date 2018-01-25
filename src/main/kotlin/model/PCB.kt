package model

import model.geom.Point

class PCB {

    val stackup: ArrayList<Layer> = ArrayList(8)

    var size: Point = Point(100.0,50.0) //default size is 100mm x 50mm

}