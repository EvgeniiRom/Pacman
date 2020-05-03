public interface GameListener {
    void onScoreChange(int score);
    void onLiveChange(int lives);
    void onGameOver();
}
