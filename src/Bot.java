import java.awt.*;
import java.awt.geom.AffineTransform;

public class Bot extends Actor implements IRenderObject{
    private Coord<Integer> currentBlock;

    private int w = 10;
    private int h = 10;

    public Bot(PacContext pacContext) {
        super(pacContext);
    }

    @Override
    public void start() {
        super.start();
        preferredDir = Dir.RIGHT;
        currentBlock = getBlockIndex();
    }

    @Override
    public void update(long time) {
        Coord<Integer> blockIndex = getBlockIndex();
        if(!blockIndex.equals(currentBlock)){
            System.out.println(currentBlock.x + " " + currentBlock.y + " " + blockIndex.x + " " + blockIndex.y);
            currentBlock = blockIndex;
            Dir nextDir = turnRight(preferredDir);
            System.out.println(preferredDir + " " +nextDir);
            Coord<Integer> nextBlockIndex = getNextBlockIndex(nextDir, currentBlock);
            int[][] field = pacContext.getPacField().getField();
            if(validBlockIndex(nextBlockIndex) && field[nextBlockIndex.y][nextBlockIndex.x]!=1){
                setPreferredDir(nextDir);
            }
        }
        super.update(time);
    }

    private Dir turnRight(Dir dir) {
        Dir nextDir = dir;
        switch (dir) {
            case UP:
                nextDir = Dir.RIGHT;
                break;
            case RIGHT:
                nextDir = Dir.DOWN;
                break;
            case DOWN:
                nextDir = Dir.LEFT;
                break;
            case LEFT:
                nextDir = Dir.UP;
                break;
        }
        return nextDir;
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.BLUE);
        AffineTransform transform = g.getTransform();
        g.translate(currentCoord.x, currentCoord.y);
        g.fillOval(-w / 2, -h / 2, w, h);
        g.setTransform(transform);
    }
}
