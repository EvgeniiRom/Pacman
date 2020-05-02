import javax.swing.*;
import java.awt.*;

public class GamePanel extends JComponent {
    private Renderer renderer;

    public GamePanel(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Dimension size = getSize();
        renderer.render((Graphics2D)g, size);
    }
}
