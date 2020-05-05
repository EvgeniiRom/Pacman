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
    private Timer boostTimer = null;
    private List<GameListener> listenerList = new ArrayList<>();

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
    }

    public PacContext getPacContext() {
        return pacContext;
    }

    public void addGameListener(GameListener listener) {
        listenerList.add(listener);
    }

    private void fireScore(){
        for (GameListener gameListener : listenerList) {
            gameListener.onScoreChange(pacContext.getScore());
        }
    }

    private void fireLives(){
        for (GameListener gameListener : listenerList) {
            gameListener.onLiveChange(pacContext.getLives());
        }
    }

    public void restartGame() {
        engine.pause();
        removeAllObject();
        pacContext.setDefaultValues();
        fireLives();
        fireScore();
        try {
            createWorldObjects();
            engine.startObjects();
            engine.resume();
            gamePanel.setScene(GamePanel.Scene.GAMING);
        } catch (IOException e) {
            logger.info("Create world objects failed: " + e.getMessage());
        }
    }

    public void startGame() {
        removeAllObject();
        pacContext.setDefaultValues();
        fireLives();
        fireScore();
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
        fireScore();
    }

    public void killPlayer() {
        engine.pause();
        int lives = pacContext.getLives();
        lives--;
        pacContext.setLives(lives);
        fireLives();
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

    private void createBoostTimer() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                boostTimer = null;
                List<IWorldObject> bots = engine.getWorldObjectListByClass(Bot.class);
                for (IWorldObject object : bots) {
                    Bot bot = (Bot) object;
                    bot.setImmortal(true);
                }
            }
        };
        boostTimer = new Timer();
        boostTimer.schedule(timerTask, 5000);
    }

    public void boost() {
        if (boostTimer == null) {
            List<IWorldObject> bots = engine.getWorldObjectListByClass(Bot.class);
            for (IWorldObject object : bots) {
                Bot bot = (Bot) object;
                bot.setImmortal(false);
            }
        } else {
            boostTimer.cancel();
        }
        createBoostTimer();
    }
}
