package view

import javafx.event.EventHandler
import javafx.scene.layout.StackPane
import model.Layer
import model.Via
import model.geom.*
import tornadofx.add
import view.layer.LayerView
import view.layer.ToolView

class PCBEditor : StackPane() {

    var viewScale: Double = 1.0
    var viewPan: Point = Point(0.0, 0.0)

    //var grid: Grid = CartesianGrid(Point(0.0, 0.0), Point(10.0, 10.0), 1000.0, 500.0)
    var grid: Grid = PolarGrid(Point(0.0, 0.0), 5.0, 10.0, 100.0, 600.0)

    val layerView = LayerView()
    val toolView = ToolView()

    init {
        add(layerView)
        add(toolView)

        layerView.grid = this.grid

        layerView.widthProperty().bind(this.widthProperty())
        layerView.heightProperty().bind(this.heightProperty())
        toolView.widthProperty().bind(this.widthProperty())
        toolView.heightProperty().bind(this.heightProperty())

        onMouseMoved = EventHandler {
            val actualPos = Point(it.x, it.y).inverseTransform(viewScale, viewPan)

            toolView.crosshair = grid.snap(actualPos).transform(viewScale, viewPan)
            toolView.redraw()
        }

        onMouseClicked = EventHandler {
            layerView.layer?.primitives?.add(Via(toolView.crosshair.inverseTransform(viewScale, viewPan), 10.0, 4.0))
            layerView.redraw()
        }

        onMouseEntered = EventHandler {
            toolView.setCrosshairVisibility(true)
        }

        onMouseExited = EventHandler {
            toolView.setCrosshairVisibility(false)
        }

        onScroll = EventHandler {
            if (it.isControlDown) {
                val scaleAdjust =if (it.deltaY < 0) 0.8 else 1.2
                val oldScale = viewScale
                viewScale *= scaleAdjust

                // the pan has to be adjusted so that the location under the mouse cursor is a fix point
                // in the view coordinate system
                viewPan += (Point(it.x, it.y) - viewPan) / oldScale * (oldScale - viewScale)
                layerView.setTransform(viewScale, viewPan)

            } else {
                viewPan += Point(it.deltaX, it.deltaY)
                layerView.setTransform(viewScale, viewPan)
            }
            //also update crosshair position
            val actualPos = Point(it.x, it.y).inverseTransform(viewScale, viewPan)
            toolView.crosshair = grid.snap(actualPos).transform(viewScale, viewPan)
            toolView.redraw()
        }
    }

    fun refresh() {
        layerView.redraw()
        toolView.redraw()
    }

    fun setPCB(layer: Layer) {
        layerView.layer = layer
        refresh()
    }
}