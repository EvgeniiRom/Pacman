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
        JPanel panel = new JPanel(new BorderLayout()){
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Dimension size = getSize();
                double blockWidth = size.getWidth() / fieldWidth;
                double blockHeight = size.getHeight() / fieldHeight;
                FieldRenderer.renderField((Graphics2D)g, blockWidth, blockHeight, field);
            }
        };

        JFrame frame = new JFrame("pacman");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setPreferredSize(new Dimension(600, 400));
        frame.pack();
        frame.setVisible(true);
    }
}
