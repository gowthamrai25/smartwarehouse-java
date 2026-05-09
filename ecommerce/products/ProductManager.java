package ecommerce.products;

import ecommerce.exceptions.LowStockException;
import ecommerce.exceptions.ProductNotFoundException;
import ecommerce.interfaces.Manageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: ProductManager
 * Manages the product inventory using ArrayList.
 * Implements the Manageable interface.
 * Demonstrates: ArrayList, Generics, Interface implementation, String methods.
 */
public class ProductManager implements Manageable {

    // ArrayList for dynamic inventory management
    private ArrayList<Product> inventory;

    public ProductManager() {
        this.inventory = new ArrayList<>();
        loadSampleData();  // Pre-load with sample products
    }

    /**
     * Load sample products to demonstrate the system.
     */
    private void loadSampleData() {
        inventory.add(new Product("Samsung Galaxy S23", Category.ELECTRONICS, 74999.00, 25));
        inventory.add(new Product("Apple AirPods Pro", Category.ELECTRONICS, 24999.00, 3));  // Low stock
        inventory.add(new Product("Levi's Jeans 511", Category.CLOTHING, 3499.00, 40));
        inventory.add(new Product("Basmati Rice 5kg", Category.GROCERY, 450.00, 100));
        inventory.add(new Product("Yoga Mat Premium", Category.SPORTS, 1299.00, 15));
        inventory.add(new Product("Head First Java", Category.BOOKS, 899.00, 4));          // Low stock
        inventory.add(new Product("LEGO Technic Set", Category.TOYS, 5999.00, 8));
        inventory.add(new Product("Office Chair Ergo", Category.FURNITURE, 12999.00, 2)); // Low stock
        inventory.add(new Product("Whey Protein 1kg", Category.SPORTS, 2499.00, 18));
        inventory.add(new Product("Vitamin C 500mg", Category.MEDICINE, 299.00, 60));
    }

    // ============================================================
    //  ADD PRODUCT
    // ============================================================

    /**
     * Add a new product to the inventory.
     * @throws LowStockException if added quantity is below threshold
     */
    public void addProduct(Product product) throws LowStockException {
        inventory.add(product);
        System.out.println("\n  ✔ Product '" + product.getProductName() + "' added successfully.");
        System.out.println("  Product ID : " + product.getProductId());
        System.out.println("  Rack       : " + product.getRackLocation() + " | Shelf: " + product.getShelfId());

        if (product.isLowStock()) {
            throw new LowStockException(product.getProductName(), product.getQuantity());
        }
    }

    // ============================================================
    //  REMOVE PRODUCT
    // ============================================================

    /**
     * Remove a product by its ID.
     * @throws ProductNotFoundException if product doesn't exist
     */
    public void removeProduct(String productId) throws ProductNotFoundException {
        Product found = findById(productId);
        if (found == null) {
            throw new ProductNotFoundException(productId);
        }
        inventory.remove(found);
        System.out.println("\n  ✔ Product '" + found.getProductName() + "' (ID: " + productId + ") removed successfully.");
    }

    // ============================================================
    //  UPDATE STOCK
    // ============================================================

    /**
     * Update the stock quantity of a product.
     * @throws ProductNotFoundException if product not found
     * @throws LowStockException if updated quantity falls below threshold
     */
    public void updateStock(String productId, int newQuantity)
            throws ProductNotFoundException, LowStockException {

        Product product = findById(productId);
        if (product == null) {
            throw new ProductNotFoundException(productId);
        }

        int oldQty = product.getQuantity();
        product.setQuantity(newQuantity);
        System.out.println("\n  ✔ Stock updated for '" + product.getProductName() + "'");
        System.out.println("  Old Quantity : " + oldQty + " units");
        System.out.println("  New Quantity : " + newQuantity + " units");

        if (product.isLowStock()) {
            throw new LowStockException(product.getProductName(), newQuantity);
        }
    }

    /**
     * Add stock to a product (for incoming shipments).
     * @throws ProductNotFoundException if product not found
     */
    public void addStock(String productId, int amount) throws ProductNotFoundException {
        Product product = findById(productId);
        if (product == null) {
            throw new ProductNotFoundException(productId);
        }
        product.addStock(amount);
        System.out.println("\n  ✔ Added " + amount + " units to '" + product.getProductName() + "'");
        System.out.println("  New Stock: " + product.getQuantity() + " units");
    }

    // ============================================================
    //  SEARCH PRODUCT — String methods: contains(), startsWith()
    // ============================================================

    /**
     * Search products by name using String.contains() and String.startsWith().
     * Returns a list of matching products.
     */
    public List<Product> searchByName(String keyword) {
        List<Product> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (Product p : inventory) {
            String lowerName = p.getProductName().toLowerCase();
            // Demonstrates String methods: contains() and startsWith()
            if (lowerName.contains(lowerKeyword) || lowerName.startsWith(lowerKeyword)) {
                results.add(p);
            }
        }
        return results;
    }

    /**
     * Search products by category.
     */
    public List<Product> searchByCategory(Category category) {
        List<Product> results = new ArrayList<>();
        for (Product p : inventory) {
            if (p.getCategory() == category) {
                results.add(p);
            }
        }
        return results;
    }

    /**
     * Find product by ID — returns null if not found.
     */
    public Product findById(String productId) {
        for (Product p : inventory) {
            if (p.getProductId().equalsIgnoreCase(productId)) {
                return p;
            }
        }
        return null;
    }

    // ============================================================
    //  LOW STOCK REPORT
    // ============================================================

    /**
     * Get list of all products with low stock (quantity < 5).
     */
    public List<Product> getLowStockProducts() {
        List<Product> lowStock = new ArrayList<>();
        for (Product p : inventory) {
            if (p.isLowStock()) {
                lowStock.add(p);
            }
        }
        return lowStock;
    }

    // ============================================================
    //  BILLING ESTIMATE
    // ============================================================

    /**
     * Calculate billing estimate for a product order.
     * Includes: price, tax (18% GST), storage cost, shipping fee.
     */
    public void calculateBilling(String productId, int quantity) throws ProductNotFoundException {
        Product product = findById(productId);
        if (product == null) throw new ProductNotFoundException(productId);

        double basePrice    = product.getPrice() * quantity;
        double gstTax       = basePrice * 0.18;           // 18% GST
        double storageCost  = quantity * 2.5;             // Rs.2.5 per unit storage
        double shippingFee  = basePrice > 5000 ? 0 : 99; // Free shipping above Rs.5000
        double totalAmount  = basePrice + gstTax + storageCost + shippingFee;

        System.out.println("\n  ┌─────────────────────────────────────┐");
        System.out.println("  │          BILLING ESTIMATE           │");
        System.out.println("  ├─────────────────────────────────────┤");
        System.out.printf("  │  Product     : %-20s │%n", product.getProductName());
        System.out.printf("  │  Quantity    : %-20d │%n", quantity);
        System.out.printf("  │  Unit Price  : Rs. %-16.2f │%n", product.getPrice());
        System.out.println("  ├─────────────────────────────────────┤");
        System.out.printf("  │  Base Price  : Rs. %-16.2f │%n", basePrice);
        System.out.printf("  │  GST (18%%)   : Rs. %-16.2f │%n", gstTax);
        System.out.printf("  │  Storage Fee : Rs. %-16.2f │%n", storageCost);
        System.out.printf("  │  Shipping    : Rs. %-16.2f │%n", shippingFee);
        System.out.println("  ├─────────────────────────────────────┤");
        System.out.printf("  │  TOTAL       : Rs. %-16.2f │%n", totalAmount);
        System.out.println("  └─────────────────────────────────────┘");
    }

    // ============================================================
    //  MANAGEABLE INTERFACE METHODS
    // ============================================================

    @Override
    public void displayAll() {
        if (inventory.isEmpty()) {
            System.out.println("\n  [No products in inventory]");
            return;
        }
        System.out.println("\n  ┌────────────┬────────────────────────┬──────────────┬──────────┬───────┬────────────┬──────────┐");
        System.out.println("  │ Product ID │ Product Name           │ Category     │    Price │  Qty  │ Rack       │ Shelf    │");
        System.out.println("  ├────────────┼────────────────────────┼──────────────┼──────────┼───────┼────────────┼──────────┤");
        for (Product p : inventory) {
            System.out.println("  " + p);
        }
        System.out.println("  └────────────┴────────────────────────┴──────────────┴──────────┴───────┴────────────┴──────────┘");
    }

    @Override
    public int getTotalCount() {
        return inventory.size();
    }

    @Override
    public void generateReport() {
        double totalValue = 0;
        int totalUnits = 0;
        for (Product p : inventory) {
            totalValue += p.getTotalValue();
            totalUnits += p.getQuantity();
        }
        System.out.println("\n  ╔═══════════════════════════════════════╗");
        System.out.println("  ║       INVENTORY SUMMARY REPORT        ║");
        System.out.println("  ╠═══════════════════════════════════════╣");
        System.out.printf("  ║  Total Products   : %-18d ║%n", inventory.size());
        System.out.printf("  ║  Total Units      : %-18d ║%n", totalUnits);
        System.out.printf("  ║  Total Value      : Rs. %-14.2f ║%n", totalValue);
        System.out.printf("  ║  Low Stock Items  : %-18d ║%n", getLowStockProducts().size());
        System.out.println("  ╚═══════════════════════════════════════╝");
    }

    // ============================================================
    //  GETTER
    // ============================================================

    public ArrayList<Product> getInventory() {
        return inventory;
    }
}
