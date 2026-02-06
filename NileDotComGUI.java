/* Name: Lydianne A. Rivera Cordero
Course: CNT 4714 – Spring 2026
Assignment title: Project 1 – An Event-driven Enterprise Simulation
Date: Sunday February 1, 2026
*/
import javax.swing.*;// Swing GUI stuff
import java.awt.*;// Layout + colors + fonts
import java.nio.file.*;// For reading the inventory file
import java.io.*;// For writing to the transactions file
import java.util.ArrayList;// storing the cart items in memory

public class NileDotComGUI {

    // Cart storage
    static ArrayList<String> cart = new ArrayList<>();
    static ArrayList<Double> cartTotals = new ArrayList<>();

    // Keeps track of how many items are in the cart and the subtotal

    static int itemCount = 0;
    static double orderSubtotal = 0.0;


    // These variables store the last item that was searched
    //so when the user clicks "Add", we know exactly what to add

    static String itemId = null;
    static String description = null;
    static double price = 0.0;
    static int qty = 0;
    static double discountRate = 0.0;
    static double lineSubtotal = 0.0;

    // The item number that the user is currently dealing with

    static int currentItemNumber = 1;

    // These arrays store the cart data used for checkout aand writing to transactions.csv file

    static ArrayList<String> cartItemIds = new ArrayList<>();
    static ArrayList<String> cartDescs = new ArrayList<>();
    static ArrayList<Double> cartPrices = new ArrayList<>();
    static ArrayList<Integer> cartQtys = new ArrayList<>();
    static ArrayList<Double> cartDiscounts = new ArrayList<>();
    static ArrayList<Double> cartLineTotals = new ArrayList<>();


    public static void main(String[] args) {

        // Create the main window for the program and put the webpage name
        JFrame frame = new JFrame("Nile.com");
        frame.setSize(750, 250);// Initial window size
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//end the program when user hits the x

        // Label and input field for ID

        JLabel idLabel = new JLabel("Item ID:");
        JTextField idField = new JTextField(40);

        // Label and input field for Quantity

        JLabel qtyLabel = new JLabel("Quantity:");
        JTextField qtyField = new JTextField(40);

        // Label and input field for details

        JLabel detailsLabel = new JLabel("Item Details:");
        JTextField detailField = new JTextField(40);//size
        detailField.setEditable(false);//the user can't type here

        // Label and output field for the subtotal
        JLabel subtotalLabel = new JLabel("Current Subtotal for 0 item(s):");
        JTextField subtotalField = new JTextField(40);//size
        subtotalField.setEditable(false);//the user can't type here
        subtotalField.setText(String.format("$%.2f", orderSubtotal));

        // Buttons the user clicks to interact with the program

        JButton searchButton = new JButton("Search");
        JButton addButton = new JButton("Add To Cart");
        addButton.setEnabled(false);
        JButton deleteButton = new JButton("Delete Last Item");
        deleteButton.setEnabled(false);
        JButton checkoutButton = new JButton("Check Out");
        checkoutButton.setEnabled(false);
        JButton newOrderButton = new JButton("Empty Cart - Start A New Order");
        newOrderButton.setEnabled(true);
        JButton exitButton = new JButton("Exit (Close App)");

        // Text shown above the cart area that its subosed to chage, will figure it out later

        JLabel cartHeader = new JLabel("Your Shopping Cart Is Currently Empty");
        cartHeader.setForeground(Color.RED);
        cartHeader.setFont(new Font("SansSerif", Font.BOLD, 16));

        // Title label above the buttons - doesnt change

        JLabel userControlsLabel = new JLabel("USER CONTROLS");
        userControlsLabel.setForeground(Color.YELLOW);
        userControlsLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        userControlsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        //there are only 5 items that can be added

        JTextField[] cartLines = new JTextField[5];
        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        cartPanel.setBackground(Color.BLACK);

        // Create the 5 cart line fields

        for (int i = 0; i < 5; i++) {
            cartLines[i] = new JTextField(80);
            cartLines[i].setEditable(false);
            cartLines[i].setText("");

            // make the white bars
            cartLines[i].setPreferredSize(new Dimension(900, 26));
            cartLines[i].setMaximumSize(new Dimension(900, 26));

            cartLines[i].setBackground(Color.WHITE);
            //size
            cartLines[i].setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));

            // wrap the line in a black row so the gap is visible
            JPanel lineRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            lineRow.setBackground(Color.BLACK);
            lineRow.add(cartLines[i]);

            // Add the row and a vertical gap under it
            cartPanel.add(lineRow);
            cartPanel.add(Box.createVerticalStrut(10));  // this is the visible black gap
        }




        setLabelItemNumber(currentItemNumber, idLabel, qtyLabel, detailsLabel,
                searchButton, addButton, idField, qtyField);



/////////////////////////////SEARCH/////////////////////////
// Search button logic
        searchButton.addActionListener(e -> {
            String id = idField.getText().trim();//get the id from the text field
            addButton.setEnabled(false);//disable the add button
            detailField.setText("");//clear the details field


            // Read quantity as an int
            int searchQty;
            try {
                // Try to read the quantity as an integer
                searchQty = Integer.parseInt(qtyField.getText().trim());
            }
            // If the user didn't enter a valid number, show an error message and stop
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Quantity must be a whole number (ex: 1, 2, 3)",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Now search inventory.csv
            try {
                boolean found = false;//Flag to track if the item was found

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

                        //Check in-stock flag
                        if (!inStock) {
                            //if the item is not in stock
                            JOptionPane.showMessageDialog(frame,
                                    "Sorry... that item is out of stock, please try another item",
                                    "Nile Dot Com - ERROR",
                                    JOptionPane.ERROR_MESSAGE);

                            // Clear the fields and focus on the ID field
                            idField.setText("");
                            qtyField.setText("");
                            detailField.setText("");
                            idField.requestFocus();

                            // Disable the add button and enable the search button
                            addButton.setEnabled(false);
                            searchButton.setEnabled(true);
                            return;
                        }


                        //Check requested quantity <= qty on hand

                        if (searchQty > qtyOnHand) {//if the user requested more than is in stock
                            JOptionPane.showMessageDialog(frame,
                                    "Insufficient stock. Only " + qtyOnHand + " on hand. Please reduce the quantity.",
                                    "Nile Dot Com - ERROR",
                                    JOptionPane.ERROR_MESSAGE);


                            // Clear the fields and focus on the ID field
                            qtyField.setText("");
                            detailField.setText("");
                            qtyField.requestFocus();

                            // Disable the add button and enable the search button
                            addButton.setEnabled(false);
                            searchButton.setEnabled(true);
                            return;
                        }



                        // everithing is good then show details
                        double discountRate = getDiscountRate(searchQty);
                        double discountPercent = discountRate * 100.0;

                        double lineSubtotal = searchQty * price * (1.0 - discountRate);


                        //this is the details field that shows the item details
                        detailField.setText(
                                itemId + " " + description + " " + "$" +String.format("%.2f", price)+ " " + searchQty+ " " + (int)(discountPercent)+ "% " + " $" + String.format("%.2f", lineSubtotal)



                        );

                        // save everiting into class variables so Add button uses the right values
                        NileDotComGUI.itemId = itemId;
                        NileDotComGUI.description = description;
                        NileDotComGUI.price = price;
                        NileDotComGUI.qty = searchQty;
                        NileDotComGUI.discountRate = discountRate;
                        NileDotComGUI.lineSubtotal = lineSubtotal;

                        // Enable Add button but disable Search button
                        addButton.setEnabled(true);
                        searchButton.setEnabled(false);
                        return;

                    }
                }

                //If loop ends and we never matched the ID
                if (!found) {//if the item was not found
                    JOptionPane.showMessageDialog(frame,
                            "item ID " + id + " not in file",
                            "Nile Dot Com - ERROR",
                            JOptionPane.ERROR_MESSAGE);

                    // Clear the fields and focus on the ID field
                    idField.setText("");
                    qtyField.setText("");
                    detailField.setText("");
                    idField.requestFocus();

                    // Disable the add button and enable the search button
                    addButton.setEnabled(false);
                    searchButton.setEnabled(true);
                }


                //useful for debuggung doesnt affect the program
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Could not read inventory.csv.\nMake sure it is in your project folder (same level as src).",
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
///////////////////////ADD//////////////////////////////////////
        addButton.addActionListener(e -> {

            if (cart.size() >= 5) { // since it starts counting at 0 will probably have to change this to 4
                JOptionPane.showMessageDialog(frame,
                        "Cart is full (5 items max)",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            itemCount = cart.size() + 1;//this is the item number that is being added to the cart


            orderSubtotal += lineSubtotal;

            // Update subtotal display
            subtotalField.setText(String.format("$%.2f", orderSubtotal));
            subtotalLabel.setText("Current Subtotal for " + (cart.size() + 1) + " item(s):");

            //this is the line that is added to the cart
            String cartLine =
                    "Item " + (itemCount) +
                            " - SKU: " + itemId +
                            ", Desc: \"" + description + "\"" +
                            ", Price Ea. $" + String.format("%.2f", price) +
                            ", Qty: " + qty +
                            ", Total: $" + String.format("%.2f", lineSubtotal);

            // Add to cart
            cart.add(cartLine);
            cartItemIds.add(itemId);
            cartDescs.add(description);
            cartPrices.add(price);
            cartQtys.add(qty);
            cartDiscounts.add(discountRate);
            cartLineTotals.add(lineSubtotal);

            // Update the UI row for this cart line
            int index = cart.size() - 1;          // 0 for first item, 1 for second, etc.
            cartLines[index].setText(cartLine);

            cartHeader.setText("Your Shopping Cart Currently Contains " + cart.size() + " item(s)");


            cartTotals.add(lineSubtotal);   // store the subtotal for this cart line
            deleteButton.setEnabled(true);  // now we have at least 1 item
/// ////////////////////////////////////////////////
            //cartArea.append(cartLine + "\n");

            checkoutButton.setEnabled(true);

            /*JOptionPane.showMessageDialog(frame,
                    "Item added to cart.\nCurrent subtotal: $" +
                            String.format("%.2f", orderSubtotal));
                            */
            // Update subtotal display
            subtotalField.setText(String.format("$%.2f", orderSubtotal));
            subtotalLabel.setText("Current Subtotal for " + cart.size() + " item(s):");

            //reset the search button and add button
            searchButton.setEnabled(true);
            addButton.setEnabled(false);

            // Update item number for next item
            currentItemNumber = cart.size() + 1;
            setLabelItemNumber(currentItemNumber, idLabel, qtyLabel, detailsLabel,
                    searchButton, addButton, idField, qtyField);


            // cart full logic
            // Grey out inputs when cart is full

            if (cart.size() >= 5) {
                setItemInputsEnabled(idField, qtyField, false);
            }

            // Disable aka lock search/add when cart is full
            if (cart.size() >= 5) {
                //cart full means can't search or add more
                searchButton.setEnabled(false);
                addButton.setEnabled(false);

                // user can still delete, checkout start new order, exit
                deleteButton.setEnabled(true);
                checkoutButton.setEnabled(true);
                newOrderButton.setEnabled(true);
                exitButton.setEnabled(true);
            }

        });
//////////////////////DELETE//////////////////////////////////
        deleteButton.addActionListener(e -> {
            if (cart.isEmpty()) return;

            //Subtract last line subtotal
            double last = cartTotals.remove(cartTotals.size() - 1);
            orderSubtotal -= last;

            //Remove last cart display line and clear the corresponding UI row
            cart.remove(cart.size() - 1);
            int clearedIndex = cart.size(); // after removal, size is the index to clear
            cartLines[clearedIndex].setText("");

            //remove from parallel arrays just to keep everything in sync
            cartItemIds.remove(cartItemIds.size() - 1);
            cartDescs.remove(cartDescs.size() - 1);
            cartPrices.remove(cartPrices.size() - 1);
            cartQtys.remove(cartQtys.size() - 1);
            cartDiscounts.remove(cartDiscounts.size() - 1);
            cartLineTotals.remove(cartLineTotals.size() - 1);

            //Update header and subtotal using the new cart.size()
            if (cart.isEmpty()) {
                cartHeader.setText("Your Shopping Cart Is Currently Empty");
            } else {
                cartHeader.setText("Your Shopping Cart Currently Contains " + cart.size() + " item(s)");
            }

            // Update subtotal display
            subtotalField.setText(String.format("$%.2f", orderSubtotal));
            subtotalLabel.setText("Current Subtotal for " + cart.size() + " item(s):");

            // Clear the "last searched item" so Details doesn't show stale info
            NileDotComGUI.itemId = null;
            NileDotComGUI.description = null;
            NileDotComGUI.price = 0.0;
            NileDotComGUI.qty = 0;
            NileDotComGUI.discountRate = 0.0;
            NileDotComGUI.lineSubtotal = 0.0;

            // Clear Details display field too
            detailField.setText("");

            // After deleting, the next item number should be (items in cart + 1)
            currentItemNumber = cart.size() + 1;


            // Reset labels/inputs so that everything udates correctly
            setLabelItemNumber(currentItemNumber, idLabel, qtyLabel, detailsLabel,
                    searchButton, addButton, idField, qtyField);


            // Enable search andadd if cart is not full
            if (cart.size() >= 5) {
                searchButton.setEnabled(false);
                addButton.setEnabled(false);
                setItemInputsEnabled(idField, qtyField, false);
            } else {//if cart is not full
                searchButton.setEnabled(true);
                addButton.setEnabled(false); // must search again
                setItemInputsEnabled(idField, qtyField, true);
            }

            // must search again before adding
            deleteButton.setEnabled(!cart.isEmpty());
            checkoutButton.setEnabled(!cart.isEmpty());
        });

/// //////////////////CHECKOUT/////////////////////////////////////////
        checkoutButton.addActionListener(e -> {



            // If cart is empty, do nothing
            if (cart.isEmpty()) {
                return;
            }

            // Calculate tax and final total
            double taxRate = 0.06;
            double taxAmount = orderSubtotal * taxRate;
            double finalTotal = orderSubtotal + taxAmount;

            //this is the time stamp
            java.time.ZonedDateTime now =
                    java.time.ZonedDateTime.now(java.time.ZoneId.of("America/New_York"));

            String invoiceTimestamp =
                    now.format(java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy, h:mm:ss a z"));

            // Build the invoice string
            StringBuilder invoice = new StringBuilder();
            invoice.append("Nile Dot Com - FINAL INVOICE\n\n");
            invoice.append("Date: ").append(invoiceTimestamp).append("\n\n");
            invoice.append("Number of line items: ").append(cartItemIds.size()).append("\n\n");
            invoice.append("Item# / ID / Title / Price / Qty / Disc % / Subtotal:\n\n");


            for (int i = 0; i < cartItemIds.size(); i++) {
                double discPct = cartDiscounts.get(i) * 100.0;

                // Loop through cart items and add to invoice
                invoice.append(String.format(
                        "%d. %s \"%s\" $%.2f %d %.0f%% $%.2f\n",
                        (i + 1),
                        cartItemIds.get(i),
                        cartDescs.get(i),
                        cartPrices.get(i),
                        cartQtys.get(i),
                        discPct,
                        cartLineTotals.get(i)
                ));
            }


            // Add totals to invoice
            invoice.append("\n");
            invoice.append(String.format("Order subtotal: $%.2f\n\n", orderSubtotal));
            invoice.append("Tax rate: 6%\n\n");
            invoice.append(String.format("Tax amount: $%.2f\n\n", taxAmount));
            invoice.append(String.format("ORDER TOTAL: $%.2f\n\n", finalTotal));
            invoice.append("Thanks for shopping at Nile Dot Com!");


            // Show invoice in a dialog pop up text box
            JOptionPane.showMessageDialog(frame,
                    invoice.toString(),
                    "Final Invoice",
                    JOptionPane.INFORMATION_MESSAGE);

/////////////////////transactions.csv//////////////////////////
        //create and open the transactions file
            try (PrintWriter out = new PrintWriter(new FileWriter("transactions.csv", true))) {

                String transactionId =
                        java.time.LocalDateTime.now()
                                .format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyyHHmmss"));



                String timestamp =
                        now.format(java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy, h:mm:ss a z"));

                out.println("\n");
                for (int i = 0; i < cartItemIds.size(); i++) {

                    // print on the transactions file
                    out.println(
                            transactionId + "," +
                                    cartItemIds.get(i) + "," +
                                    "\"" + cartDescs.get(i).replace("\"", "\"\"") + "\"," +
                                    String.format("%.2f", cartPrices.get(i)) + "," +
                                    cartQtys.get(i) + "," +
                                    String.format("%.2f", cartDiscounts.get(i)) + "," +
                                    String.format("%.2f", cartLineTotals.get(i)) + "," +
                                    timestamp
                    );
                }

                //if is not possible - grat for debuging
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Error writing transactions.csv",
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
            }


////////////////////lock the app after check out///////////////

            // Lock inputs
            setItemInputsEnabled(idField, qtyField, false);
// Disable buttons
            searchButton.setEnabled(false);   // Find Item / Search
            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
            checkoutButton.setEnabled(false);

// Enable post-checkout actions
            newOrderButton.setEnabled(true);
            exitButton.setEnabled(true);



        });
///////////////////////EXIT///////////////////////////////////////////
        exitButton.addActionListener(e -> System.exit(0));//exit the program
/// //////////////////NEW-ORDER/////////////////////////////////////////////
        newOrderButton.addActionListener(e -> {

            // Clear data structures
            cart.clear();
            cartTotals.clear();
            cartItemIds.clear();
            cartDescs.clear();
            cartPrices.clear();
            cartQtys.clear();
            cartDiscounts.clear();
            cartLineTotals.clear();


            // Clear cart display lines
            for (int i = 0; i < 5; i++) {
                cartLines[i].setText("");
            }
            cartHeader.setText("Your Shopping Cart Is Currently Empty");

            itemCount = 0;
            orderSubtotal = 0.0;

            subtotalField.setText(String.format("$%.2f", orderSubtotal));
            subtotalLabel.setText("Current Subtotal for 0 item(s):");


            // Clear UI fields
            idField.setText("");
            qtyField.setText("");
            detailField.setText("");     // if you use JTextArea
            // detailArea.setText("");  // if it's JTextField, same line works
////////////////////////////////////////////////
            //cartArea.setText("");       // clears cart display

            // Reset sved item
            NileDotComGUI.itemId = null;
            NileDotComGUI.description = null;
            NileDotComGUI.price = 0.0;
            NileDotComGUI.qty = 0;
            NileDotComGUI.discountRate = 0.0;
            NileDotComGUI.lineSubtotal = 0.0;

            //Unlock inputs
            setItemInputsEnabled(idField, qtyField, true);
            ;

            // reset buttons for a fresh order
            searchButton.setEnabled(true);
            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
            checkoutButton.setEnabled(false);

            // disable new order until next checkout
            newOrderButton.setEnabled(true);

            currentItemNumber = 1;

            // Update labels for the items
            setLabelItemNumber(currentItemNumber, idLabel, qtyLabel, detailsLabel,
                    searchButton, addButton, idField, qtyField);

        });
/// //////////////////////////////////////////////////////////////////////////////////////



//////////////build the main window layout////////////

        ///north panel, center panel, South panel
Container pane = frame.getContentPane();
        pane.removeAll();
        pane.setLayout(new BorderLayout(12, 12));

/////////panels///////////////////
        JPanel northPanel = new JPanel(new GridLayout(4, 2, 10, 10));   // 4 rows, 2 cols
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        JPanel southPanel = new JPanel(new GridLayout(3, 2, 12, 12));   // 3 rows, 2 cols

///a wrapper so we can put a title above the button grid
        JPanel southWrapper = new JPanel(new BorderLayout());
        southWrapper.setBackground(new Color(98, 0, 120)); // brown-ish


////title panel so spacing looks nice
        JPanel userControlsTitlePanel = new JPanel(new BorderLayout());
        userControlsTitlePanel.setBackground(new Color(98, 0, 120));
        userControlsTitlePanel.add(userControlsLabel, BorderLayout.CENTER);

////put title above the grid of buttons
        southWrapper.add(userControlsTitlePanel, BorderLayout.NORTH);
        southWrapper.add(southPanel, BorderLayout.CENTER);

////north panel, center panel, South panel
        pane.add(northPanel, BorderLayout.NORTH);
        pane.add(centerPanel, BorderLayout.CENTER);
        pane.add(southWrapper, BorderLayout.SOUTH);

///NORTH: ID, Qty,Details, Subtotal
        northPanel.add(idLabel);
        northPanel.add(idField);

        northPanel.add(qtyLabel);
        northPanel.add(qtyField);

        northPanel.add(detailsLabel);
        northPanel.add(detailField);

        northPanel.add(subtotalLabel);
        northPanel.add(subtotalField);

///center panel
        centerPanel.add(cartHeader);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(cartPanel);

///center panel stretch
        cartPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cartHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

//south buttons
        southPanel.add(searchButton);
        southPanel.add(addButton);

        southPanel.add(deleteButton);
        southPanel.add(checkoutButton);

        southPanel.add(newOrderButton);
        southPanel.add(exitButton);

// colors
        northPanel.setBackground(Color.DARK_GRAY);
        centerPanel.setBackground(Color.BLACK);
        southPanel.setBackground(new Color(98, 0, 120));
         idLabel.setForeground(Color.PINK);
        qtyLabel.setForeground(Color.PINK);
        detailsLabel.setForeground(Color.CYAN);
        subtotalLabel.setForeground(Color.CYAN);
        cartHeader.setForeground(Color.RED);
        cartHeader.setFont(new Font("SansSerif", Font.BOLD, 18));

// making the cart lines look like white bars:
        for (Component c : cartPanel.getComponents()) {
            if (c instanceof JTextField) {
                c.setBackground(Color.WHITE);
            }
        }

// Final frame settings
        frame.setSize(900, 650);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.revalidate();
        frame.repaint();





    }

    // This helper function determines the discount rate based on how many items the user is buying

    private static double getDiscountRate(int qty) {

        // Apply a 20% discount for orders of 15 or more items

        if (qty >= 15) return 0.20;

        // Apply a 15% discount for orders of 10–14 items

        if (qty >= 10) return 0.15;

        // Apply a 10% discount for orders of 5–9 items

        if (qty >= 5) return 0.10;

        // No discount if less than 5 items are purchased

        return 0.0;//return no discount
    }


    // Updates all labels, buttons, and input fields to reflect the current item number

    private static void setLabelItemNumber(
            int itemNum,
            JLabel idLabel, JLabel qtyLabel, JLabel detailsLabel,
            JButton searchButton, JButton addButton,
            JTextField idField, JTextField qtyField
    ) {
        // Update the item ID and the quantity label depending on what the user is entering

        idLabel.setText("Enter item ID for Item #" + itemNum + ":");
        qtyLabel.setText("Enter quantity for Item #" + itemNum + ":");

        detailsLabel.setText("Details for Item #" + itemNum + ":");//TO DO: this  is suposed to always match the current item being worked on

        // Update the Search button and the add button text to match the current item number

        searchButton.setText("Search For Item #" + itemNum);
        addButton.setText("Add Item #" + itemNum + " To Cart");

        // Clear the item ID field so the user can enter a new item

        idField.setText("");
        qtyField.setText("");
    }

    // Turns the item ID and quantity text fields on or off and
    // changes their color so the user can tell if they are usable or not

    private static void setItemInputsEnabled(JTextField idField, JTextField qtyField, boolean enabled) {

        // Enable or lock the item ID and the qtyField depending on their current state

        idField.setEditable(enabled);
        qtyField.setEditable(enabled);

        // If the fields should be usable (normal shopping state)

        if (enabled) {

            // Sets the background color of the text
            // boxes to white to indicate to the
            // user that the fields are usable

            idField.setBackground(Color.WHITE);
            qtyField.setBackground(Color.WHITE);
        } else {

            // Sets the background color of the text
            // boxes to light gray to indicate to the
            // user that the fields are locked

            idField.setBackground(Color.LIGHT_GRAY);
            qtyField.setBackground(Color.LIGHT_GRAY);
        }
    }


}
