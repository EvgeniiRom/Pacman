import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private PacContext pacContext;
    private Renderer renderer;
    private CardLayout overlayLayout;
    private JPanel overlayPanel;

    private GameOverForm gameOverForm;
    private MainMenuForm mainMenuForm;
    private PauseForm pauseForm;

    public enum Scene {
        MAIN_MENU,
        GAMING,
        PAUSE,
        GAME_OVER;
    }

    public GamePanel(PacContext pacContext, Renderer renderer) {
        super(new BorderLayout());
        this.pacContext = pacContext;
        this.renderer = renderer;

        overlayLayout = new CardLayout();
        overlayPanel = new JPanel(overlayLayout){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Dimension size = getSize();
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, size.width, size.height);
            }
        };
        overlayPanel.setOpaque(false);

        mainMenuForm = new MainMenuForm(pacContext);
        pauseForm = new PauseForm(pacContext);
        gameOverForm = new GameOverForm(pacContext);

        overlayPanel.add(mainMenuForm.getMainPanel(), Scene.MAIN_MENU.name());
        overlayPanel.add(pauseForm.getMainPanel(), Scene.PAUSE.name());
        overlayPanel.add(gameOverForm.getMainPanel(), Scene.GAME_OVER.name());

        add(overlayPanel, BorderLayout.CENTER);

        overlayLayout.show(overlayPanel, Scene.MAIN_MENU.name());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = getSize();
        renderer.render((Graphics2D) g, size);
    }

    public void setScene(Scene scene) {
        overlayLayout.show(overlayPanel, scene.name());

        if(scene.equals(Scene.GAMING)){
            overlayPanel.setVisible(false);
        }else {
            overlayPanel.setVisible(true);
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
        for (int i = 0; i < envfonts.length; i++) {
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
