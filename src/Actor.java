import java.util.ArrayList;
import java.util.List;

public abstract class Actor implements IWorldObject{
    protected PacContext pacContext;
    private double pathOffset = 0d;
    private Coord<Double> startCoord = new Coord<>(15d, 15d);
    private Coord<Double> targetCoord = new Coord<>(15d, 15d);
    protected Coord<Double> currentCoord = new Coord<>(15d, 15d);
    protected double defV = 20d;

    protected Dir preferredDir = Dir.NONE;

    public Actor(PacContext pacContext) {
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
    public void start() {
        currentCoord = new Coord<>(15d, 15d);
    }

    protected List<Dir> getPossibleDirList(Coord<Integer> blockIndex){
        List<Dir> result = new ArrayList<>();
        int[][] field = pacContext.getPacField().getField();
        Dir[] dirs = Dir.values();
        for (Dir dir : dirs) {
            Coord<Integer> nextBlockIndex = getNextBlockIndex(dir, blockIndex);
            if(!nextBlockIndex.equals(blockIndex) && validBlockIndex(nextBlockIndex) && field[nextBlockIndex.y][nextBlockIndex.x]!=1){
                result.add(dir);
            }
        }
        return result;
    }

    protected Coord<Integer> getBlockIndex() {
        int blockSize = pacContext.getBlockSize();
        int x = (int) (currentCoord.x / blockSize);
        int y = (int) (currentCoord.y / blockSize);
        return new Coord<>(x, y);
    }

    protected boolean validBlockIndex(Coord<Integer> blockIndex) {
        PacField pacField = pacContext.getPacField();
        return blockIndex.x >= 0 && blockIndex.x < pacField.getWidth() && blockIndex.y >= 0 && blockIndex.y < pacField.getHeight();
    }

    protected Coord<Integer> getNextBlockIndex(Dir dir, Coord<Integer> currentBlockIndex){
        Coord<Integer> blockIndex = new Coord<>(currentBlockIndex.x, currentBlockIndex.y);
        switch (dir) {
            case UP:
                blockIndex.y--;
                break;
            case RIGHT:
                blockIndex.x++;
                break;
            case DOWN:
                blockIndex.y++;
                break;
            case LEFT:
                blockIndex.x--;
                break;
        }
        return blockIndex;
    }

    protected Coord<Double> getNextCoord(Dir dir) {
        Coord<Integer> blockIndex = getNextBlockIndex(preferredDir, getBlockIndex());
        int[][] field = pacContext.getPacField().getField();
        int blockSize = pacContext.getBlockSize();
        if (validBlockIndex(blockIndex) && field[blockIndex.y][blockIndex.x] != 1) {
            return new Coord<Double>(blockSize * (blockIndex.x + 0.5d), blockSize * (blockIndex.y + 0.5d));
        }
        return startCoord.clone();
    }

    @Override
    public void update(long time) {
        pathOffset += defV * time / 1000d;
        double needGo = Math.max(Math.abs(targetCoord.x - startCoord.x), Math.abs(targetCoord.y - startCoord.y));

        double donePercent = Math.abs(pathOffset) / needGo;
        currentCoord.x = startCoord.x + (targetCoord.x - startCoord.x) * donePercent;
        currentCoord.y = startCoord.y + (targetCoord.y - startCoord.y) * donePercent;

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
