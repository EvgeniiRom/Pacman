import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PauseForm {
    private JPanel mainPanel;
    private JButton resumeButton;
    private JButton restartButton;

    private PacContext pacContext;

    public PauseForm(PacContext pacContext) {
        this.pacContext = pacContext;
        mainPanel.setOpaque(false);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pacContext.getGameManager().restartGame();
            }
        });
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pacContext.getGameManager().resumeGame();
            }
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
