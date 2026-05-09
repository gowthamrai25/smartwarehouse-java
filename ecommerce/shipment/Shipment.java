package ecommerce.shipment;

import ecommerce.interfaces.Trackable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class: Shipment
 * Represents an incoming or outgoing shipment.
 * Implements the Trackable interface — demonstrates interface usage.
 *
 * Demonstrates:
 * - Interface implementation (Trackable)
 * - Enum (ShipmentType, ShipmentStatus)
 */
public class Shipment implements Trackable {

    public enum ShipmentType { INCOMING, OUTGOING }

    public enum ShipmentStatus {
        PENDING, IN_TRANSIT, DELIVERED, CANCELLED
    }

    private String trackingId;
    private String productId;
    private String productName;
    private int quantity;
    private ShipmentType type;
    private ShipmentStatus status;
    private String origin;
    private String destination;
    private String createdAt;
    private String deliveredAt;

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    /**
     * Constructor for a new shipment.
     */
    public Shipment(String trackingId, String productId, String productName,
                    int quantity, ShipmentType type, String origin, String destination) {
        this.trackingId  = trackingId;
        this.productId   = productId;
        this.productName = productName;
        this.quantity    = quantity;
        this.type        = type;
        this.origin      = origin;
        this.destination = destination;
        this.status      = ShipmentStatus.PENDING;
        this.createdAt   = LocalDateTime.now().format(FORMATTER);
        this.deliveredAt = "-";
    }

    // ==================== Trackable Interface ====================

    @Override
    public void trackShipment() {
        String arrow = type == ShipmentType.INCOMING ? "→ WAREHOUSE" : "→ CUSTOMER";
        System.out.println("\n  ┌────────────────────────────────────────────┐");
        System.out.println("  │           SHIPMENT TRACKING INFO           │");
        System.out.println("  ├────────────────────────────────────────────┤");
        System.out.printf("  │  Tracking ID  : %-26s │%n", trackingId);
        System.out.printf("  │  Type         : %-26s │%n", type + " " + arrow);
        System.out.printf("  │  Product      : %-26s │%n", productName);
        System.out.printf("  │  Quantity     : %-26d │%n", quantity);
        System.out.printf("  │  From         : %-26s │%n", origin);
        System.out.printf("  │  To           : %-26s │%n", destination);
        System.out.printf("  │  Status       : %-26s │%n", status);
        System.out.printf("  │  Created At   : %-26s │%n", createdAt);
        System.out.printf("  │  Delivered At : %-26s │%n", deliveredAt);
        System.out.println("  └────────────────────────────────────────────┘");
    }

    @Override
    public String getTrackingId() {
        return trackingId;
    }

    @Override
    public boolean isDelivered() {
        return status == ShipmentStatus.DELIVERED;
    }

    @Override
    public void updateStatus(String newStatus) {
        try {
            this.status = ShipmentStatus.valueOf(newStatus.toUpperCase());
            if (this.status == ShipmentStatus.DELIVERED) {
                this.deliveredAt = LocalDateTime.now().format(FORMATTER);
            }
            System.out.println("  ✔ Shipment " + trackingId + " status updated to: " + this.status);
        } catch (IllegalArgumentException e) {
            System.out.println("  ✘ Invalid status: " + newStatus);
        }
    }

    // ==================== Getters ====================
    public String getProductId()   { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity()       { return quantity; }
    public ShipmentType getType()  { return type; }
    public ShipmentStatus getStatus() { return status; }
    public String getOrigin()      { return origin; }
    public String getDestination() { return destination; }
    public String getCreatedAt()   { return createdAt; }

    @Override
    public String toString() {
        return String.format("| %-15s | %-8s | %-22s | %5d | %-12s | %-10s |",
            trackingId, type, productName, quantity, status, createdAt.substring(0, 10));
    }
}
