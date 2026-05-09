package ecommerce.main;

import ecommerce.exceptions.InsufficientStockException;
import ecommerce.exceptions.LowStockException;
import ecommerce.exceptions.ProductNotFoundException;
import ecommerce.products.Category;
import ecommerce.products.Product;
import ecommerce.products.ProductManager;
import ecommerce.shipment.ShipmentManager;
import ecommerce.warehouse.StorageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class: Main
 * Entry point of the Smart E-Commerce Warehouse & Storage Management System.
 *
 * Demonstrates:
 * - Full modular system using packages
 * - All Java concepts in action
 * - Interactive console UI
 */
public class Main {

    private static ProductManager  productManager;
    private static StorageManager  storageManager;
    private static ShipmentManager shipmentManager;
    private static Scanner         scanner;
    private static Employee        loggedInEmployee;

    // Pre-defined employee accounts
    private static Employee[] employees = {
        new Employee("EMP001", "Arjun Sharma",   EmployeeRole.ADMIN,            "admin123"),
        new Employee("EMP002", "Priya Patel",    EmployeeRole.WAREHOUSE_STAFF,  "staff123"),
        new Employee("EMP003", "Vikram Singh",   EmployeeRole.DELIVERY_MANAGER, "deliver123")
    };

    public static void main(String[] args) {

        scanner = new Scanner(System.in);

        printBanner();

        // Employee login
        if (!login()) {
            System.out.println("\n  ✘ Authentication failed. Exiting system.");
            return;
        }

        System.out.println("\n  Initializing Warehouse System...");
        productManager  = new ProductManager();
        storageManager  = new StorageManager();
        shipmentManager = new ShipmentManager(productManager);

        System.out.println("  ✔ System Ready!\n");
        pause(1000);

        // Main menu loop
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("  Enter your choice: ");

            switch (choice) {
                case 1:  addProduct();         break;
                case 2:  updateStock();        break;
                case 3:  removeProduct();      break;
                case 4:  searchProduct();      break;
                case 5:  displayInventory();   break;
                case 6:  warehouseMenu();      break;
                case 7:  shipmentMenu();       break;
                case 8:  lowStockReport();     break;
                case 9:  billingEstimate();    break;
                case 10: inventoryReport();    break;
                case 0:
                    System.out.println("\n  ╔══════════════════════════════════════════╗");
                    System.out.println("  ║  Thank you for using SmartWarehouse™     ║");
                    System.out.println("  ║  System exited safely. Goodbye!          ║");
                    System.out.println("  ╚══════════════════════════════════════════╝\n");
                    running = false;
                    break;
                default:
                    System.out.println("\n  ✘ Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    // ================================================================
    //  BANNER
    // ================================================================

    private static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════════════════╗");
        System.out.println("  ║                                                              ║");
        System.out.println("  ║     SmartWarehouse™ — E-Commerce Storage Management         ║");
        System.out.println("  ║     Version 1.0  |  Industrial Java Project                 ║");
        System.out.println("  ║                                                              ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    // ================================================================
    //  LOGIN
    // ================================================================

    private static boolean login() {
        System.out.println("  ┌─────────────────────────────────────────────┐");
        System.out.println("  │            EMPLOYEE LOGIN PORTAL            │");
        System.out.println("  ├─────────────────────────────────────────────┤");
        System.out.println("  │  Demo Accounts:                             │");
        System.out.println("  │  EMP001 / admin123    → Admin               │");
        System.out.println("  │  EMP002 / staff123    → Warehouse Staff     │");
        System.out.println("  │  EMP003 / deliver123  → Delivery Manager    │");
        System.out.println("  └─────────────────────────────────────────────┘");

        System.out.print("\n  Employee ID : ");
        String id = scanner.nextLine().trim();
        System.out.print("  Password    : ");
        String pwd = scanner.nextLine().trim();

        for (Employee emp : employees) {
            if (emp.getEmployeeId().equalsIgnoreCase(id) && emp.authenticate(pwd)) {
                loggedInEmployee = emp;
                System.out.println("\n  ╔══════════════════════════════════╗");
                System.out.println("  ║    ✔ LOGIN SUCCESSFUL            ║");
                System.out.println("  ╠══════════════════════════════════╣");
                System.out.println("  ║  Welcome, " + String.format("%-22s", emp.getName()) + "║");
                System.out.println("  ║  Role: " + String.format("%-25s", emp.getRole())  + "║");
                System.out.println("  ╚══════════════════════════════════╝");
                return true;
            }
        }
        return false;
    }

    // ================================================================
    //  MAIN MENU
    // ================================================================

    private static void printMainMenu() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════════════════╗");
        System.out.println("  ║         SmartWarehouse™ — MAIN MENU                         ║");
        System.out.printf ("  ║  Logged in as: %-20s (%-20s) ║%n",
                loggedInEmployee.getName(), loggedInEmployee.getRole());
        System.out.println("  ╠══════════════════════════════════════════════════════════════╣");
        System.out.println("  ║  PRODUCT MANAGEMENT                                          ║");
        System.out.println("  ║   1. Add New Product                                         ║");
        System.out.println("  ║   2. Update Stock Quantity                                   ║");
        System.out.println("  ║   3. Remove Product                                          ║");
        System.out.println("  ║   4. Search Product                                          ║");
        System.out.println("  ║   5. Display All Inventory                                   ║");
        System.out.println("  ╠══════════════════════════════════════════════════════════════╣");
        System.out.println("  ║  WAREHOUSE & SHIPMENT                                        ║");
        System.out.println("  ║   6. Warehouse Storage View                                  ║");
        System.out.println("  ║   7. Shipment Management                                     ║");
        System.out.println("  ╠══════════════════════════════════════════════════════════════╣");
        System.out.println("  ║  REPORTS & TOOLS                                             ║");
        System.out.println("  ║   8. Low Stock Alert Report                                  ║");
        System.out.println("  ║   9. Billing Estimate                                        ║");
        System.out.println("  ║  10. Inventory Summary Report                                ║");
        System.out.println("  ╠══════════════════════════════════════════════════════════════╣");
        System.out.println("  ║   0. Exit System                                             ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════════╝");
    }

    // ================================================================
    //  1. ADD PRODUCT
    // ================================================================

    private static void addProduct() {
        printSectionHeader("ADD NEW PRODUCT");

        System.out.print("  Product Name : ");
        String name = scanner.nextLine().trim();

        System.out.println("\n  Select Category:");
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.printf("    %2d. %-15s → %s%n",
                (i + 1), categories[i], categories[i].getAssignedRack());
        }

        int catChoice = readInt("  Your choice: ") - 1;
        if (catChoice < 0 || catChoice >= categories.length) {
            System.out.println("  ✘ Invalid category.");
            return;
        }
        Category category = categories[catChoice];

        System.out.print("  Price (Rs.)  : ");
        double price;
        try {
            price = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  ✘ Invalid price.");
            return;
        }

        int quantity = readInt("  Quantity     : ");

        Product newProduct = new Product(name, category, price, quantity);

        try {
            productManager.addProduct(newProduct);
            storageManager.allocateStorage(newProduct);
        } catch (LowStockException e) {
            System.out.println("\n  " + e.getMessage());
        }
    }

    // ================================================================
    //  2. UPDATE STOCK
    // ================================================================

    private static void updateStock() {
        printSectionHeader("UPDATE STOCK QUANTITY");

        System.out.print("  Product ID   : ");
        String id = scanner.nextLine().trim();
        int newQty = readInt("  New Quantity : ");

        try {
            productManager.updateStock(id, newQty);
        } catch (ProductNotFoundException e) {
            System.out.println("\n  " + e.getMessage());
        } catch (LowStockException e) {
            System.out.println("\n  " + e.getMessage());
        }
    }

    // ================================================================
    //  3. REMOVE PRODUCT
    // ================================================================

    private static void removeProduct() {
        printSectionHeader("REMOVE PRODUCT");

        System.out.print("  Product ID to remove : ");
        String id = scanner.nextLine().trim();

        try {
            Product p = productManager.findById(id);
            if (p != null) storageManager.freeStorage(p);
            productManager.removeProduct(id);
        } catch (ProductNotFoundException e) {
            System.out.println("\n  " + e.getMessage());
        }
    }

    // ================================================================
    //  4. SEARCH PRODUCT
    // ================================================================

    private static void searchProduct() {
        printSectionHeader("SEARCH PRODUCT");

        System.out.println("  Search by:");
        System.out.println("   1. Product Name (keyword)");
        System.out.println("   2. Product ID");
        System.out.println("   3. Category");
        int choice = readInt("  Choice: ");

        switch (choice) {
            case 1: {
                System.out.print("  Enter keyword : ");
                String keyword = scanner.nextLine().trim();
                List<Product> results = productManager.searchByName(keyword);
                if (results.isEmpty()) {
                    System.out.println("\n  No products found matching '" + keyword + "'");
                } else {
                    System.out.println("\n  Found " + results.size() + " result(s):");
                    for (Product p : results) {
                        System.out.println(p.toDetailedString());
                    }
                }
                break;
            }
            case 2: {
                System.out.print("  Enter Product ID : ");
                String id = scanner.nextLine().trim();
                Product p = productManager.findById(id);
                if (p == null) {
                    System.out.println("\n  ✘ Product not found: " + id);
                } else {
                    System.out.println(p.toDetailedString());
                }
                break;
            }
            case 3: {
                System.out.println("\n  Categories:");
                Category[] cats = Category.values();
                for (int i = 0; i < cats.length; i++) {
                    System.out.printf("    %2d. %s%n", (i + 1), cats[i]);
                }
                int catIdx = readInt("  Choice: ") - 1;
                if (catIdx < 0 || catIdx >= cats.length) {
                    System.out.println("  ✘ Invalid.");
                    return;
                }
                List<Product> results = productManager.searchByCategory(cats[catIdx]);
                if (results.isEmpty()) {
                    System.out.println("\n  No products in category: " + cats[catIdx]);
                } else {
                    System.out.println("\n  " + results.size() + " product(s) in " + cats[catIdx] + ":");
                    for (Product p : results) {
                        System.out.println(p.toDetailedString());
                    }
                }
                break;
            }
            default:
                System.out.println("  ✘ Invalid choice.");
        }
    }

    // ================================================================
    //  5. DISPLAY INVENTORY
    // ================================================================

    private static void displayInventory() {
        printSectionHeader("COMPLETE INVENTORY");
        System.out.println("  Total Products: " + productManager.getTotalCount());
        productManager.displayAll();
    }

    // ================================================================
    //  6. WAREHOUSE MENU
    // ================================================================

    private static void warehouseMenu() {
        printSectionHeader("WAREHOUSE STORAGE");
        System.out.println("   1. View Overall Warehouse Layout");
        System.out.println("   2. View Rack Capacity Report");
        System.out.println("   3. View Specific Rack Details");
        System.out.println("   0. Back");

        int choice = readInt("  Choice: ");
        switch (choice) {
            case 1: storageManager.displayWarehouse();  break;
            case 2: storageManager.generateReport();    break;
            case 3: {
                System.out.println("\n  Categories:");
                Category[] cats = Category.values();
                for (int i = 0; i < cats.length; i++) {
                    System.out.printf("    %2d. %-15s → %s%n", (i + 1), cats[i], cats[i].getAssignedRack());
                }
                int idx = readInt("  Choice: ") - 1;
                if (idx >= 0 && idx < cats.length) {
                    storageManager.displayRackDetails(cats[idx]);
                }
                break;
            }
            case 0: break;
            default: System.out.println("  ✘ Invalid choice.");
        }
    }

    // ================================================================
    //  7. SHIPMENT MENU
    // ================================================================

    private static void shipmentMenu() {
        printSectionHeader("SHIPMENT MANAGEMENT");
        System.out.println("   1. Receive Incoming Shipment");
        System.out.println("   2. Queue Outgoing Dispatch");
        System.out.println("   3. Process Next Dispatch");
        System.out.println("   4. Process All Dispatches");
        System.out.println("   5. Track a Shipment");
        System.out.println("   6. View Shipment Log");
        System.out.println("   7. View Dispatch Queue");
        System.out.println("   0. Back");

        int choice = readInt("  Choice: ");

        switch (choice) {
            case 1: {
                System.out.print("  Product ID   : ");
                String pid = scanner.nextLine().trim();
                int qty = readInt("  Quantity     : ");
                System.out.print("  Supplier     : ");
                String supplier = scanner.nextLine().trim();
                try {
                    shipmentManager.receiveShipment(pid, qty, supplier);
                } catch (ProductNotFoundException e) {
                    System.out.println("\n  " + e.getMessage());
                }
                break;
            }
            case 2: {
                System.out.print("  Product ID   : ");
                String pid = scanner.nextLine().trim();
                int qty = readInt("  Quantity     : ");
                System.out.print("  Destination  : ");
                String dest = scanner.nextLine().trim();
                try {
                    shipmentManager.queueDispatch(pid, qty, dest);
                } catch (ProductNotFoundException | InsufficientStockException | LowStockException e) {
                    System.out.println("\n  " + e.getMessage());
                }
                break;
            }
            case 3: {
                try {
                    shipmentManager.processNextDispatch();
                } catch (LowStockException | ProductNotFoundException e) {
                    System.out.println("\n  " + e.getMessage());
                }
                break;
            }
            case 4:
                shipmentManager.processAllDispatches();
                break;
            case 5: {
                System.out.print("  Tracking ID  : ");
                String tid = scanner.nextLine().trim();
                shipmentManager.trackShipmentById(tid);
                break;
            }
            case 6:
                shipmentManager.displayShipmentLog();
                break;
            case 7:
                shipmentManager.displayDispatchQueue();
                break;
            case 0:
                break;
            default:
                System.out.println("  ✘ Invalid choice.");
        }
    }

    // ================================================================
    //  8. LOW STOCK REPORT
    // ================================================================

    private static void lowStockReport() {
        printSectionHeader("LOW STOCK ALERT REPORT");

        List<Product> lowStockList = productManager.getLowStockProducts();

        if (lowStockList.isEmpty()) {
            System.out.println("\n  ✔ All products are adequately stocked. No alerts.");
            return;
        }

        System.out.println("\n  ⚠ WARNING: " + lowStockList.size() + " product(s) are low on stock!\n");
        System.out.println("  ┌──────────────────────────────────────────────────────────────┐");
        System.out.println("  │                    LOW STOCK PRODUCTS                        │");
        System.out.println("  ├──────────────────────────────────────────────────────────────┤");

        for (Product p : lowStockList) {
            // Demonstrate exception throwing and catching for each low-stock product
            try {
                if (p.isLowStock()) {
                    throw new LowStockException(p.getProductName(), p.getQuantity());
                }
            } catch (LowStockException e) {
                System.out.println("  │  " + e.getMessage());
                System.out.println("  │  Rack: " + p.getRackLocation() + " | ID: " + p.getProductId());
                System.out.println("  ├──────────────────────────────────────────────────────────────┤");
            }
        }
        System.out.println("  └──────────────────────────────────────────────────────────────┘");
        System.out.println("\n  ℹ ACTION REQUIRED: Please restock the above products immediately.");
    }

    // ================================================================
    //  9. BILLING ESTIMATE
    // ================================================================

    private static void billingEstimate() {
        printSectionHeader("BILLING ESTIMATE");

        System.out.print("  Product ID : ");
        String pid = scanner.nextLine().trim();
        int qty = readInt("  Quantity   : ");

        try {
            productManager.calculateBilling(pid, qty);
        } catch (ProductNotFoundException e) {
            System.out.println("\n  " + e.getMessage());
        }
    }

    // ================================================================
    //  10. INVENTORY REPORT
    // ================================================================

    private static void inventoryReport() {
        printSectionHeader("INVENTORY SUMMARY REPORT");
        productManager.generateReport();
    }

    // ================================================================
    //  UTILITY METHODS
    // ================================================================

    private static void printSectionHeader(String title) {
        System.out.println();
        System.out.println("  ┌──────────────────────────────────────────────────┐");
        System.out.printf ("  │  %-48s│%n", title);
        System.out.println("  └──────────────────────────────────────────────────┘");
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("  ✘ Please enter a valid number.");
            }
        }
    }

    private static void pause(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
