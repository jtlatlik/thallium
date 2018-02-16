package view

import javafx.event.EventHandler
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.util.Callback
import model.pcb.Layer
import javafx.scene.input.DataFormat


class RowFactoryCallback : Callback<TableView<Layer>, TableRow<Layer>> {

    private val SERIALIZED_MIME_TYPE = DataFormat("application/x-java-serialized-object")

    override fun call(param: TableView<Layer>?): TableRow<Layer> {
        val row = TableRow<Layer>()

        row.onDragDetected = EventHandler {
            if (!row.isEmpty) {
                val db = row.startDragAndDrop(TransferMode.MOVE);
                db.setDragView(row.snapshot(null, null))
                val cc = ClipboardContent()
                cc.put(SERIALIZED_MIME_TYPE, row.index)
                db.setContent(cc)
                it.consume()
            }
        }

        row.onDragOver = EventHandler {
            val db = it.dragboard
            if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                val draggedRowIndex = db.getContent(SERIALIZED_MIME_TYPE) as Int
                if (row.index != draggedRowIndex) {
                    it.acceptTransferModes(*TransferMode.COPY_OR_MOVE)
                    it.consume()

                }
            }
        }

        row.onDragDropped = EventHandler {
            val tv = row.tableView
            val db = it.dragboard
            if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                val draggedIndex = db.getContent(SERIALIZED_MIME_TYPE) as Int
                val dropIndex = if (row.isEmpty) tv.items.size  else row.index

                val draggedLayer = tv.items.removeAt(draggedIndex)

                if(dropIndex >= tv.items.size) {
                    tv.items.add(draggedLayer)
                    tv.selectionModel.clearAndSelect(tv.items.size - 1)
                } else {
                    tv.items.add(dropIndex,draggedLayer)
                    tv.selectionModel.clearAndSelect(dropIndex)
                }

                it.isDropCompleted = true
                it.consume()
            }
        }

        return row
    }
}