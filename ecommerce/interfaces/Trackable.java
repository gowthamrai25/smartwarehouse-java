package ecommerce.interfaces;

/**
 * Interface: Trackable
 * All shipments must implement this interface.
 * Demonstrates interface usage and abstraction in Java.
 */
public interface Trackable {

    /**
     * Track and display current shipment status.
     */
    void trackShipment();

    /**
     * Get the tracking ID of the shipment.
     * @return tracking ID string
     */
    String getTrackingId();

    /**
     * Check if the shipment has been delivered.
     * @return true if delivered
     */
    boolean isDelivered();

    /**
     * Update the current status of the shipment.
     * @param status new status string
     */
    void updateStatus(String status);
}
