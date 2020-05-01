import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Pacman {
    public static void main(String[] args) throws IOException {
        FieldReader fieldReader = new FieldReader();
        fieldReader.read("field.ini");
        fieldReader.printField();

        int[][] field = fieldReader.getField();
        int fieldHeight = fieldReader.getHeight();
        int fieldWidth = fieldReader.getWidth();


        Person person = new Person();
        Engine engine = new Engine();
        engine.addWorldObject("pac", person);

        JPanel panel = new JPanel(new BorderLayout()){
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Dimension size = getSize();
                int blockSize = 10;
                double yScale = size.getHeight()/(fieldHeight*blockSize);
                double xScale = size.getWidth()/(fieldWidth*blockSize);
                Graphics2D g2 = (Graphics2D) g;
                g2.scale(xScale, yScale);
                Renderer.renderField(g2, blockSize, blockSize, field);
                person.render(g2);
            }
        };

        JFrame frame = new JFrame("pacman");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setPreferredSize(new Dimension(600, 400));
        frame.pack();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1);
                        panel.repaint();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        engine.startWorld();
        frame.setVisible(true);
    }
}
