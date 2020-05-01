import java.awt.*;
import java.awt.geom.AffineTransform;

public class Person implements IWorldObject, IRenderObject {
    private PacContext pacContext;
    private Coord<Double> coord = new Coord<>(15d, 15d);
    private int w = 10;
    private int h = 10;

    private double defV = 20f;
    private double xV = 0f;
    private double yV = 0f;

    private Dir dir = Dir.NONE;
    private Dir preferredDir = Dir.NONE;

    public Person(PacContext pacContext) {
        this.pacContext = pacContext;
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
        g.translate(coord.x, coord.y);
        g.fillOval(-w / 2, -h / 2, w, h);
        g.setTransform(transform);
    }

    @Override
    public void start() {
        coord = new Coord<>(15d, 15d);
        w = 10;
        h = 10;
    }

    private boolean crossCenter(long time){
        int blockSize = pacContext.getBlockSize();
        Coord<Double> nextCoord = new Coord<>(coord.x + time * xV / 1000d, coord.y + time * yV / 1000d);
        double before = 0d;
        double after = 0d;
        int halfBlock = blockSize / 2;
        switch (dir) {
            case UP:
                before = blockSize - (coord.y % blockSize);
                after = blockSize - (nextCoord.y % blockSize);
                break;
            case RIGHT:
                before = coord.x % blockSize;
                after = nextCoord.x % blockSize;
                break;
            case DOWN:
                before = coord.y % blockSize;
                after = nextCoord.y % blockSize;
                break;
            case LEFT:
                before = blockSize - (coord.x % blockSize);
                after = blockSize - (nextCoord.x % blockSize);
                break;
        }

        return before<halfBlock && after>halfBlock;
    }

    private int getNextBlock(Dir dir){
        int blockSize = pacContext.getBlockSize();
        int[][] field = pacContext.getPacField().getField();
        int j = (int)(coord.x / blockSize);
        int i = (int)(coord.y / blockSize);
        switch (dir) {
            case UP:
                return field[i-1][j];
            case RIGHT:
                return field[i][j+1];
            case DOWN:
                return field[i+1][j];
            case LEFT:
                return field[i][j-1];
        }
        return 1;
    }

    private void updateVelocity(){
        yV = 0f;
        xV = 0f;
        switch (dir) {
            case UP:
                yV = -defV;
                break;
            case RIGHT:
                xV = defV;
                break;
            case DOWN:
                yV = defV;
                break;
            case LEFT:
                xV = -defV;
                break;
        }
    }

    private void correctPosition(){
        int blockSize = pacContext.getBlockSize();
        coord.x = (double)(int)(coord.x/blockSize)*blockSize + blockSize/2;
        coord.y = (double)(int)(coord.y/blockSize)*blockSize + blockSize/2;
    }

    @Override
    public void update(long time) {
        if(dir==Dir.NONE||crossCenter(time)){
            if(dir!=preferredDir){
                correctPosition();
            }
            dir = preferredDir;
            updateVelocity();
            if(getNextBlock(dir)==1){
                dir = Dir.NONE;
                preferredDir = Dir.NONE;
                updateVelocity();
            }
        }

        coord.x += time*xV/1000d;
        coord.y += time*yV/1000d;
    }

    @Override
    public void finish() {

    }

    public void setPreferredDir(Dir dir) {
        preferredDir = dir;
    }
}
