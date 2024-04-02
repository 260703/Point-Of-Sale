
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
    private Map<String, Double> items;

    public ShoppingCart() {
        items = new HashMap<>();
    }

    public void addItem(String itemName, double price) {
        items.put(itemName, price);
    }

    public Map<String, Double> getItems() {
        return items;
    }


}
