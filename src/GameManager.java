import java.io.IOException;
import java.util.List;
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
        gamePanel = new GamePanel(renderer);

        Coord<Integer> playerBlock = pacContext.getPacField().getPlayer();
        Player player = new Player(pacContext);
        player.setLocationToBlock(playerBlock);
        engine.addWorldObject("player", player);
        renderer.addRenderObject("player", player);
        pacContext.setPlayer(player);

        List<Coord<Integer>> bots = pacContext.getPacField().getBots();
        int botCount = bots.size();
        for (int i = 0; i<botCount; i++) {
            Coord<Integer> coord = bots.get(i);
            Bot bot = new Bot(pacContext);
            bot.setLocationToBlock(coord);
            String id = "bot_" + i;
            engine.addWorldObject(id, bot);
            renderer.addRenderObject(id, bot);
        }

        List<Coord<Integer>> sweets = pacContext.getPacField().getSweets();
        int blockSize = pacContext.getBlockSize();
        int sweetCount = sweets.size();
        for (int i = 0; i<sweetCount; i++) {
            Coord<Integer> coord = sweets.get(i);
            Coord<Double> sweetCoord = new Coord<>((coord.x + 0.5) * blockSize, (coord.y + 0.5) * blockSize);
            String id = "sweet_" + i;
            Sweet sweet = new Sweet(pacContext, sweetCoord, id);
            engine.addWorldObject(id, sweet);
            renderer.addRenderObject(id, sweet);
        }
    }

    public PacContext getPacContext() {
        return pacContext;
    }

    public void startGame(){
        if(!gameStarted) {
            updatePanelThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            Thread.sleep(10);
                            gamePanel.repaint();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            updatePanelThread.start();
            engine.start();
            gameStarted = true;
            logger.info("game started");
        }else{
            logger.info("game already started");
        }
    }

    public void stopGame(){
        if(gameStarted) {
            updatePanelThread.interrupt();
            engine.stop();
            gameStarted = false;
            logger.info("game stopped");
        }else{
            logger.info("game is not started");
        }
    }

    public void removeObject(String id){
        engine.removeWorldObject(id);
        renderer.removeRenderObject(id);
    }
}
