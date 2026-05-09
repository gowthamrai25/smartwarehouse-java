package ecommerce.warehouse;

import ecommerce.interfaces.Manageable;
import ecommerce.products.Category;
import ecommerce.products.Product;

import java.util.ArrayList;

/**
 * Class: StorageManager
 * Manages the warehouse racks and product storage allocation.
 * Uses Generics and implements Manageable interface.
 *
 * Demonstrates:
 * - Generics (ArrayList<Rack>)
 * - Smart Rack Allocation (auto-assign by category)
 * - Interface implementation
 */
public class StorageManager implements Manageable {

    // Generics usage: typed ArrayList
    private ArrayList<Rack> racks;

    public StorageManager() {
        racks = new ArrayList<>();
        initializeWarehouse();
    }

    /**
     * Initialize all warehouse racks — one per category.
     */
    private void initializeWarehouse() {
        for (Category cat : Category.values()) {
            String rackId = cat.getCode();
            String rackName = cat.getAssignedRack();
            racks.add(new Rack(rackId, rackName, cat));
        }
        System.out.println("  [Warehouse initialized with " + racks.size() + " rack sections]");
    }

    /**
     * Smart Rack Allocation:
     * Automatically finds the rack matching the product's category.
     * @return the rack assigned to this product's category
     */
    public Rack getRackForCategory(Category category) {
        for (Rack rack : racks) {
            if (rack.getCategory() == category) {
                return rack;
            }
        }
        return null;
    }

    /**
     * Allocate storage for a product based on its category.
     * Returns the shelf ID assigned.
     */
    public String allocateStorage(Product product) {
        Rack rack = getRackForCategory(product.getCategory());
        if (rack == null) return null;

        if (!rack.hasSpace()) {
            System.out.println("  ⚠ Rack " + rack.getRackName() + " is FULL! Cannot allocate space.");
            return null;
        }
        return rack.allocateShelf(product.getProductId());
    }

    /**
     * Free storage when a product is removed.
     */
    public void freeStorage(Product product) {
        Rack rack = getRackForCategory(product.getCategory());
        if (rack != null) {
            rack.freeShelf(product.getProductId());
        }
    }

    /**
     * Display the entire warehouse layout.
     */
    public void displayWarehouse() {
        System.out.println("\n  ╔══════════════════════════════════════════════════╗");
        System.out.println("  ║           WAREHOUSE STORAGE LAYOUT               ║");
        System.out.println("  ╠══════════════════════════════════════════════════╣");

        int totalCapacity = 0;
        int totalUsed = 0;

        for (Rack rack : racks) {
            totalCapacity += rack.getTotalShelves();
            totalUsed += rack.getUsedShelves();
            System.out.printf("  ║  %-10s | %-12s | %2d/%2d shelves used  ║%n",
                rack.getRackName(), rack.getCategory(),
                rack.getUsedShelves(), rack.getTotalShelves());
        }

        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.printf("  ║  TOTAL CAPACITY : %-30s ║%n", totalUsed + "/" + totalCapacity + " shelves");
        System.out.printf("  ║  UTILIZATION    : %-30s ║%n",
                String.format("%.1f%%", (totalUsed * 100.0 / totalCapacity)));
        System.out.println("  ╚══════════════════════════════════════════════════╝");
    }

    /**
     * Display detailed rack view for a specific category.
     */
    public void displayRackDetails(Category category) {
        Rack rack = getRackForCategory(category);
        if (rack != null) {
            rack.displayRack();
        } else {
            System.out.println("  No rack found for category: " + category);
        }
    }

    // ==================== Manageable Interface ====================

    @Override
    public void displayAll() {
        displayWarehouse();
    }

    @Override
    public int getTotalCount() {
        return racks.size();
    }

    @Override
    public void generateReport() {
        System.out.println("\n  ╔═══════════════════════════════════════╗");
        System.out.println("  ║       WAREHOUSE CAPACITY REPORT       ║");
        System.out.println("  ╠═══════════════════════════════════════╣");
        for (Rack rack : racks) {
            int pct = (rack.getUsedShelves() * 100) / rack.getTotalShelves();
            String bar = buildBar(pct);
            System.out.printf("  ║  %-8s [%-20s] %3d%%  ║%n",
                    rack.getRackName(), bar, pct);
        }
        System.out.println("  ╚═══════════════════════════════════════╝");
    }

    private String buildBar(int pct) {
        int filled = pct / 5;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            sb.append(i < filled ? "█" : "░");
        }
        return sb.toString();
    }

    public ArrayList<Rack> getRacks() {
        return racks;
    }
}
