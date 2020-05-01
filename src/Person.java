import java.awt.*;
import java.awt.geom.AffineTransform;

public class Person implements IWorldObject, IRenderObject {
    private PacContext pacContext;
    private double pathOffset = 0d;
    private Coord<Double> startCoord = new Coord<>(15d, 15d);
    private Coord<Double> targetCoord = new Coord<>(15d, 15d);
    private Coord<Double> currentCoord = new Coord<>(15d, 15d);
    private int w = 10;
    private int h = 10;

    private double defV = 20f;

    private Dir preferredDir = Dir.NONE;

    public Person(PacContext pacContext) {
        this.pacContext = pacContext;
    }

    public Coord<Double> getCurrentCoord() {
        return currentCoord;
    }

    public enum Dir {
        NONE,
        UP,
        RIGHT,
        DOWN,
        LEFT;
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.RED);
        AffineTransform transform = g.getTransform();
        g.translate(currentCoord.x, currentCoord.y);
        g.fillOval(-w / 2, -h / 2, w, h);
        g.setTransform(transform);
    }

    @Override
    public void start() {
        currentCoord = new Coord<>(15d, 15d);
        w = 10;
        h = 10;
    }

    private Coord<Double> getNextCoord(Dir dir) {
        int blockSize = pacContext.getBlockSize();
        int[][] field = pacContext.getPacField().getField();
        int j = (int) (startCoord.x / blockSize);
        int i = (int) (startCoord.y / blockSize);
        switch (dir) {
            case UP:
                i--;
                break;
            case RIGHT:
                j++;
                break;
            case DOWN:
                i++;
                break;
            case LEFT:
                j--;
                break;
        }
        if(field[i][j]!=1){
            return new Coord<Double>(blockSize*(j+0.5d), blockSize*(i+0.5d));
        }
        return startCoord.clone();
    }

    @Override
    public void update(long time) {
        pathOffset += defV * time / 1000d;
        double needGo = Math.max(Math.abs(targetCoord.x - startCoord.x), Math.abs(targetCoord.y - startCoord.y));

        double donePercent = Math.abs(pathOffset) / needGo;
        currentCoord.x = startCoord.x + (targetCoord.x - startCoord.x)*donePercent;
        currentCoord.y = startCoord.y + (targetCoord.y - startCoord.y)*donePercent;

        if (Math.abs(pathOffset) > needGo) {
            pathOffset = 0;
            startCoord = targetCoord.clone();
            currentCoord = targetCoord.clone();
            targetCoord = getNextCoord(preferredDir);
        }
    }

    @Override
    public void finish() {

    }

    public void setPreferredDir(Dir dir) {
        preferredDir = dir;
    }
}
