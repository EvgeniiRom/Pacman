import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.logging.Logger;

public class MainForm extends JFrame {
    private JPanel contentPane;
    private JButton startButton;
    private JButton stopButton;
    private GamePanel gamePanel;

    private PacContext pacContext;
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
        pacContext = new PacContext();

        registerKeyboard();

        gamePanel = new GamePanel(pacContext);
        contentPane.add(gamePanel, BorderLayout.CENTER);
        setPreferredSize(new Dimension(600, 400));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(10);
                        gamePanel.repaint();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
        pacContext.getPlayer().setPreferredDir(Person.Dir.UP);
    }
    private void onDown(){
        logger.info("down");
        pacContext.getPlayer().setPreferredDir(Person.Dir.DOWN);
    }
    private void onLeft(){
        logger.info("left");
        pacContext.getPlayer().setPreferredDir(Person.Dir.LEFT);
    }
    private void onRight(){
        logger.info("right");
        pacContext.getPlayer().setPreferredDir(Person.Dir.RIGHT);
    }
    private void onStart() {
        logger.info("start");
        pacContext.getEngine().startWorld();
    }
    private void onStop() {
        logger.info("stop");
        pacContext.getEngine().stopWorld();
    }
}
