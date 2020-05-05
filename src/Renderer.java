import javafx.collections.transformation.SortedList;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.List;

public class Renderer {
    private PacContext pacContext;
    private Map<String, IRenderObject> renderObjectMap = new HashMap<>();
    private List<IRenderObject> sortedSet = new ArrayList<>();
    private Comparator<IRenderObject> renderObjectComparator = new RenderObjectComparator();

    public Renderer(PacContext pacContext) {
        this.pacContext = pacContext;
    }

    public void addRenderObject(IRenderObject renderObject){
        synchronized (renderObjectMap){
            renderObjectMap.put(renderObject.getId(), renderObject);
            sortedSet.add(renderObject);
            sortedSet.sort(renderObjectComparator);
        }
    }

    public void removeRenderObject(String id){
        synchronized (renderObjectMap){
            sortedSet.remove(renderObjectMap.get(id));
            renderObjectMap.remove(id);
        }
    }

    public void removeAllObjects() {
        synchronized (renderObjectMap) {
            sortedSet.clear();
            renderObjectMap.clear();
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
            for (IRenderObject renderObject : sortedSet) {
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
                    g.setColor(new Color(0, 0, 0));
                }
                if (block == 1) {
                    g.setColor(new Color(0, 161, 212));
                }
                if (block == 2) {
                    g.setColor(new Color(212, 125, 77));
                }
                g.fillRect(blockSize*j, blockSize*i, blockSize, blockSize);
            }
        }
    }
}
