import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private Renderer renderer;
    private JLabel overlayLabel;
    private PacContext pacContext;
    private boolean overlay = false;

    public enum Scene{
        MAIN_MENU,
        GAMING,
        GAME_OVER;
    }

    public GamePanel(PacContext pacContext, Renderer renderer) {
        super(new BorderLayout());
        this.pacContext = pacContext;
        this.renderer = renderer;
        overlayLabel = new JLabel("", SwingConstants.CENTER);
        overlayLabel.setFont(new Font("01 DIGIT", Font.PLAIN, 50));
        add(overlayLabel, BorderLayout.CENTER);
        overlayLabel.setVisible(false);
    }

    private void setGameOverMessage(int score){
        String text = "<html><p align=\"center\"><font color='white'>" +
                "GAME OVER<br>" +
                "<br>" +
                "SCORE : " + String.valueOf(score) +
                "</font></p></html>";
        overlayLabel.setText(text);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = getSize();
        renderer.render((Graphics2D) g, size);
        if (overlay) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, size.width, size.height);
        }
    }

    public void setScene(Scene scene){
        switch (scene) {
            case MAIN_MENU:
                break;
            case GAMING:
                overlay = false;
                overlayLabel.setVisible(false);
                break;
            case GAME_OVER:
                overlay = true;
                setGameOverMessage(pacContext.getScore());
                overlayLabel.setVisible(true);
                break;
        }
    }

    /**
     * Показывает все доступные шрифты в системе
     */
    public static void main(String[] args) {
        GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String envfonts[] = gEnv.getAvailableFontFamilyNames();
        GridLayout layout = new GridLayout(envfonts.length, 1);
        JPanel jPanel = new JPanel(layout);
        for (int i = 0; i<envfonts.length; i++) {
            JLabel jLabel = new JLabel(envfonts[i]);
            jLabel.setFont(new Font(envfonts[i], Font.PLAIN, 50));
            jPanel.add(jLabel);
        }
        JScrollPane jScrollPane = new JScrollPane(jPanel);
        JFrame frame = new JFrame("test");
        frame.setContentPane(jScrollPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
