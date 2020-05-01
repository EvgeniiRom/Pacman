import java.io.IOException;

public class PacContext {
    private Engine engine;
    private PacField pacField;
    private Renderer renderer;

    public Engine getEngine() {
        return engine;
    }

    public PacField getPacField() {
        return pacField;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public PacContext() throws IOException {
        renderer = new Renderer(this);
        engine = new Engine();
        pacField = new PacField();
        pacField.read("field.ini");
        pacField.printField();
    }
}
