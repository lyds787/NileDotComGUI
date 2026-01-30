import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;

public class NileDotComGUI {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Nile.com");
        frame.setSize(750, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel idLabel = new JLabel("Item ID:");
        JTextField idField = new JTextField(15);

        JLabel qtyLabel = new JLabel("Quantity:");
        JTextField qtyField = new JTextField(15);

        JButton searchButton = new JButton("Search");

        searchButton.addActionListener(e -> {
            String id = idField.getText().trim();

            try {
                boolean found = false;

                for (String line : Files.readAllLines(Paths.get("inventory.csv"))) {
                    String[] parts = line.split(",");

                    String itemId = parts[0].trim();
                    String description = parts[1];
                    double price = Double.parseDouble(parts[4]);

                    if (itemId.equals(id)) {
                        found = true;
                        JOptionPane.showMessageDialog(frame,
                                "Item Found:\n" +
                                description + "\nPrice: $" + price);
                        break;
                    }
                }

                if (!found) {
                    JOptionPane.showMessageDialog(frame,
                            "Item ID " + id + " not found",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Could not read inventory.csv",
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setLayout(new FlowLayout());
        frame.add(idLabel);
        frame.add(idField);
        frame.add(qtyLabel);
        frame.add(qtyField);
        frame.add(searchButton);

        frame.setVisible(true);
    }
}

    }
}
