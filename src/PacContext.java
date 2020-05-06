import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PacContext {
    private GameManager gameManager;
    private List<ContextListener> listenerList = new ArrayList<>();
    private PacField pacField;
    private Player player;
    private int score = 0;
    private int blockSize = 40;
    private int lives = 3;
    private int level = 1;

    public PacContext(GameManager gameManager) throws IOException {
        this.gameManager = gameManager;
        pacField = new PacField();
        pacField.read("field.ini");
        pacField.printField();
    }

    public void setDefaultValues(){
        score = 0;
        lives = 3;
        level = 1;
        for (ContextListener contextListener : listenerList) {
            contextListener.onLevelChange(score);
            contextListener.onLevelChange(lives);
            contextListener.onLevelChange(level);
        }
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public PacField getPacField() {
        return pacField;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Actor getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        for (ContextListener contextListener : listenerList) {
            contextListener.onScoreChange(score);
        }
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
        for (ContextListener contextListener : listenerList) {
            contextListener.onLiveChange(lives);
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        for (ContextListener contextListener : listenerList) {
            contextListener.onLevelChange(level);
        }
    }

    public void addContextListener(ContextListener listener) {
        listenerList.add(listener);
    }
}
