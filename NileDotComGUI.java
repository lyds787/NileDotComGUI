import javax.swing.*;
import java.awt.*;

public class NileDotComGUI {
    public static void main(String[] args) {

        JFrame frame = new JFrame("Nile.com");
        frame.setSize(700, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Labels + Text Fields
        JLabel idLabel = new JLabel("Item ID:");
        JTextField idField = new JTextField(15);

        JLabel qtyLabel = new JLabel("Quantity:");
        JTextField qtyField = new JTextField(15);

        // Button
        JButton searchButton = new JButton("Search");

        searchButton.addActionListener(e -> {
            String id = idField.getText();       // what user typed
            String qty = qtyField.getText();     // what user typed

            JOptionPane.showMessageDialog(frame,
                    "You typed:\nItem ID = " + id + "\nQuantity = " + qty);
        });

        // Put everything on screen
        frame.setLayout(new FlowLayout());
        frame.add(idLabel);
        frame.add(idField);
        frame.add(qtyLabel);
        frame.add(qtyField);
        frame.add(searchButton);

        frame.setVisible(true);
    }
}
