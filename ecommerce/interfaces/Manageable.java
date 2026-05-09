package ecommerce.interfaces;

/**
 * Interface: Manageable
 * Implemented by ProductManager and StorageManager.
 * Demonstrates polymorphism through interfaces.
 */
public interface Manageable {

    /**
     * Display all items managed by this manager.
     */
    void displayAll();

    /**
     * Get the total count of managed items.
     * @return count
     */
    int getTotalCount();

    /**
     * Generate a summary report.
     */
    void generateReport();
}
