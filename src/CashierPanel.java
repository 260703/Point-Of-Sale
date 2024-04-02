import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
public class CashierPanel extends JPanel {
    private final JTextArea billTextArea;
    private final JComboBox<String> productComboBox;
    private final JTextField quantityField; // Text field for entering quantity
    private JButton addButton;
    private JButton generateBillButton;
    private JButton fetchBillsButton; // Button for fetching bills
    private final Cashier cashier;
    private final Map<String, Integer> productsAdded; // Map to keep track of products and their quantities

    public CashierPanel(Cashier cashier) {
        this.cashier = cashier;
        this.productsAdded = new HashMap<>();

        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel productLabel = new JLabel("Select or Search Product:");
        inputPanel.add(productLabel);

        productComboBox = new JComboBox<>(getProductNamesFromDatabase()); // Populate product names from database
        productComboBox.setEditable(true); // Allow manual input for search
        inputPanel.add(productComboBox);

        quantityField = new JTextField(5);
        inputPanel.add(quantityField);

        addButton = new JButton("Add");
        addButton.addActionListener(e -> addToCart());
        inputPanel.add(addButton);

        generateBillButton = new JButton("Generate Bill"); // Button for generating bill
        generateBillButton.addActionListener(e -> generateBillAndResetTextArea());
        inputPanel.add(generateBillButton); // Adding the button to the panel

        fetchBillsButton = new JButton("Fetch Bills"); // Button for fetching bills
        fetchBillsButton.addActionListener(e -> fetchBills());
        inputPanel.add(fetchBillsButton); // Adding the button to the panel

        add(inputPanel, BorderLayout.NORTH);

        billTextArea = new JTextArea(10, 30);
        billTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(billTextArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addToCart() {
        String selectedProduct = (String) productComboBox.getSelectedItem();
        if (selectedProduct != null && !selectedProduct.isEmpty()) {
            int quantity = 1; // Default quantity is 1
            try {
                quantity = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException ex) {
                // If the quantity is not a valid integer, default to 1
            }
            double price = getProductPriceFromDatabase(selectedProduct);
            cashier.addItemToCart(selectedProduct, price * quantity);
            // Update the quantity of the product in the map
            productsAdded.put(selectedProduct, quantity);
            updateBill();
        }
    }

    private void generateBillAndResetTextArea() {
        ShoppingCart cart = cashier.getShoppingCart();
        try {
            // Insert bill into RecentBills table
            insertBillIntoDatabase(cart);
            // Reset bill text area
            billTextArea.setText("");
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle exception (e.g., log error, display error message)
        }
    }
    private void fetchBills() {
        // Fetch bills from RecentBills table
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT * FROM RecentBills");
             ResultSet resultSet = statement.executeQuery()) {

            // Create a DefaultTableModel to hold the fetched data
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("Product");
            model.addColumn("Quantity");
            model.addColumn("Price");

            // Populate the model with data from the ResultSet
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String product = resultSet.getString("product_id");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("price");
                model.addRow(new Object[]{id, product, quantity, price});
            }

            // Create a JTable with the populated model
            JTable table = new JTable(model);

            // Set column widths
            table.getColumnModel().getColumn(0).setPreferredWidth(50);
            table.getColumnModel().getColumn(1).setPreferredWidth(100);
            table.getColumnModel().getColumn(2).setPreferredWidth(80);
            table.getColumnModel().getColumn(3).setPreferredWidth(80);

            // Add the JTable to a JScrollPane
            JScrollPane scrollPane = new JScrollPane(table);

            // Create a new dialog to display the fetched bills
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Fetched Bills", true);
            dialog.setLayout(new BorderLayout());
            dialog.add(scrollPane, BorderLayout.CENTER);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(null); // Center the dialog on the screen
            dialog.setVisible(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle SQL exception
        }
    }


    private String[] getProductNamesFromDatabase() {
        List<String> productNames = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection()) {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT name FROM products");
            while (resultSet.next()) {
                productNames.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productNames.toArray(new String[productNames.size()]);
    }

    private double getProductPriceFromDatabase(String productName) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement statement = conn.prepareStatement("SELECT price FROM products WHERE name = ?");
            statement.setString(1, productName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Default to 0 if product price is not found
    }

    private void updateBill() {
        ShoppingCart cart = cashier.getShoppingCart();

        StringBuilder billBuilder = new StringBuilder();
        billBuilder.append("Items purchased:\n\n");

        // Add header row
        billBuilder.append(String.format("%-20s | %-10s | %-10s\n", "Product", "Quantity", "Price"));
        billBuilder.append("-".repeat(45)).append("\n");

        for (String itemName : cart.getItems().keySet()) {
            int quantity = productsAdded.getOrDefault(itemName, 0);
            double totalPriceForItem = cart.getItems().get(itemName);
            // Add item row
            billBuilder.append(String.format("%-20s | %-10s | $%-10.2f\n", itemName, "(x" + quantity + ")", totalPriceForItem));
        }

        // Add total row
        double total = cart.getItems().values().stream().mapToDouble(Double::doubleValue).sum();
        billBuilder.append("-".repeat(45)).append("\n");
        billBuilder.append(String.format("%-31s   | $%-10.2f", "Total:", total));

        billTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)); // Set monospaced font
        billTextArea.setText(billBuilder.toString());
    }

    private void insertBillIntoDatabase(ShoppingCart cart) throws SQLException {
        // Generate a unique identifier for the bill
        String billIdentifier = generateBillIdentifier();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement statement = conn.prepareStatement("INSERT INTO RecentBills (bill_identifier, product_id, quantity, price) VALUES (?, ?, ?, ?)")) {
            for (Map.Entry<String, Integer> entry : productsAdded.entrySet()) {
                String productName = entry.getKey();
                int quantity = entry.getValue();
                double price = cart.getItems().get(productName);

                // Fetch the product ID from the "products" table
                int productId = getProductIdFromDatabase(productName);

                statement.setString(1, billIdentifier);
                statement.setInt(2, productId);
                statement.setInt(3, quantity);
                statement.setDouble(4, price);
                statement.executeUpdate();
            }
        }
    }

    // Method to generate a unique identifier for the bill (e.g., timestamp)
    private String generateBillIdentifier() {
        return String.valueOf(System.currentTimeMillis());
    }



    private int getProductIdFromDatabase(String productName) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT id FROM products WHERE name = ?")) {
            statement.setString(1, productName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        }
        return -1; // Return -1 if product ID is not found (handle accordingly)
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Cashier cashier = new Cashier();
            JFrame frame = new JFrame("Cashier Interface");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new CashierPanel(cashier));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

