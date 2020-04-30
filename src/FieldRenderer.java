import java.awt.*;

public class FieldRenderer {
    public static void renderField(Graphics2D g, double blockWidth, double blockHeight, int[][] field) {
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
                g.fillRect((int)(blockWidth*j), (int)(blockHeight*i), (int) blockWidth+1, (int) blockHeight+1);
            }
        }
    }
}
