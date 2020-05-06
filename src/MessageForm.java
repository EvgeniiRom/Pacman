import javax.swing.*;

public class MessageForm {
    private JLabel label;
    private JPanel mainPanel;

    public MessageForm() {
        mainPanel.setOpaque(false);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setText(String text){
        label.setText(text);
    }
}
