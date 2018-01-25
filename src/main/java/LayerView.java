import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Affine;

public class LayerView extends Canvas {

    private void draw() {

        double width = getWidth();
        double height = getHeight();
        GraphicsContext gc = getGraphicsContext2D();

        gc.clearRect(0,0,width,height);


    }

    public LayerView(int width, int height) {

    }
}
