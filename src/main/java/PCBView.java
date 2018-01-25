import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import model.Layer;
import model.PCB;
import org.jetbrains.annotations.NotNull;
import tornadofx.UIComponent;

import java.util.ArrayList;

public class PCBView extends Pane {

    private ArrayList<LayerView> layers = new ArrayList<>();

    public PCBView(PCB pcb) {

        for(Layer l: pcb.getStackup()) {

        }
    }
}
