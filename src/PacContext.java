import sun.security.util.Length;

import java.io.IOException;
import java.util.List;

public class PacContext {
    private GameManager gameManager;
    private PacField pacField;
    private Player player;
    private int score = 0;
    private int blockSize = 40;
    private int lives = 3;

    public PacContext(GameManager gameManager) throws IOException {
        this.gameManager = gameManager;
        pacField = new PacField();
        pacField.read("field.ini");
        pacField.printField();
    }

    public void setDefaultValues(){
        score = 0;
        lives = 3;
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
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        if(lives==2){
            int a = 1;
        }
        this.lives = lives;
    }
}
