import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnector {
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/point", "root", "root");
    }

    public static double getProductPrice(String productName) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT price FROM products WHERE name = ?")) {
            statement.setString(1, productName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("price");
            }
        }
        return 0.0; // Default to 0 if product price is not found
    }
}
