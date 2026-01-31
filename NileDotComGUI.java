/* Name: Lydianne A. Rivera Cordero
Course: CNT 4714 – Spring 2026
Assignment title: Project 1 – An Event-driven Enterprise Simulation
Date: Sunday February 1, 2026
*/
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
    static int currentItemNumber = 1;


    public static void main(String[] args) {


        JFrame frame = new JFrame("Nile.com");
        frame.setSize(750, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel idLabel = new JLabel("Item ID:");
        JTextField idField = new JTextField(30);

        JLabel qtyLabel = new JLabel("Quantity:");
        JTextField qtyField = new JTextField(30);

        JLabel detailsLabel = new JLabel("Item Details:");
        JTextArea detailArea = new JTextArea(1, 30);
        detailArea.setEditable(false);
        JScrollPane detailScroll = new JScrollPane(detailArea);

        JLabel subtotalLabel = new JLabel("Current Subtotal for 0 item(s):");
        JTextField subtotalField = new JTextField(30);
        subtotalField.setEditable(false);
        subtotalField.setText(String.format("$%.2f", orderSubtotal));


/*
        JLabel detailsLabel = new JLabel("Item Details:");
        JTextArea detailsArea = new JTextArea(4, 55);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        JScrollPane detailsScroll = new JScrollPane(detailsArea);
*/

        //JLabel qtyLabel = new JLabel("Details:");
        //        JTextField qtyField = new JTextField(15);

        JButton searchButton = new JButton("Search");

        JButton addButton = new JButton("Add To Cart");
        addButton.setEnabled(false);

        JButton deleteButton = new JButton("Delete Last Item");
        deleteButton.setEnabled(false);

        JButton checkoutButton = new JButton("Check Out");
        checkoutButton.setEnabled(false);

        JButton newOrderButton = new JButton("New Order");
        newOrderButton.setEnabled(false);

        JButton exitButton = new JButton("Exit");



        JTextArea cartArea = new JTextArea(8, 55);
        cartArea.setEditable(false);
        JScrollPane cartScroll = new JScrollPane(cartArea);

        setUiForItemNumber(currentItemNumber, idLabel, qtyLabel, detailsLabel,
                searchButton, addButton, idField, qtyField);



/////////////////////////////SEARCH/////////////////////////
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
/*
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
*/
                        detailArea.setText(
                                itemId + " " + description + " " + "$" +String.format("%.2f", price)+ " " + searchQty+ " " + (int)(discountPercent)+ "% " + " $" + String.format("%.2f", lineSubtotal)

                                //"Item Found!\n" +
                                      //  "ID: " + itemId + "\n" +
                                        //"Desc: " + description + "\n" +
                                       // "Price: $" + String.format("%.2f", price) + "\n" +
                                       // "Requested Qty: " + searchQty + "\n" +
                                       // "Discount: " + (int)(discountPercent) + "%\n" +
                                       // "Line Subtotal: $" + String.format("%.2f", lineSubtotal)

                        );

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
/////////////////////////////////////////////////////////////
        addButton.addActionListener(e -> {

            if (cart.size() >= 5) { // since it starts counting at 0 will probably have to change this to 4
                JOptionPane.showMessageDialog(frame,
                        "Cart is full (5 items max)",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            itemCount = cart.size() + 1;


            orderSubtotal += lineSubtotal;

            subtotalField.setText(String.format("$%.2f", orderSubtotal));
            subtotalLabel.setText("Current Subtotal for " + (cart.size() + 1) + " item(s):");


            String cartLine =
                    "Item " + itemCount + ". " +
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

            /*JOptionPane.showMessageDialog(frame,
                    "Item added to cart.\nCurrent subtotal: $" +
                            String.format("%.2f", orderSubtotal));
                            */
            subtotalField.setText(String.format("$%.2f", orderSubtotal));
            subtotalLabel.setText("Current Subtotal for " + cart.size() + " item(s):");


            addButton.setEnabled(false);

            currentItemNumber++;  // go to next item number

            setUiForItemNumber(currentItemNumber, idLabel, qtyLabel, detailsLabel,
                    searchButton, addButton, idField, qtyField);

        });
////////////////////////////////////////////////////////
        deleteButton.addActionListener(e -> {
            if (cart.isEmpty()) return;

            // subtract last subtotal
            double last = cartTotals.remove(cartTotals.size() - 1);
            orderSubtotal -= last;

            subtotalField.setText(String.format("$%.2f", orderSubtotal));
            subtotalLabel.setText("Current Subtotal for " + cart.size() + " item(s):");


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
/// ///////////////////////////////////////////////////////////
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
            /*
            searchButton.setEnabled(false);
            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
            checkoutButton.setEnabled(false);
*/

            // Lock inputs
            idField.setEditable(false);
            qtyField.setEditable(false);

// Disable buttons (order completed)
            searchButton.setEnabled(false);   // Find Item / Search
            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
            checkoutButton.setEnabled(false);

// Enable post-checkout actions
            newOrderButton.setEnabled(true);
            exitButton.setEnabled(true);



        });
//////////////////////////////////////////////////////////////////
        exitButton.addActionListener(e -> System.exit(0));
/// ///////////////////////////////////////////////////////////////
        newOrderButton.addActionListener(e -> {

            // Clear data structures
            cart.clear();
            cartTotals.clear();
            itemCount = 0;
            orderSubtotal = 0.0;

            subtotalField.setText(String.format("$%.2f", orderSubtotal));
            subtotalLabel.setText("Current Subtotal for 0 item(s):");


            // Clear UI fields
            idField.setText("");
            qtyField.setText("");
            detailArea.setText("");     // if you use JTextArea
            // detailArea.setText("");  // if it's JTextField, same line works

            cartArea.setText("");       // clears cart display

            // Reset saved item
            NileDotComGUI.itemId = null;
            NileDotComGUI.description = null;
            NileDotComGUI.price = 0.0;
            NileDotComGUI.qty = 0;
            NileDotComGUI.discountRate = 0.0;
            NileDotComGUI.lineSubtotal = 0.0;

            // Unlock inputs
            idField.setEditable(true);
            qtyField.setEditable(true);

            // Reset buttons for a fresh order
            searchButton.setEnabled(true);
            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
            checkoutButton.setEnabled(false);

            // Disable new order until next checkout
            newOrderButton.setEnabled(false);

            currentItemNumber = 1;

            setUiForItemNumber(currentItemNumber, idLabel, qtyLabel, detailsLabel,
                    searchButton, addButton, idField, qtyField);

        });




// Use vertical stacking for rows
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

// Row 1: ID
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        row1.add(idLabel);
        row1.add(idField);

// Row 2: Quantity
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        row2.add(qtyLabel);
        row2.add(qtyField);

// Row 3: Details
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        row3.add(detailsLabel);
        row3.add(detailScroll);

        JPanel rowSub = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rowSub.add(subtotalLabel);
        rowSub.add(subtotalField);


// Row 4: Buttons
        JPanel row4 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        row4.add(searchButton);
        row4.add(addButton);
        row4.add(deleteButton);
        row4.add(checkoutButton);
        row4.add(newOrderButton);
        row4.add(exitButton);


// Row 5: Cart
        JPanel row5 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cartScroll.setPreferredSize(new Dimension(800, 220));
        row5.add(cartScroll);

// Align rows to the left (important for BoxLayout)
        row1.setAlignmentX(Component.RIGHT_ALIGNMENT);
        row2.setAlignmentX(Component.RIGHT_ALIGNMENT);
        row3.setAlignmentX(Component.RIGHT_ALIGNMENT);
        row4.setAlignmentX(Component.RIGHT_ALIGNMENT);
        row5.setAlignmentX(Component.RIGHT_ALIGNMENT);

// Add rows with controlled spacing
        frame.add(row1);
        frame.add(Box.createVerticalStrut(6));
        frame.add(row2);
        frame.add(Box.createVerticalStrut(6));
        frame.add(row3);
        frame.add(Box.createVerticalStrut(6));
        frame.add(rowSub);
        frame.add(Box.createVerticalStrut(10));
        frame.add(row4);
        frame.add(Box.createVerticalStrut(10));
        frame.add(row5);



// Better sizing behavior
        frame.pack();
        frame.setSize(950, 600);           // optional: keeps it from being tiny
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


    }
    private static double getDiscountRate(int qty) {
        if (qty >= 15) return 0.20;
        if (qty >= 10) return 0.15;
        if (qty >= 5) return 0.10;
        return 0.0;
    }
    private static void setUiForItemNumber(
            int itemNum,
            JLabel idLabel, JLabel qtyLabel, JLabel detailsLabel,
            JButton searchButton, JButton addButton,
            JTextField idField, JTextField qtyField
    ) {
        idLabel.setText("Enter item ID for Item #" + itemNum + ":");
        qtyLabel.setText("Enter quantity for Item #" + itemNum + ":");

        // Details shows the previous item number (like the screenshot)
        detailsLabel.setText("Details for Item #" + (itemNum - 1) + ":");

        searchButton.setText("Search For Item #" + itemNum);
        addButton.setText("Add Item #" + itemNum + " To Cart");

        // Clear inputs for the next item
        idField.setText("");
        qtyField.setText("");
        idField.requestFocus();
    }

}
