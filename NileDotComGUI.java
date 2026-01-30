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
        
        //JLabel qtyLabel = new JLabel("Details");
        //JTextField qtyField = new JTextField(15);

        JButton searchButton = new JButton("Search");

        JButton addButton = new JButton("Add To Cart");
        addButton.setEnabled(false);

        JButton deleteButton = new JButton("Delete Last Item");
        deleteButton.setEnabled(false);

        JButton checkoutButton = new JButton("Check Out");
        checkoutButton.setEnabled(false);


        JTextArea cartArea = new JTextArea(8, 55);
        cartArea.setEditable(false);
        JScrollPane cartScroll = new JScrollPane(cartArea);


///  //////////////////////////SEARCH/////////////////////////
        searchButton.addActionListener(e -> {
            String id = idField.getText().trim();

            // 1) Read quantity as an int
            int searchQty;
            try {
                searchQty = Integer.parseInt(qtyField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Quantity must be a whole number (ex: 1, 2, 3)",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2) Now search inventory.csv
            try {
                boolean found = false;

                for (String line : Files.readAllLines(Paths.get("inventory.csv"))) {
                    String[] parts = line.split(",");

                    // parts meaning:
                    // 0=id, 1="description", 2=inStock(true/false), 3=qtyOnHand, 4=price
                    String itemId = parts[0].trim();
                    String description = parts[1].replace("\"", "").trim();
                    boolean inStock = Boolean.parseBoolean(parts[2].trim());
                    int qtyOnHand = Integer.parseInt(parts[3].trim());
                    double price = Double.parseDouble(parts[4].trim());

                    if (itemId.equals(id)) {
                        found = true;

                        // 3) Check in-stock flag
                        if (!inStock) {
                            JOptionPane.showMessageDialog(frame,
                                    "Sorry... that item is out of stock.",
                                    "Nile Dot Com - ERROR",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // 4) Check requested quantity <= qty on hand
                        if (searchQty > qtyOnHand) {
                            JOptionPane.showMessageDialog(frame,
                                    "Insufficient stock. Only " + qtyOnHand + " available.",
                                    "Nile Dot Com - ERROR",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }


                        // 5) SUCCESS: show details
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

                        // SAVE into class variables so Add button uses the right values
                        NileDotComGUI.itemId = itemId;
                        NileDotComGUI.description = description;
                        NileDotComGUI.price = price;
                        NileDotComGUI.qty = searchQty;
                        NileDotComGUI.discountRate = discountRate;
                        NileDotComGUI.lineSubtotal = lineSubtotal;

                        addButton.setEnabled(true);
                        return;

                    }
                }

                // 6) If loop ends and we never matched the ID
                if (!found) {
                    JOptionPane.showMessageDialog(frame,
                            "Item ID " + id + " not found.",
                            "Nile Dot Com - ERROR",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Could not read inventory.csv.\nMake sure it is in your project folder (same level as src).",
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
/// //////////////////////////////////////////////////////////
        addButton.addActionListener(e -> {

            if (itemCount == 5) { // since it starts counting at 0 will probably have to change this to 4
                JOptionPane.showMessageDialog(frame,
                        "Cart is full (5 items max)",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            itemCount = cart.size() + 1;
            itemCount = cart.size();

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

            cartTotals.add(lineSubtotal);   // store the subtotal for this cart line
            deleteButton.setEnabled(true);  // now we have at least 1 item

            cartArea.append(cartLine + "\n");

            checkoutButton.setEnabled(true);

            JOptionPane.showMessageDialog(frame,
                    "Item added to cart.\nCurrent subtotal: $" +
                            String.format("%.2f", orderSubtotal));

            addButton.setEnabled(false);
        });
////////////////////////////////////////////////////////
        deleteButton.addActionListener(e -> {
            if (cart.isEmpty()) return;

            // subtract last subtotal
            double last = cartTotals.remove(cartTotals.size() - 1);
            orderSubtotal -= last;

            // remove last cart line
            cart.remove(cart.size() - 1);
            itemCount--;

            // rebuild cartArea text from scratch (easy + reliable)
            cartArea.setText("");
            for (String line : cart) {
                cartArea.append(line + "\n");
            }

            JOptionPane.showMessageDialog(frame,
                    "Last item deleted.\nCurrent subtotal: $" + String.format("%.2f", orderSubtotal));

            if (cart.isEmpty()) {
                checkoutButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }
        });
///////////////////////////////////////////////////////////////
        checkoutButton.addActionListener(e -> {

            if (cart.isEmpty()) {
                return;
            }

            double taxRate = 0.06;
            double taxAmount = orderSubtotal * taxRate;
            double finalTotal = orderSubtotal + taxAmount;

            StringBuilder invoice = new StringBuilder();
            invoice.append("Nile Dot Com - Final Invoice\n\n");

            for (String line : cart) {
                invoice.append(line).append("\n");
            }

            invoice.append("\n----------------------------\n");
            invoice.append(String.format("Subtotal: $%.2f\n", orderSubtotal));
            invoice.append(String.format("Tax (6%%): $%.2f\n", taxAmount));
            invoice.append(String.format("FINAL TOTAL: $%.2f\n", finalTotal));
            invoice.append("\n\nThank you for shopping at Nile Dot Com!");

            JOptionPane.showMessageDialog(frame,
                    invoice.toString(),
                    "Final Invoice",
                    JOptionPane.INFORMATION_MESSAGE);

            // ===== WRITE TO transactions.csv =====
            try (PrintWriter out = new PrintWriter(new FileWriter("transactions.csv", true))) {

                String transactionId =
                        java.time.LocalDateTime.now()
                                .format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyyHHmmss"));

                String timestamp =
                        java.time.LocalDateTime.now()
                                .format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"));

                for (String line : cart) {
                    out.println(transactionId + "," + timestamp + "," + line );
                }
                out.println("\n");

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Error writing transactions.csv",
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            // ===== LOCK THE APP AFTER CHECKOUT =====
            searchButton.setEnabled(false);
            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
            checkoutButton.setEnabled(false);
        });


        frame.setLayout(new FlowLayout());
        frame.add(idLabel);
        frame.add(idField);
        frame.add(qtyLabel);
        frame.add(qtyField);
        frame.add(searchButton);
        frame.add(addButton);
        frame.add(deleteButton);
        frame.add(checkoutButton);
        frame.add(cartScroll);

        frame.setVisible(true);
    }
    private static double getDiscountRate(int qty) {
        if (qty >= 15) return 0.20;
        if (qty >= 10) return 0.15;
        if (qty >= 5) return 0.10;
        return 0.0;
    }

}
