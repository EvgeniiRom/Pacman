import java.io.IOException;

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

        player = new Player(this);
        engine.addWorldObject("player", player);
        renderer.addRenderObject("player", player);

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

        Bot bot = new Bot(this);
        engine.addWorldObject("bot1", bot);
        renderer.addRenderObject("bot1", bot);
    }
}
