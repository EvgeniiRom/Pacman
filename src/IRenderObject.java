import java.awt.*;

public interface IRenderObject {
    void render(Graphics2D g);
    String getId();
    int getZIndex();
}
