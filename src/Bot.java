import javafx.util.Pair;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.security.spec.RSAOtherPrimeInfo;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Bot extends Actor implements IRenderObject {
    private int w = 10;
    private int h = 10;

    public Bot(PacContext pacContext) {
        super(pacContext);
    }

    @Override
    public void start() {
        super.start();
        preferredDir = Dir.RIGHT;
        defV = 15d;
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
                            field[nextBlockIndex.y][nextBlockIndex.x] == 0 &&
                            !used[nextBlockIndex.y][nextBlockIndex.x]) {
                        queue.add(new Pair<>(nextBlockIndex, next.getValue() + 1));
                    }
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    private boolean[][] prepareKeyField(){
        PacField pacField = pacContext.getPacField();
        int height = pacField.getHeight();
        int width = pacField.getWidth();
        boolean[][] booleans = new boolean[height][width];
        return booleans;
    }

    private Dir getRandomDir(){
        List<Dir> possibleDirList = getPossibleDirList(getBlockIndex());
        int size = possibleDirList.size();
        if(size > 0){
            return possibleDirList.get(Math.abs(Randomizer.getInstance().getInteger()) % size);
        }
        return Dir.NONE;
    }

    @Override
    public void update(long time) {
        Coord<Integer> blockIndex = getBlockIndex();
        List<Dir> possibleDirList = getPossibleDirList(blockIndex);
        Coord<Integer> playerBlock = pacContext.getPlayer().getBlockIndex();
        int minDepth = Integer.MAX_VALUE;
        Dir nextDir = Dir.NONE;
        if(!playerBlock.equals(blockIndex)){
            for (Dir dir : possibleDirList) {
                boolean[][] used = prepareKeyField();
                Coord<Integer> nextBlockIndex = getNextBlockIndex(dir, blockIndex);
                int depth = bfs(nextBlockIndex, playerBlock, used);
                if(depth<minDepth){
                    minDepth = depth;
                    nextDir = dir;
                }
            }
        }
        if(nextDir==Dir.NONE){
            nextDir = getRandomDir();
        }
        setPreferredDir(nextDir);
        super.update(time);
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.BLUE);
        AffineTransform transform = g.getTransform();
        Coord<Double> currentCoord = getCurrentCoord();
        g.translate(currentCoord.x, currentCoord.y);
        g.fillOval(-w / 2, -h / 2, w, h);
        g.setTransform(transform);
    }
}
