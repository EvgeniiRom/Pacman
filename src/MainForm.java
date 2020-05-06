import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.logging.Logger;

public class MainForm extends JFrame implements ContextListener {
    private JPanel contentPane;
    private JLabel scoreLabel;
    private JLabel livesLabel;
    private JCheckBox fakeFocusableElement;
    private JLabel levelLabel;
    private JLabel killsLabel;

    private GameManager gameManager;
    private Logger logger = Logger.getLogger(MainForm.class.getName());

    public static void main(String[] args) throws IOException {
        MainForm mainForm = new MainForm();
        mainForm.pack();
        mainForm.setVisible(true);
    }

    public MainForm() throws IOException {
        init();
    }

    private void init() throws IOException {
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameManager = new GameManager();
        gameManager.getPacContext().addContextListener(this);
        PacContext pacContext = gameManager.getPacContext();
        scoreLabel.setText(Integer.toString(pacContext.getScore()));
        livesLabel.setText(Integer.toString(pacContext.getLives()));
        registerKeyboard();
        contentPane.add(gameManager.getGamePanel(), BorderLayout.CENTER);
        setPreferredSize(new Dimension(600, 800));
    }

    private void registerKeyboard(){
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onUp();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onDown();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onLeft();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onRight();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onPause();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onUp(){
        logger.info("up");
        gameManager.getPacContext().getPlayer().setPreferredDir(Actor.Dir.UP);
    }
    private void onDown(){
        logger.info("down");
        gameManager.getPacContext().getPlayer().setPreferredDir(Actor.Dir.DOWN);
    }
    private void onLeft(){
        logger.info("left");
        gameManager.getPacContext().getPlayer().setPreferredDir(Actor.Dir.LEFT);
    }
    private void onRight(){
        logger.info("right");
        gameManager.getPacContext().getPlayer().setPreferredDir(Actor.Dir.RIGHT);
    }
    private void onPause() {
        logger.info("pause");
        gameManager.pauseGame();
    }

    @Override
    public void onScoreChange(int score) {
        scoreLabel.setText(Integer.toString(score));
    }

    @Override
    public void onLiveChange(int lives) {
        livesLabel.setText(Integer.toString(lives));
    }

    @Override
    public void onLevelChange(int level) {
        levelLabel.setText(Integer.toString(level));
    }

    @Override
    public void onKillsChange(int kills) {
        killsLabel.setText(Integer.toString(kills));
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        fakeFocusableElement = new JCheckBox(){
            @Override
            public void paint(Graphics g) {
                //super.paint(g);
                Dimension size = getSize();
                g.setColor(Color.BLACK);
                g.fillRect(0,0, size.width, size.height);
            }
        };
    }
}
