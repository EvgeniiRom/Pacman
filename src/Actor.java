import java.util.ArrayList;
import java.util.List;

public abstract class Actor implements IWorldObject {
    protected PacContext pacContext;
    private double pathOffset = 0d;
    private Coord<Double> startCoord = new Coord<>(15d, 15d);
    private Coord<Double> targetCoord = new Coord<>(15d, 15d);
    private Coord<Double> currentCoord = new Coord<>(15d, 15d);
    private Coord<Double> preferredCoord = new Coord<>(15d, 15d);
    private String id;

    protected double velosity = 100d;
    public boolean gateKey = false;

    protected Dir dir = Dir.NONE;
    protected Dir preferredDir = Dir.NONE;

    public Actor(PacContext pacContext, String id) {
        this.pacContext = pacContext;
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public Coord<Double> getCurrentCoord() {
        return currentCoord;
    }

    public void setGateKey(boolean gateKey) {
        this.gateKey = gateKey;
    }

    public enum Dir {
        NONE,
        UP,
        RIGHT,
        DOWN,
        LEFT;
    }

    public Dir getReverseDir(Dir dir)
    {
        switch (dir) {
            case UP:
                return Dir.DOWN;
            case RIGHT:
                return Dir.LEFT;
            case DOWN:
                return Dir.UP;
            case LEFT:
                return Dir.RIGHT;
        }
        return Dir.NONE;
    }

    public void setPreferredLocationToBlock(Coord<Integer> blockIndex) {
        int blockSize = pacContext.getBlockSize();
        preferredCoord = new Coord<Double>(blockSize * (blockIndex.x + 0.5d), blockSize * (blockIndex.y + 0.5d));
    }

    @Override
    public void start() {
        currentCoord = preferredCoord.clone();
        startCoord = preferredCoord.clone();
        targetCoord = preferredCoord.clone();
        preferredDir = Dir.NONE;
        dir = Dir.NONE;
    }

    protected List<Dir> getPossibleDirList(Coord<Integer> blockIndex) {
        List<Dir> result = new ArrayList<>();
        Dir[] dirs = Dir.values();
        for (Dir dir : dirs) {
            Coord<Integer> nextBlockIndex = getNextBlockIndex(dir, blockIndex);
            int nextBlock = pacContext.getPacField().getBlock(nextBlockIndex);
            if (!nextBlockIndex.equals(blockIndex) && (nextBlock == 0 || nextBlock == 2 && gateKey)) {
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

    protected Coord<Integer> getNextBlockIndex(Dir dir, Coord<Integer> currentBlockIndex) {
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
        int blockSize = pacContext.getBlockSize();
        int block = pacContext.getPacField().getBlock(blockIndex);
        if (block == 0 || block == 2 && gateKey) {
            return new Coord<Double>(blockSize * (blockIndex.x + 0.5d), blockSize * (blockIndex.y + 0.5d));
        }
        return startCoord.clone();
    }

    @Override
    public void update(long time) {
        pathOffset += velosity * time / 1000d;
        double needGo = Math.max(Math.abs(targetCoord.x - startCoord.x), Math.abs(targetCoord.y - startCoord.y));
        double donePercent = pathOffset / needGo;

        currentCoord.x = startCoord.x + (targetCoord.x - startCoord.x) * donePercent;
        currentCoord.y = startCoord.y + (targetCoord.y - startCoord.y) * donePercent;

        if(preferredDir!=Dir.NONE && preferredDir == getReverseDir(dir)){
            Coord<Double> temp = targetCoord;
            targetCoord = startCoord;
            startCoord = temp;
            pathOffset = Math.max(Math.abs(currentCoord.x - startCoord.x), Math.abs(currentCoord.y - startCoord.y));
            dir = preferredDir;
        }

        if (pathOffset > needGo) {
            pathOffset = 0;
            startCoord = targetCoord.clone();
            currentCoord = targetCoord.clone();
            targetCoord = getNextCoord(preferredDir);
            if(targetCoord.equals(currentCoord)){
                preferredDir = Dir.NONE;
            }
            dir = preferredDir;
        }
    }

    @Override
    public void finish() {

    }

    public void setPreferredDir(Dir dir) {
        preferredDir = dir;
    }
}
