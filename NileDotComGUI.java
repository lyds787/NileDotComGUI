import javax.swing.*;
import java.awt.*;

public class NileDotComGUI {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Nile.com");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton button = new JButton("Click Me");

        button.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Button clicked!");
        });

        frame.setLayout(new FlowLayout());
        frame.add(button);

        frame.setVisible(true);
    }
}
