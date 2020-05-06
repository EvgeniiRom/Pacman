import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class GameManager {
    private Engine engine;
    private Renderer renderer;
    private GamePanel gamePanel;
    private PacContext pacContext;
    private Thread updatePanelThread = null;
    private boolean gameStarted = false;

    private Logger logger = Logger.getLogger(GameManager.class.getName());

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public GameManager() throws IOException {
        pacContext = new PacContext(this);
        renderer = new Renderer(pacContext);
        engine = new Engine();
        gamePanel = new GamePanel(pacContext, renderer);

    }

    private void createWorldObjects() throws IOException {
        Coord<Integer> playerBlock = pacContext.getPacField().getPlayer();
        Player player = new Player(pacContext, "player");
        player.setDefaultLocationToBlock(playerBlock);
        pacContext.setPlayer(player);
        engine.addWorldObject(player);
        renderer.addRenderObject(player);

        List<Coord<Integer>> bots = pacContext.getPacField().getBots();
        int botCount = bots.size();
        for (int i = 0; i < botCount; i++) {
            Coord<Integer> coord = bots.get(i);
            String id = "bot_" + i;
            try {
                Bot bot = new Bot(pacContext, id);
                bot.setDefaultLocationToBlock(coord);
                engine.addWorldObject(bot);
                renderer.addRenderObject(bot);
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
        }

        List<Coord<Integer>> sweets = pacContext.getPacField().getSweets();
        int blockSize = pacContext.getBlockSize();
        int sweetCount = sweets.size();
        for (int i = 0; i < sweetCount; i++) {
            Coord<Integer> coord = sweets.get(i);
            Coord<Double> sweetCoord = new Coord<>((coord.x + 0.5) * blockSize, (coord.y + 0.5) * blockSize);
            String id = "sweet_" + i;
            Sweet sweet = new Sweet(pacContext, sweetCoord, id);
            engine.addWorldObject(sweet);
            renderer.addRenderObject(sweet);
        }

        List<Coord<Integer>> boosts = pacContext.getPacField().getBoosts();
        int boostCount = boosts.size();
        for (int i = 0; i < boostCount; i++) {
            Coord<Integer> coord = boosts.get(i);
            Coord<Double> boostCoord = new Coord<>((coord.x + 0.5) * blockSize, (coord.y + 0.5) * blockSize);
            String id = "boost_" + i;
            Boost boost = new Boost(pacContext, boostCoord, id);
            engine.addWorldObject(boost);
            renderer.addRenderObject(boost);
        }

        ArrayList<Coord<Integer>> extraSweets = new ArrayList<>(sweets);
        extraSweets.addAll(boosts);
        int extraSweetOffset = sweetCount;
        for (int i = 0; i < extraSweets.size(); i++) {
            for (int j = i; j < extraSweets.size(); j++) {
                if (i != j) {
                    Coord<Integer> s1 = extraSweets.get(i);
                    Coord<Integer> s2 = extraSweets.get(j);
                    int dx = s2.x - s1.x;
                    int dy = s2.y - s1.y;
                    if (Math.abs(dx) + Math.abs(dy) == 1) {
                        Coord<Double> coord = new Coord<>((s1.x + 0.5 + dx * 0.5) * blockSize, (s1.y + 0.5 + dy * 0.5) * blockSize);
                        Sweet sweet = new Sweet(pacContext, coord, "sweet_" + extraSweetOffset++);
                        engine.addWorldObject(sweet);
                        renderer.addRenderObject(sweet);
                    }
                }
            }
        }

    }

    public PacContext getPacContext() {
        return pacContext;
    }

    public void restartGame() {
        engine.pause();
        removeAllObject();
        pacContext.setDefaultValues();
        try {
            createWorldObjects();
            engine.startObjects();
            startCountdown(new Runnable() {
                @Override
                public void run() {
                    engine.resume();
                    gamePanel.setScene(GamePanel.Scene.GAMING);
                }
            });
        } catch (IOException e) {
            logger.info("Create world objects failed: " + e.getMessage());
        }
    }

    public void startCountdown(Runnable runnable){
        gamePanel.setScene(GamePanel.Scene.MESSAGE);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            Integer offset = 3;
            @Override
            public void run() {
                if(offset>0){
                    gamePanel.setMessage(offset.toString());
                }
                if(offset == 0) {
                    gamePanel.setMessage("START");
                }
                if(offset < 0){
                    timer.cancel();
                    runnable.run();
                }
                offset--;
            }
        }, 0, 1000);
    }


    public void startGame() {
        startCountdown(new Runnable() {
            @Override
            public void run() {
                removeAllObject();
                pacContext.setDefaultValues();
                try {
                    createWorldObjects();
                    engine.startObjects();
                    if (!gameStarted) {
                        updatePanelThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    while (true) {
                                        Thread.sleep(10);
                                        gamePanel.repaint();
                                    }
                                } catch (InterruptedException e) {
                                    logger.info("game panel update thread interrupted");
                                }
                            }
                        });
                        updatePanelThread.start();
                        engine.start();
                        gameStarted = true;
                        gamePanel.setScene(GamePanel.Scene.GAMING);
                        logger.info("game started");
                    } else {
                        logger.info("game already started");
                    }
                } catch (IOException e) {
                    logger.info("Create world objects failed: " + e.getMessage());
                }
            }
        });
    }


    public void pauseGame() {
        engine.pause();
        gamePanel.setScene(GamePanel.Scene.PAUSE);
    }

    public void resumeGame() {
        engine.resume();
        gamePanel.setScene(GamePanel.Scene.GAMING);
    }

    public void stopGame() {
        if (gameStarted) {
            updatePanelThread.interrupt();
            engine.stop();
            gameStarted = false;
            gamePanel.setScene(GamePanel.Scene.MAIN_MENU);
            logger.info("game stopped");
        } else {
            logger.info("game is not started");
        }
    }

    public void removeObject(String id) {
        engine.removeWorldObject(id);
        renderer.removeRenderObject(id);
    }

    public void removeAllObject() {
        engine.removeAllWorldObjects();
        renderer.removeAllObjects();
    }

    public void incrementScore(int score) {
        int value = pacContext.getScore() + score;
        pacContext.setScore(value);
    }

    public void killPlayer() {
        engine.pause();
        int lives = pacContext.getLives();
        lives--;
        pacContext.setLives(lives);
        if (lives == 0) {
            gameOver();
        } else {
            engine.startObjects();
            engine.resume();
        }
    }

    private void gameOver() {
        gamePanel.setScene(GamePanel.Scene.GAME_OVER);
    }

    public void boost() {
        List<IWorldObject> bots = engine.getWorldObjectListByClass(Bot.class);
        for (IWorldObject object : bots) {
            Bot bot = (Bot) object;
            bot.deadly();
        }
    }

    private void nextLevel(){
        pacContext.setLevel(pacContext.getLevel()+1);
        engine.pause();
        removeAllObject();
        try {
            createWorldObjects();
            engine.startObjects();
            engine.resume();
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }

    public void checkSweets(){
        List<IWorldObject> sweets = engine.getWorldObjectListByClass(Sweet.class);
        List<IWorldObject> boosts = engine.getWorldObjectListByClass(Boost.class);
        if(sweets.size() + boosts.size()==0){
            nextLevel();
        }
    }
}
