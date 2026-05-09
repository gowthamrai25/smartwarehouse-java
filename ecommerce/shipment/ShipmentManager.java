package ecommerce.shipment;

import ecommerce.exceptions.InsufficientStockException;
import ecommerce.exceptions.LowStockException;
import ecommerce.exceptions.ProductNotFoundException;
import ecommerce.products.Product;
import ecommerce.products.ProductManager;
import ecommerce.shipment.Shipment.ShipmentType;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

/**
 * Class: ShipmentManager
 * Manages all incoming and outgoing shipments.
 *
 * Demonstrates:
 * - Vector: shipment log (thread-safe, synchronized)
 * - Queue (LinkedList): pending delivery queue
 * - Interface usage via Trackable
 */
public class ShipmentManager {

    // Vector for shipment log — demonstrates Vector usage (thread-safe)
    private Vector<Shipment> shipmentLog;

    // Queue for pending outgoing deliveries — demonstrates Queue usage
    private Queue<Shipment> dispatchQueue;

    private ProductManager productManager;
    private int trackingCounter = 1000;

    public ShipmentManager(ProductManager productManager) {
        this.productManager = productManager;
        this.shipmentLog = new Vector<>();
        this.dispatchQueue = new LinkedList<>();
    }

    // ============================================================
    //  RECEIVE INCOMING SHIPMENT
    // ============================================================

    /**
     * Record an incoming shipment and update product stock.
     */
    public void receiveShipment(String productId, int quantity, String supplier)
            throws ProductNotFoundException {

        Product product = productManager.findById(productId);
        if (product == null) throw new ProductNotFoundException(productId);

        String trackId = "IN-" + (++trackingCounter);
        Shipment shipment = new Shipment(
            trackId, productId, product.getProductName(),
            quantity, ShipmentType.INCOMING,
            supplier, "Main Warehouse"
        );

        shipment.updateStatus("IN_TRANSIT");
        product.addStock(quantity);
        shipment.updateStatus("DELIVERED");

        shipmentLog.add(shipment);  // Add to Vector log

        System.out.println("\n  ✔ Incoming shipment received!");
        System.out.println("  Tracking ID : " + trackId);
        System.out.println("  Product     : " + product.getProductName());
        System.out.println("  Quantity    : +" + quantity + " units added to stock");
        System.out.println("  New Stock   : " + product.getQuantity() + " units");
    }

    // ============================================================
    //  DISPATCH OUTGOING SHIPMENT
    // ============================================================

    /**
     * Add an outgoing shipment to the dispatch queue.
     * @throws InsufficientStockException if not enough stock
     * @throws LowStockException if stock falls below threshold after dispatch
     */
    public void queueDispatch(String productId, int quantity, String destination)
            throws ProductNotFoundException, InsufficientStockException, LowStockException {

        Product product = productManager.findById(productId);
        if (product == null) throw new ProductNotFoundException(productId);

        if (product.getQuantity() < quantity) {
            throw new InsufficientStockException(product.getProductName(), quantity, product.getQuantity());
        }

        String trackId = "OUT-" + (++trackingCounter);
        Shipment shipment = new Shipment(
            trackId, productId, product.getProductName(),
            quantity, ShipmentType.OUTGOING,
            "Main Warehouse", destination
        );

        dispatchQueue.offer(shipment);  // Enqueue to Queue
        System.out.println("\n  ✔ Shipment queued for dispatch.");
        System.out.println("  Tracking ID      : " + trackId);
        System.out.println("  Product          : " + product.getProductName());
        System.out.println("  Quantity         : " + quantity + " units");
        System.out.println("  Destination      : " + destination);
        System.out.println("  Queue Size       : " + dispatchQueue.size() + " shipment(s) pending");
    }

    /**
     * Process the next shipment in the dispatch queue.
     * @throws LowStockException if remaining stock is low after processing
     */
    public void processNextDispatch() throws LowStockException, ProductNotFoundException {
        Shipment shipment = dispatchQueue.poll();  // Dequeue from Queue

        if (shipment == null) {
            System.out.println("\n  [No shipments in dispatch queue]");
            return;
        }

        Product product = productManager.findById(shipment.getProductId());
        if (product != null) {
            product.reduceStock(shipment.getQuantity());
        }

        shipment.updateStatus("IN_TRANSIT");
        shipment.updateStatus("DELIVERED");
        shipmentLog.add(shipment);

        System.out.println("\n  ✔ Shipment dispatched!");
        shipment.trackShipment();

        if (product != null && product.isLowStock()) {
            throw new LowStockException(product.getProductName(), product.getQuantity());
        }
    }

    /**
     * Process ALL shipments in the dispatch queue.
     */
    public void processAllDispatches() {
        if (dispatchQueue.isEmpty()) {
            System.out.println("\n  [Dispatch queue is empty — no shipments to process]");
            return;
        }

        System.out.println("\n  Processing " + dispatchQueue.size() + " shipment(s)...");
        int count = dispatchQueue.size();
        for (int i = 0; i < count; i++) {
            try {
                processNextDispatch();
            } catch (LowStockException e) {
                System.out.println("\n  " + e.getMessage());
            } catch (ProductNotFoundException e) {
                System.out.println("\n  " + e.getMessage());
            }
        }
    }

    // ============================================================
    //  TRACK SHIPMENT
    // ============================================================

    /**
     * Find a shipment by tracking ID and display its details.
     */
    public void trackShipmentById(String trackingId) {
        for (Shipment s : shipmentLog) {
            if (s.getTrackingId().equalsIgnoreCase(trackingId)) {
                s.trackShipment();
                return;
            }
        }
        // Check queue too
        for (Shipment s : dispatchQueue) {
            if (s.getTrackingId().equalsIgnoreCase(trackingId)) {
                s.trackShipment();
                return;
            }
        }
        System.out.println("\n  ✘ No shipment found with Tracking ID: " + trackingId);
    }

    // ============================================================
    //  DISPLAY SHIPMENT LOG
    // ============================================================

    /**
     * Display all completed shipments from the Vector log.
     */
    public void displayShipmentLog() {
        if (shipmentLog.isEmpty()) {
            System.out.println("\n  [No shipment records found]");
            return;
        }
        System.out.println("\n  ┌─────────────────┬──────────┬────────────────────────┬───────┬──────────────┬────────────┐");
        System.out.println("  │ Tracking ID     │ Type     │ Product                │  Qty  │ Status       │ Date       │");
        System.out.println("  ├─────────────────┼──────────┼────────────────────────┼───────┼──────────────┼────────────┤");
        for (Shipment s : shipmentLog) {
            System.out.println("  " + s);
        }
        System.out.println("  └─────────────────┴──────────┴────────────────────────┴───────┴──────────────┴────────────┘");
    }

    /**
     * Display pending dispatch queue.
     */
    public void displayDispatchQueue() {
        if (dispatchQueue.isEmpty()) {
            System.out.println("\n  [Dispatch queue is empty]");
            return;
        }
        System.out.println("\n  Pending Dispatch Queue (" + dispatchQueue.size() + " shipments):");
        System.out.println("  ┌─────────────────┬──────────┬────────────────────────┬───────┬──────────────┬────────────┐");
        System.out.println("  │ Tracking ID     │ Type     │ Product                │  Qty  │ Status       │ Date       │");
        System.out.println("  ├─────────────────┼──────────┼────────────────────────┼───────┼──────────────┼────────────┤");
        for (Shipment s : dispatchQueue) {
            System.out.println("  " + s);
        }
        System.out.println("  └─────────────────┴──────────┴────────────────────────┴───────┴──────────────┴────────────┘");
    }

    public Queue<Shipment> getDispatchQueue() { return dispatchQueue; }
    public Vector<Shipment> getShipmentLog()  { return shipmentLog; }
}
