import java.awt.*;
import java.awt.geom.AffineTransform;

public class Player extends Actor implements IRenderObject{
    private int w = 10;
    private int h = 10;

    public Player(PacContext pacContext) {
        super(pacContext);
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.RED);
        AffineTransform transform = g.getTransform();
        Coord<Double> currentCoord = getCurrentCoord();
        g.translate(currentCoord.x, currentCoord.y);
        g.fillOval(-w / 2, -h / 2, w, h);
        g.setTransform(transform);
    }
}
