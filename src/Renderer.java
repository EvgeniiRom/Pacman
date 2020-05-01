import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Renderer {
    private PacContext pacContext;
    private Map<String, IRenderObject> renderObjectMap = new HashMap<>();
    private final int BLOCK_SIZE = 10;

    public Renderer(PacContext pacContext) {
        this.pacContext = pacContext;
    }

    public void addRenderObject(String id, IRenderObject renderObject){
        renderObjectMap.put(id, renderObject);
    }

    public void render(Graphics2D g, Dimension size){
        PacField pacField = pacContext.getPacField();
        int fieldHeight = pacField.getHeight();
        int fieldWidth = pacField.getWidth();
        double yScale = size.getHeight()/(fieldHeight*BLOCK_SIZE);
        double xScale = size.getWidth()/(fieldWidth*BLOCK_SIZE);

        AffineTransform transform = g.getTransform();
        g.scale(xScale, yScale);
        renderField(g, pacField.getField());
        Collection<IRenderObject> renderObjectArray = renderObjectMap.values();
        for (IRenderObject renderObject : renderObjectArray) {
            renderObject.render(g);
        }
        g.setTransform(transform);
    }

    private void renderField(Graphics2D g, int[][] field) {
        int rowCount = field.length;
        for (int i = 0; i < rowCount; i++) {
            int[] row = field[i];
            int columnCount = row.length;
            for (int j = 0; j < columnCount; j++) {
                int block = row[j];
                if (block == 0) {
                    g.setColor(new Color(46, 178, 90));
                }
                if (block == 1) {
                    g.setColor(new Color(0, 0, 0));
                }
                g.fillRect(BLOCK_SIZE*j, BLOCK_SIZE*i, BLOCK_SIZE, BLOCK_SIZE);
            }
        }
    }
}
