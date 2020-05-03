import javafx.util.Pair;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Bot extends Actor implements IRenderObject {
    private Coord<Integer> currentBlock;
    private int w = 32;
    private int h = 32;
    private double eatDistance = 20d;
    private double defV = 90d;
    private boolean immortal = true;


    public Bot(PacContext pacContext, String id) {
        super(pacContext, id);
        setGateKey(true);
    }

    public void setImmortal(boolean immortal) {
        this.immortal = immortal;
        if(immortal){
            velosity = defV;
        }else {
            velosity = 50d;
        }
    }

    @Override
    public void start() {
        super.start();
        currentBlock = getBlockIndex();
        preferredDir = Dir.NONE;
        velosity = defV;
    }

    private int bfs(Coord<Integer> startBlock, Coord<Integer> target, boolean[][] used) {
        Queue<Pair<Coord<Integer>, Integer>> queue = new LinkedList<>();
        queue.add(new Pair<>(startBlock, 0));
        Dir[] dirs = Dir.values();
        int[][] field = pacContext.getPacField().getField();
        while (!queue.isEmpty()) {
            Pair<Coord<Integer>, Integer> next = queue.poll();
            Coord<Integer> blockIndex = next.getKey();
            if (!used[blockIndex.y][blockIndex.x]) {
                used[blockIndex.y][blockIndex.x] = true;
                if (target.equals(blockIndex)) {
                    return next.getValue();
                }
                for (Dir nextDir : dirs) {
                    Coord<Integer> nextBlockIndex = getNextBlockIndex(nextDir, blockIndex);
                    int nextBlock = pacContext.getPacField().getBlock(nextBlockIndex);
                    if (!nextBlockIndex.equals(blockIndex) &&
                            (nextBlock == 0 || nextBlock == 2 && gateKey) &&
                            !used[nextBlockIndex.y][nextBlockIndex.x]) {
                        queue.add(new Pair<>(nextBlockIndex, next.getValue() + 1));
                    }
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    private boolean[][] prepareKeyField() {
        PacField pacField = pacContext.getPacField();
        int height = pacField.getHeight();
        int width = pacField.getWidth();
        boolean[][] booleans = new boolean[height][width];
        return booleans;
    }

    private Dir getRandomDir(List<Dir> possibleDirList) {
        int size = possibleDirList.size();
        if (size > 0) {
            return possibleDirList.get(Math.abs(Randomizer.getInstance().getInteger()) % size);
        }
        return Dir.NONE;
    }

    private Dir getRandomDir() {
        List<Dir> possibleDirList = getPossibleDirList(getBlockIndex());
        return getRandomDir(possibleDirList);
    }

    @Override
    public void update(long time) {
        if(time==0){
            return;
        }

        Coord<Double> playerCoord = pacContext.getPlayer().getCurrentCoord();
        Coord<Double> coord = getCurrentCoord();
        double dx = playerCoord.x - coord.x;
        double dy = playerCoord.y - coord.y;
        if(Math.sqrt(dx*dx + dy*dy)<eatDistance){
            GameManager gameManager = pacContext.getGameManager();
            if(immortal) {
                gameManager.killPlayer();
            }else{
                //TODO respawn
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        gameManager.removeObject(getId());
                    }
                }).start();
            }
        }

        Coord<Integer> blockIndex = getBlockIndex();
        if (!blockIndex.equals(currentBlock) || preferredDir == Dir.NONE) {
            currentBlock = blockIndex;
            List<Dir> possibleDirList = getPossibleDirList(currentBlock);
            Dir reverseDir = getReverseDir(preferredDir);
            possibleDirList.remove(reverseDir);
            if (possibleDirList.size() > 0) {
                setPreferredDir(getRandomDir(possibleDirList));
            } else {
                setPreferredDir(reverseDir);
            }
        }
        super.update(time);
    }

    @Override
    public void render(Graphics2D g) {
        if(immortal) {
            g.setColor(Color.BLUE);
        }else{
            g.setColor(Color.MAGENTA);
        }
        AffineTransform transform = g.getTransform();
        Coord<Double> currentCoord = getCurrentCoord();
        g.translate(currentCoord.x, currentCoord.y);
        g.fillOval(-w / 2, -h / 2, w, h);
        g.setTransform(transform);
    }
}
