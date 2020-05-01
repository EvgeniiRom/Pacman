import java.awt.*;
import java.awt.geom.AffineTransform;

public class Person implements IWorldObject{
    private Coord<Double> coord = new Coord<>(15d,0d);
    private int w = 10;
    private int h = 10;

    private double xV = 10f;
    private double yV = 0f;


    public void render(Graphics2D g){
        g.setColor(Color.RED);
        AffineTransform transform = g.getTransform();
        g.translate(coord.x, coord.y);
        g.fillOval(-w/2, -h/2, w, h);
        g.setTransform(transform);
    }

    @Override
    public void start() {
        coord = new Coord<>(15d,15d);
        w = 10;
        h = 10;
    }

    @Override
    public void update(long time) {
        coord.x += time*xV/1000d;
        coord.y += time*yV/1000d;
    }

    @Override
    public void finish() {

    }
}
