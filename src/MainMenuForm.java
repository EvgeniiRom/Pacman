import javax.swing.*;
import java.awt.event.ActionEvent;

public class MainMenuForm {
    private JButton startButton;
    private JPanel mainPanel;

    private PacContext pacContext;

    public MainMenuForm(PacContext pacContext) {
        this.pacContext = pacContext;
        mainPanel.setOpaque(false);
        startButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pacContext.getGameManager().startGame();
            }
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
