
//public class Cashier {
//    private ShoppingCart shoppingCart;
//
//    public Cashier() {
//        shoppingCart = new ShoppingCart();
//    }
//
//    public ShoppingCart getShoppingCart() {
//        return shoppingCart;
//    }
//
//    public void addItemToCart(String itemName, double price) {
//        shoppingCart.addItem(itemName, price);
//    }
//}
import java.sql.SQLException;

public class Cashier {
    private ShoppingCart shoppingCart;

    public Cashier() {
        shoppingCart = new ShoppingCart();
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void addItemToCart(String itemName, double v) {
        try {
            double price = DatabaseConnector.getProductPrice(itemName);
            shoppingCart.addItem(itemName, price);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception (e.g., log error, display error message)
        }
    }
}
