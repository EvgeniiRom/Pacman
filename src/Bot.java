import javafx.util.Pair;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Bot extends Actor implements IRenderObject {
    private Coord<Integer> currentBlock;

    private int w = 10;
    private int h = 10;

    public Bot(PacContext pacContext) {
        super(pacContext);
    }

    @Override
    public void start() {
        super.start();
        currentCoord = new Coord<>(35d, 15d);
        preferredDir = Dir.RIGHT;
        defV = 15d;
        currentBlock = getBlockIndex();
    }

    private int bfs(Coord<Integer> startBlock, Coord<Integer> target, boolean[][] used) {
        Queue<Pair<Coord<Integer>, Integer>> queue = new LinkedList<>();
        queue.add(new Pair<>(startBlock, 0));
        Dir[] dirs = Dir.values();
        int[][] field = pacContext.getPacField().getField();
        while (!queue.isEmpty()) {
            Pair<Coord<Integer>, Integer> next = queue.poll();
            Coord<Integer> block = next.getKey();
            if (!used[block.y][block.x]) {
                used[block.y][block.x] = true;
                if (target.equals(block)) {
                    return next.getValue();
                }
                for (Dir nextDir : dirs) {
                    Coord<Integer> nextBlockIndex = getNextBlockIndex(nextDir, block);
                    if (!nextBlockIndex.equals(block) &&
                            validBlockIndex(nextBlockIndex) &&
                            field[nextBlockIndex.y][nextBlockIndex.x] != 1 &&
                            !used[nextBlockIndex.y][nextBlockIndex.x]) {
                        queue.add(new Pair<>(nextBlockIndex, next.getValue() + 1));
                    }
                }
            }
        }
        return -1;
    }

    private boolean[][] prepareKeyField(){
        PacField pacField = pacContext.getPacField();
        int height = pacField.getHeight();
        int width = pacField.getWidth();
        boolean[][] booleans = new boolean[height][width];
        return booleans;
    }

    @Override
    public void update(long time) {
        Coord<Integer> blockIndex = getBlockIndex();
        List<Dir> possibleDirList = getPossibleDirList(blockIndex);
        Coord<Integer> playerBlock = pacContext.getPlayer().getBlockIndex();
        int minDepth = Integer.MAX_VALUE;
        Dir nextDir = Dir.NONE;
        for (Dir dir : possibleDirList) {
            boolean[][] used = prepareKeyField();
            Coord<Integer> nextBlockIndex = getNextBlockIndex(dir, blockIndex);
            int depth = bfs(nextBlockIndex, playerBlock, used);
            if(depth<minDepth){
                minDepth = depth;
                nextDir = dir;
            }
        }
        setPreferredDir(nextDir);

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
