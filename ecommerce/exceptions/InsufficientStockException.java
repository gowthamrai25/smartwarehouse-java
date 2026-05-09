package ecommerce.exceptions;

/**
 * Exception thrown when trying to dispatch more than available stock.
 */
public class InsufficientStockException extends Exception {

    public InsufficientStockException(String productName, int requested, int available) {
        super("✘ DISPATCH FAILED: Cannot dispatch " + requested + " unit(s) of '" +
              productName + "'. Only " + available + " unit(s) available.");
    }
}
