import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;

public class Player extends Actor implements IRenderObject{
    private int w = 64;
    private int h = 64;

    private long timeOffset = 0l;
    private Animator sideAnimator;
    private Animator fontAnimator;
    private Animator rearAnimator;
    private Animator currentAnimator;

    public Player(PacContext pacContext, String id) throws IOException {
        super(pacContext, id);
        sideAnimator = new Animator("images/player/side/", 4);
        fontAnimator = new Animator("images/player/front/", 2);
        rearAnimator = new Animator("images/player/rear/", 2);
        fontAnimator.setFps(2);
        rearAnimator.setFps(2);
        currentAnimator = sideAnimator;
    }

    @Override
    public void update(long time) {
        timeOffset += time;
        super.update(time);
    }

    @Override
    public void render(Graphics2D g) {
        AffineTransform transform = g.getTransform();
        Coord<Double> currentCoord = getCurrentCoord();
        g.translate(currentCoord.x, currentCoord.y);
        switch (dir) {
            case UP:
                currentAnimator = rearAnimator;
                break;
            case RIGHT:
                currentAnimator = sideAnimator;
                break;
            case DOWN:
                currentAnimator = fontAnimator;
                break;
            case LEFT:
                currentAnimator = sideAnimator;
                g.transform(new AffineTransform(-1d, 0d, 0d, 1d, 0d, 0d));
                break;
        }
        g.drawImage(currentAnimator.getCurrentFrame(timeOffset),-w / 2, -h / 2, w, h, null);
        g.setTransform(transform);
    }
}
