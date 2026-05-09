package ecommerce.exceptions;

/**
 * Exception thrown when a product is not found in inventory.
 */
public class ProductNotFoundException extends Exception {

    public ProductNotFoundException(String identifier) {
        super("✘ PRODUCT NOT FOUND: No product matches '" + identifier + "' in the inventory.");
    }
}
