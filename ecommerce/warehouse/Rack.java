package ecommerce.warehouse;

import ecommerce.products.Category;

/**
 * Class: Rack
 * Represents a physical storage rack in the warehouse.
 * Uses an Array to store shelf slots — demonstrates Array usage.
 */
public class Rack {

    private String rackId;
    private String rackName;
    private Category category;
    private int totalShelves;
    private String[] shelves;      // Array for fixed rack shelves
    private int usedShelves;

    private static final int DEFAULT_SHELVES = 10;

    public Rack(String rackId, String rackName, Category category) {
        this.rackId = rackId;
        this.rackName = rackName;
        this.category = category;
        this.totalShelves = DEFAULT_SHELVES;
        this.shelves = new String[totalShelves];  // Fixed-size Array
        this.usedShelves = 0;

        // Initialize shelf labels
        for (int i = 0; i < totalShelves; i++) {
            shelves[i] = "EMPTY";
        }
    }

    /**
     * Allocate a shelf slot for a product.
     * @return shelf ID if successful, null if rack is full
     */
    public String allocateShelf(String productId) {
        for (int i = 0; i < totalShelves; i++) {
            if (shelves[i].equals("EMPTY")) {
                shelves[i] = productId;
                usedShelves++;
                return rackId + "-SH" + (i + 1);
            }
        }
        return null;  // Rack full
    }

    /**
     * Free a shelf slot when a product is removed.
     */
    public boolean freeShelf(String productId) {
        for (int i = 0; i < totalShelves; i++) {
            if (shelves[i].equals(productId)) {
                shelves[i] = "EMPTY";
                usedShelves--;
                return true;
            }
        }
        return false;
    }

    /**
     * Check if this rack has available space.
     */
    public boolean hasSpace() {
        return usedShelves < totalShelves;
    }

    /**
     * Display the rack layout with shelf occupancy.
     */
    public void displayRack() {
        System.out.println("\n  ┌──────────────────────────────────────────────┐");
        System.out.printf("  │  RACK: %-10s | Category: %-12s│%n", rackName, category);
        System.out.printf("  │  Capacity: %d shelves | Used: %d | Free: %d     │%n",
                totalShelves, usedShelves, totalShelves - usedShelves);
        System.out.println("  ├──────────────────────────────────────────────┤");
        for (int i = 0; i < totalShelves; i++) {
            String status = shelves[i].equals("EMPTY") ? "[ EMPTY ]" : "[" + shelves[i] + "]";
            System.out.printf("  │  Shelf %-2d : %-33s│%n", (i + 1), status);
        }
        System.out.println("  └──────────────────────────────────────────────┘");
    }

    // Getters
    public String getRackId() { return rackId; }
    public String getRackName() { return rackName; }
    public Category getCategory() { return category; }
    public int getTotalShelves() { return totalShelves; }
    public int getUsedShelves() { return usedShelves; }
    public int getFreeShelves() { return totalShelves - usedShelves; }
}
