package ecommerce.products;

/**
 * Enum: Category
 * Represents product categories in the warehouse.
 * Each category maps to a specific rack section.
 * Demonstrates Enum usage in Java.
 */
public enum Category {
    ELECTRONICS("Rack A", "EA"),
    GROCERY("Rack B", "GB"),
    CLOTHING("Rack C", "CL"),
    FURNITURE("Rack D", "FU"),
    SPORTS("Rack E", "SP"),
    BOOKS("Rack F", "BK"),
    TOYS("Rack G", "TY"),
    BEAUTY("Rack H", "BE"),
    MEDICINE("Rack I", "MD"),
    OTHER("Rack J", "OT");

    private final String assignedRack;
    private final String code;

    // Enum constructor
    Category(String assignedRack, String code) {
        this.assignedRack = assignedRack;
        this.code = code;
    }

    public String getAssignedRack() {
        return assignedRack;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
