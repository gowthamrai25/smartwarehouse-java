package ecommerce.exceptions;

/**
 * Custom Exception for Low Stock Alert.
 * Thrown when product quantity falls below threshold.
 */
public class LowStockException extends Exception {

    private String productName;
    private int currentQuantity;
    private static final int THRESHOLD = 5;

    public LowStockException(String productName, int currentQuantity) {
        super("⚠ LOW STOCK ALERT: Product '" + productName +
              "' has only " + currentQuantity + " unit(s) left! (Threshold: " + THRESHOLD + ")");
        this.productName = productName;
        this.currentQuantity = currentQuantity;
    }

    public String getProductName() {
        return productName;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public int getThreshold() {
        return THRESHOLD;
    }
}
