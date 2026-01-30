import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;

public class NileDotComGUI {

    // Cart storage
    static ArrayList<String> cart = new ArrayList<>();
    static ArrayList<Double> cartTotals = new ArrayList<>();

    static int itemCount = 0;
    static double orderSubtotal = 0.0;

    // Remember the last searched valid item
    static String itemId = null;
    static String description = null;
    static double price = 0.0;
    static int qty = 0;
    static double discountRate = 0.0;
    static double lineSubtotal = 0.0;

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

//////////////////////////////////////////////////////////////////////////////
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

               double discountRate = getDiscountRate(searchQty);
                        double discountPercent = discountRate * 100.0;

                        double lineSubtotal = searchQty * price * (1.0 - discountRate);

                        JOptionPane.showMessageDialog(frame,
                                "Item Found!\n" +
                                        "ID: " + itemId + "\n" +
                                        "Desc: " + description + "\n" +
                                        "Price: $" + price + "\n" +
                                        "Requested Qty: " + searchQty + "\n" +
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
//////////////////////////////////////////////////////////////////

addButton.addActionListener(e2 -> {

    // 1) Must have searched a valid item first
    if (lastItemId == null) {
        JOptionPane.showMessageDialog(frame,
                "Search for an item first.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        return;
    }

    // 2) Max 5 items
    if (itemCount >= 5) {
        JOptionPane.showMessageDialog(frame,
                "Cart is full (5 items max).",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        addButton.setEnabled(false);
        return;
    }

    // 3) Add to cart
    itemCount++;
    orderSubtotal += lastLineSubtotal;

    String cartLine =
            itemCount + ". " +
            lastItemId + " | " +
            lastDescription + " | " +
            "Qty: " + lastQty + " | " +
            "Price: $" + String.format("%.2f", lastPrice) + " | " +
            "Disc: " + (int)(lastDiscountRate * 100) + "% | " +
            "Total: $" + String.format("%.2f", lastLineSubtotal);

    cart.add(cartLine);

    JOptionPane.showMessageDialog(frame,
            "Item added to cart.\nCurrent subtotal: $" +
            String.format("%.2f", orderSubtotal));

    // 4) Reset so they must search again before adding another
    lastItemId = null;
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
