import javafx.util.Pair;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.List;

public class Bot extends Actor implements IRenderObject {
    private Coord<Integer> lastBlockIndex;
    private int w = 32;
    private int h = 32;
    private double eatDistance = 20d;
    private double defV = 90d;
    private boolean immortal = true;
    private boolean dead = false;


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
        lastBlockIndex = getBlockIndex();
        preferredDir = Dir.NONE;
        velosity = defV;
    }

    private int bfs(Coord<Integer> startBlock, Coord<Integer> target, boolean[][] used) {
        if(startBlock.equals(target)){
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

    boolean meetPlayer(){
        Coord<Double> playerCoord = pacContext.getPlayer().getCurrentCoord();
        Coord<Double> coord = getCurrentCoord();
        double dx = playerCoord.x - coord.x;
        double dy = playerCoord.y - coord.y;
        return Math.sqrt(dx*dx + dy*dy)<eatDistance;
    }

    private void death(){
        dead = true;
        velosity = 200d;
    }

    public void initRespawn(){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                dead = false;
                immortal = true;
                gateKey = true;
                velosity = defV;
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 5000);
    }

    @Override
    public void update(long time) {
        if(time==0){
            return;
        }
        GameManager gameManager = pacContext.getGameManager();
        Coord<Integer> blockIndex = getBlockIndex();
        if(meetPlayer()){
            if(immortal) {
                gameManager.killPlayer();
            }else {
                death();
            }
        }
        if(dead && gateKey){
            goHome();
        }else{
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
            if(minPath==0){
                initRespawn();
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
        g.setColor(Color.BLUE);
        if(!immortal) {
            g.setColor(Color.MAGENTA);
        }
        if(dead){
            g.setColor(Color.WHITE);
        }
        AffineTransform transform = g.getTransform();
        Coord<Double> currentCoord = getCurrentCoord();
        g.translate(currentCoord.x, currentCoord.y);
        g.fillOval(-w / 2, -h / 2, w, h);
        g.setTransform(transform);
    }
}
