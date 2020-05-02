import java.io.IOException;
import java.util.List;

public class PacContext {
    private Engine engine;
    private PacField pacField;
    private Renderer renderer;
    private Player player;

    private int blockSize = 10;

    public Engine getEngine() {
        return engine;
    }

    public PacField getPacField() {
        return pacField;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public Actor getPlayer() {
        return player;
    }

    public PacContext() throws IOException {
        renderer = new Renderer(this);
        engine = new Engine();
        pacField = new PacField();
        pacField.read("field.ini");
        pacField.printField();

        int[][] field = pacField.getField();
        int sweetOffset = 0;
        int fieldHeight = pacField.getHeight();
        int fieldWidth = pacField.getWidth();
        for (int i = 0; i < fieldHeight; i++) {
            for (int j = 0; j < fieldWidth; j++) {
                if (field[i][j] == 0) {
                    Coord<Double> sweetCoord = new Coord<>((j + 0.5) * blockSize, (i + 0.5) * blockSize);
                    String sweetId = "sweet_" + sweetOffset++;
                    Sweet sweet = new Sweet(this, sweetCoord, sweetId);
                    engine.addWorldObject(sweetId, sweet);
                    renderer.addRenderObject(sweetId, sweet);
                }
            }
        }

        List<Coord<Integer>> bots = pacField.getBots();
        int botCount = bots.size();
        for (int i = 0; i<botCount; i++) {
            Coord<Integer> coord = bots.get(i);
            Bot bot = new Bot(this);
            bot.setLocationToBlock(coord);
            String id = "bot_" + i;
            engine.addWorldObject(id, bot);
            renderer.addRenderObject(id, bot);
        }

        List<Coord<Integer>> sweets = pacField.getSweets();
        int sweetCount = sweets.size();
        for (int i = 0; i<sweetCount; i++) {
            Coord<Integer> coord = sweets.get(i);
            Coord<Double> sweetCoord = new Coord<>((coord.x + 0.5) * blockSize, (coord.y + 0.5) * blockSize);
            String id = "sweet_" + i;
            Sweet sweet = new Sweet(this, sweetCoord, id);
            engine.addWorldObject(id, sweet);
            renderer.addRenderObject(id, sweet);
        }

        Coord<Integer> playerBlock = pacField.getPlayer();
        player = new Player(this);
        player.setLocationToBlock(playerBlock);
        engine.addWorldObject("player", player);
        renderer.addRenderObject("player", player);
    }
}
