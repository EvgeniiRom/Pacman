import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.logging.Logger;

public class MainForm extends JFrame {
    private JPanel contentPane;
    private JButton startButton;
    private JButton stopButton;

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
        getRootPane().setDefaultButton(startButton);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onStart();
            }
        });
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onStop();
            }
        });
        gameManager = new GameManager();

        registerKeyboard();


        contentPane.add(gameManager.getGamePanel(), BorderLayout.CENTER);
        setPreferredSize(new Dimension(600, 400));
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
                onStop();
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
    private void onStart() {
        logger.info("start");
        gameManager.startGame();
    }
    private void onStop() {
        logger.info("stop");
        gameManager.stopGame();
    }
}
