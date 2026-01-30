import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;


public class NileDotComGUI {
    
static ArrayList<String> cart = new ArrayList<>();
static int itemCount = 0;
static double orderSubtotal = 0.0;


    public static void main(String[] args) {

        JFrame frame = new JFrame("Nile.com");
        frame.setSize(750, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel idLabel = new JLabel("Item ID:");
        JTextField idField = new JTextField(15);

        JLabel qtyLabel = new JLabel("Quantity:");
        JTextField qtyField = new JTextField(15);

        JButton searchButton = new JButton("Search");
        
        JButton addButton = new JButton("Add To Cart");
addButton.setEnabled(false);


searchButton.addActionListener(e -> {
    String id = idField.getText().trim();
    int qty;

    try {
        qty = Integer.parseInt(qtyField.getText().trim());
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(frame,
                "Quantity must be a number",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        boolean found = false;

        for (String line : Files.readAllLines(Paths.get("inventory.csv"))) {
            String[] parts = line.split(",");

            String itemId = parts[0].trim();
            String description = parts[1].replace("\"", "");
            boolean inStock = Boolean.parseBoolean(parts[2]);
            int stockQty = Integer.parseInt(parts[3]);
            double price = Double.parseDouble(parts[4]);

            if (itemId.equals(id)) {
                found = true;

                if (!inStock) {
                    JOptionPane.showMessageDialog(frame,
                            "Sorry, this item is out of stock",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                } 

                if (qty > stockQty) {
                    JOptionPane.showMessageDialog(frame,
                            "Only " + stockQty + " items available",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double discountRate = getDiscountRate(qty);
double discountPercent = discountRate * 100.0;

double lineSubtotal = qty * price * (1.0 - discountRate);

JOptionPane.showMessageDialog(frame,
        "Item Found!\n" +
        "ID: " + itemId + "\n" +
        "Desc: " + description + "\n" +
        "Price: $" + price + "\n" +
        "Requested Qty: " + qty + "\n" +
        "Discount: " + (int)discountPercent + "%\n" +
        "Line Subtotal: $" + String.format("%.2f", lineSubtotal),
        "Nile Dot Com",
        JOptionPane.INFORMATION_MESSAGE);
                
                addButton.setEnabled(true);
return;

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

        addButton.addActionListener(e -> {

    if (itemCount == 5) {
        JOptionPane.showMessageDialog(frame,
                "Cart is full (5 items max)",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        return;
    }

    double discountRate = getDiscountRate(qty);
    double lineSubtotal = qty * price * (1.0 - discountRate);

    itemCount++;
    orderSubtotal += lineSubtotal;

    String cartLine =
            itemCount + ". " +
            itemId + " | " +
            description + " | " +
            "Qty: " + qty + " | " +
            "Price: $" + price + " | " +
            "Disc: " + (int)(discountRate * 100) + "% | " +
            "Total: $" + String.format("%.2f", lineSubtotal);

    cart.add(cartLine);

    JOptionPane.showMessageDialog(frame,
            "Item added to cart.\nCurrent subtotal: $" +
            String.format("%.2f", orderSubtotal));

    addButton.setEnabled(false);
});



        frame.setLayout(new FlowLayout());
        frame.add(idLabel);
        frame.add(idField);
        frame.add(qtyLabel);
        frame.add(qtyField);
        frame.add(searchButton);
        frame.add(addButton);

        frame.setVisible(true);
    }
    private static double getDiscountRate(int qty) {
    if (qty >= 15) return 0.20;
    if (qty >= 10) return 0.15;
    if (qty >= 5) return 0.10;
    return 0.0;
}

}
