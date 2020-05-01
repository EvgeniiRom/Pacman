import javax.swing.*;
import java.awt.*;

public class GamePanel extends JComponent {
    private PacContext pacContext;

    public GamePanel(PacContext pacContext) {
        this.pacContext = pacContext;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Dimension size = getSize();
        Renderer renderer = pacContext.getRenderer();
        renderer.render((Graphics2D)g, size);
    }
}
