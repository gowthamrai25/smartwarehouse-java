package ecommerce.main;

/**
 * Enum: EmployeeRole
 * Defines access roles for the Employee Access System.
 */
public enum EmployeeRole {
    ADMIN("Full Access — All modules"),
    WAREHOUSE_STAFF("Inventory & Storage only"),
    DELIVERY_MANAGER("Shipment & Dispatch only");

    private final String permissions;

    EmployeeRole(String permissions) {
        this.permissions = permissions;
    }

    public String getPermissions() {
        return permissions;
    }
}
