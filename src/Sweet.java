import java.awt.*;

public class Sweet implements IWorldObject, IRenderObject {
    private PacContext pacContext;
    private int r = 4;
    private Coord<Double> coord = new Coord<>(0d,0d);
    private String id;
    private double eatDistance = 16d;
    private int zIndex = 50;

    public Sweet(PacContext pacContext, Coord<Double> coord, String id) {
        this.pacContext = pacContext;
        this.coord = coord;
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillOval(coord.x.intValue()-r, coord.y.intValue()-r, r*2, r*2);
    }

    @Override
    public void start() {

    }

    @Override
    public void update(long time) {
        Coord<Double> playerCoord = pacContext.getPlayer().getCurrentCoord();
        double dx = playerCoord.x - coord.x;
        double dy = playerCoord.y - coord.y;
        if(Math.sqrt(dx*dx + dy*dy)<eatDistance){
            GameManager gameManager = pacContext.getGameManager();
            gameManager.incrementScore(10);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    gameManager.removeObject(id);
                    gameManager.checkSweets();
                }
            }).start();
        }
    }

    @Override
    public void finish() {

    }
}
