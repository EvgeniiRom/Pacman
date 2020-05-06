import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Bot extends Actor implements IRenderObject {
    private Coord<Integer> lastBlockIndex;
    private int w = 64;
    private int h = 64;
    private double eatDistance = 20d;

    private boolean immortal = true;
    private boolean dead = false;
    private boolean warning = false;

    private Animator mainAnimator;
    private Animator mortalAnimator;
    private BufferedImage deadImage;
    private long timeOffset = 0l;
    private int zIndex = 100;

    private long deadlyTime = 10000l;
    private long spawnTime = 5000l;
    private long warningStartTime = 7000l;

    private long deadlyStartTime = -deadlyTime;
    private boolean spawning = false;
    private long spawnStartTime = 0l;

    public Bot(PacContext pacContext, String id) throws IOException {
        super(pacContext, id);
        setGateKey(true);
        mainAnimator = new Animator("images/bot/main/", 4);
        mortalAnimator = new Animator("images/bot/mortal/", 4);
        deadImage = ImageIO.read(new File("images/bot/dead.png"));
    }

    private void updateVelosity() {
        velosity = 110d;
        if (!immortal) {
            velosity = 50d;
        }
        if (dead) {
            velosity = 200d;
        }
    }

    public void deadly() {
        if (!dead) {
            deadlyStartTime = timeOffset;
        }
    }

    @Override
    public void start() {
        super.start();
        deadlyStartTime = -deadlyTime;
        spawning = false;
        spawnStartTime = 0l;
        lastBlockIndex = getBlockIndex();
        preferredDir = Dir.NONE;
    }

    private int bfs(Coord<Integer> startBlock, Coord<Integer> target, boolean[][] used) {
        if (startBlock.equals(target)) {
            return 0;
        }
        Queue<Pair<Coord<Integer>, Integer>> queue = new LinkedList<>();
        queue.add(new Pair<>(startBlock, 0));
        Dir[] dirs = Dir.values();
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
                    int nextBlock = 1;
                    if (pacContext.getPacField().validBlockIndex(nextBlockIndex)) {
                        nextBlock = pacContext.getPacField().getBlock(nextBlockIndex);
                    }
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

    boolean meetPlayer() {
        Coord<Double> playerCoord = pacContext.getPlayer().getCurrentCoord();
        Coord<Double> coord = getCurrentCoord();
        double dx = playerCoord.x - coord.x;
        double dy = playerCoord.y - coord.y;
        return Math.sqrt(dx * dx + dy * dy) < eatDistance;
    }

    private void death() {
        pacContext.getGameManager().killBot();
        dead = true;
    }

    private void updateFlags() {
        long deadlyOffset = timeOffset - deadlyStartTime;
        immortal = deadlyOffset > deadlyTime;
        if (spawning) {
            if (timeOffset - spawnStartTime > spawnTime) {
                dead = false;
                gateKey = true;
                spawning = false;
                deadlyStartTime = -deadlyTime;
            }
        }
        warning = !immortal && deadlyOffset > warningStartTime && (deadlyOffset - warningStartTime) % 1000 < 500;
    }

    @Override
    public void update(long time) {
        if (time == 0) {
            return;
        }
        timeOffset += time;
        updateFlags();
        updateVelosity();
        GameManager gameManager = pacContext.getGameManager();
        Coord<Integer> blockIndex = getBlockIndex();
        if (meetPlayer()) {
            if (immortal) {
                gameManager.killPlayer();
            } else {
                if(!dead) {
                    death();
                }
            }
        }
        if (dead && gateKey) {
            goHome();
        } else {
            randomMotion();
        }
        lastBlockIndex = blockIndex;
        super.update(time);
    }

    private void goHome() {
        Coord<Integer> blockIndex = getBlockIndex();
        if (!blockIndex.equals(lastBlockIndex) || preferredDir == Dir.NONE) {
            List<Dir> possibleDirList = getPossibleDirList(blockIndex);
            int minPath = Integer.MAX_VALUE;
            Dir result = Dir.NONE;
            for (Dir possibleDir : possibleDirList) {
                int bfs = bfs(getNextBlockIndex(possibleDir, blockIndex), pacContext.getPacField().getBotHome(), prepareKeyField());
                if (bfs < minPath) {
                    minPath = bfs;
                    result = possibleDir;
                }
            }
            if (minPath == 0 && !spawning) {
                spawnStartTime = timeOffset;
                spawning = true;
                gateKey = false;
            }
            setPreferredDir(result);
        }
    }

    private void randomMotion() {
        Coord<Integer> blockIndex = getBlockIndex();
        if (!blockIndex.equals(lastBlockIndex) || preferredDir == Dir.NONE) {
            List<Dir> possibleDirList = getPossibleDirList(blockIndex);
            Dir reverseDir = getReverseDir(preferredDir);
            possibleDirList.remove(reverseDir);
            if (possibleDirList.size() > 0) {
                setPreferredDir(getRandomDir(possibleDirList));
            } else {
                setPreferredDir(reverseDir);
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        AffineTransform transform = g.getTransform();
        Coord<Double> currentCoord = getCurrentCoord();
        g.translate(currentCoord.x, currentCoord.y);
        if (dir.equals(Dir.LEFT) || dir.equals(Dir.DOWN)) {
            g.transform(new AffineTransform(-1d, 0d, 0d, 1d, 0d, 0d));
        }

        BufferedImage currentFrame = null;
        if (immortal) {
            currentFrame = mainAnimator.getCurrentFrame(timeOffset);
        } else {
            currentFrame = mortalAnimator.getCurrentFrame(timeOffset);
            if (warning) {
                currentFrame = mainAnimator.getCurrentFrame(timeOffset);
            }
        }
        if (dead) {
            currentFrame = deadImage;
        }

        g.drawImage(currentFrame, -w / 2, -h / 2, w, h, null);
        g.setTransform(transform);
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }
}
