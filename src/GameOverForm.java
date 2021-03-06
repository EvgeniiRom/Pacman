import javax.swing.*;
import java.awt.event.ActionEvent;

public class GameOverForm implements ContextListener {
    private JButton restartButton;
    private JPanel mainPanel;
    private JLabel scoreLabel;

    private PacContext pacContext;

    public GameOverForm(PacContext pacContext) {
        this.pacContext = pacContext;
        mainPanel.setOpaque(false);
        pacContext.getGameManager().getPacContext().addContextListener(this);
        restartButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pacContext.getGameManager().restartGame();
            }
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    @Override
    public void onScoreChange(int score) {
        scoreLabel.setText("SCORE : " + String.valueOf(score));
    }

    @Override
    public void onLiveChange(int lives) {

    }

    @Override
    public void onLevelChange(int level) {

    }

    @Override
    public void onKillsChange(int kills) {

    }
}
