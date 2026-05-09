package ecommerce.main;

/**
 * Class: Employee
 * Represents a warehouse employee with a role.
 */
public class Employee {

    private String employeeId;
    private String name;
    private EmployeeRole role;
    private String password;

    public Employee(String employeeId, String name, EmployeeRole role, String password) {
        this.employeeId = employeeId;
        this.name = name;
        this.role = role;
        this.password = password;
    }

    /**
     * Authenticate employee with password.
     */
    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    /**
     * Check if this employee can access a given module.
     * ADMIN can access all, others have restricted access.
     */
    public boolean canAccess(String module) {
        if (role == EmployeeRole.ADMIN) return true;
        if (role == EmployeeRole.WAREHOUSE_STAFF) {
            return module.equals("INVENTORY") || module.equals("STORAGE");
        }
        if (role == EmployeeRole.DELIVERY_MANAGER) {
            return module.equals("SHIPMENT");
        }
        return false;
    }

    // Getters
    public String getEmployeeId() { return employeeId; }
    public String getName()       { return name; }
    public EmployeeRole getRole() { return role; }

    @Override
    public String toString() {
        return "  Employee  : " + name + " (" + employeeId + ")" +
               "\n  Role      : " + role +
               "\n  Access    : " + role.getPermissions();
    }
}
