import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Renderer {
    private PacContext pacContext;
    private Map<String, IRenderObject> renderObjectMap = new HashMap<>();

    public Renderer(PacContext pacContext) {
        this.pacContext = pacContext;
    }

    public void addRenderObject(String id, IRenderObject renderObject){
        synchronized (renderObjectMap){
            renderObjectMap.put(id, renderObject);
        }
    }

    public void removeRenderObject(String id){
        synchronized (renderObjectMap){
            renderObjectMap.remove(id);
        }
    }

    public void render(Graphics2D g, Dimension size){
        int blockSize = pacContext.getBlockSize();
        PacField pacField = pacContext.getPacField();
        int fieldHeight = pacField.getHeight();
        int fieldWidth = pacField.getWidth();
        double yScale = size.getHeight()/(fieldHeight*blockSize);
        double xScale = size.getWidth()/(fieldWidth*blockSize);

        AffineTransform transform = g.getTransform();
        g.scale(xScale, yScale);
        renderField(g, pacField.getField());
        synchronized (renderObjectMap) {
            Collection<IRenderObject> renderObjectArray = renderObjectMap.values();
            for (IRenderObject renderObject : renderObjectArray) {
                renderObject.render(g);
            }
        }
        g.setTransform(transform);
    }

    private void renderField(Graphics2D g, int[][] field) {
        int blockSize = pacContext.getBlockSize();
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
                if (block == 2) {
                    g.setColor(new Color(0, 161, 212));
                }
                g.fillRect(blockSize*j, blockSize*i, blockSize, blockSize);
            }
        }
    }
}
