import java.awt.*;
import java.awt.geom.AffineTransform;

public class Person implements IWorldObject, IRenderObject{
    private Coord<Double> coord = new Coord<>(15d,0d);
    private int w = 10;
    private int h = 10;

    private double xV = 0f;
    private double yV = 0f;

    public enum Dir{
        UP,
        RIGHT,
        DOWN,
        LEFT;
    }

    @Override
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

    public void setPreferredDir(Dir dir){
        xV = 0f;
        yV = 0f;
        switch (dir) {
            case UP:
                yV = -10f;
                break;
            case RIGHT:
                xV = 10f;
                break;
            case DOWN:
                yV = 10f;
                break;
            case LEFT:
                xV = -10f;
                break;
        }
    }
}
