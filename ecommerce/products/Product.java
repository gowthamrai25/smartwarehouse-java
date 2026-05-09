package ecommerce.products;

/**
 * Class: Product
 * Represents a product stored in the warehouse.
 * Core entity of the system — demonstrates Classes & Objects.
 */
public class Product {

    // Static counter for auto-generating product IDs
    private static int idCounter = 1000;

    private String productId;
    private String productName;
    private Category category;
    private double price;
    private int quantity;
    private String rackLocation;   // Assigned by StorageManager
    private String section;        // Shelf section (e.g., A1, A2)
    private String shelfId;        // Specific shelf

    /**
     * Constructor: Creates a new product with auto-generated ID.
     */
    public Product(String productName, Category category, double price, int quantity) {
        this.productId = category.getCode() + "-" + (++idCounter);
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.rackLocation = category.getAssignedRack();     // Smart auto-allocation
        this.section = category.getCode() + "-S" + (idCounter % 5 + 1);
        this.shelfId = "SH-" + idCounter;
    }

    /**
     * Constructor: Creates a product with a manually specified ID (for loading from file).
     */
    public Product(String productId, String productName, Category category, double price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.rackLocation = category.getAssignedRack();
        this.section = category.getCode() + "-S1";
        this.shelfId = "SH-" + productId;
    }

    // ==================== Getters ====================

    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Category getCategory() { return category; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getRackLocation() { return rackLocation; }
    public String getSection() { return section; }
    public String getShelfId() { return shelfId; }

    // ==================== Setters ====================

    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setProductName(String productName) { this.productName = productName; }

    /**
     * Add stock to this product.
     */
    public void addStock(int amount) {
        this.quantity += amount;
    }

    /**
     * Reduce stock from this product.
     * Returns false if insufficient stock.
     */
    public boolean reduceStock(int amount) {
        if (this.quantity < amount) return false;
        this.quantity -= amount;
        return true;
    }

    /**
     * Check if product has low stock (below threshold of 5).
     */
    public boolean isLowStock() {
        return this.quantity < 5;
    }

    /**
     * Calculate total value of this product's stock.
     */
    public double getTotalValue() {
        return price * quantity;
    }

    /**
     * String representation — used for display.
     */
    @Override
    public String toString() {
        return String.format(
            "| %-10s | %-22s | %-12s | %8.2f | %5d | %-10s | %-8s |",
            productId, productName, category, price, quantity, rackLocation, shelfId
        );
    }

    /**
     * Detailed view of the product.
     */
    public String toDetailedString() {
        return "\n  Product ID   : " + productId +
               "\n  Name         : " + productName +
               "\n  Category     : " + category +
               "\n  Price        : Rs. " + String.format("%.2f", price) +
               "\n  Quantity     : " + quantity + " units" +
               "\n  Rack         : " + rackLocation +
               "\n  Section      : " + section +
               "\n  Shelf ID     : " + shelfId +
               "\n  Total Value  : Rs. " + String.format("%.2f", getTotalValue()) +
               (isLowStock() ? "\n  ⚠ STATUS     : LOW STOCK!" : "\n  STATUS       : OK");
    }
}
