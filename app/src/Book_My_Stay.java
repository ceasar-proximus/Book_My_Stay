import java.util.*;

/**
 * The entry point for the Hotel Booking Management System.
 * Use Case 5: Booking Request Intake (FIFO Queue).
 *
 * @author Karthik
 * @version 3.0
 */
public class Book_My_Stay {

    public static void main(String[] args) {
        System.out.println("****************************************");
        System.out.println("Welcome to Book My Stay!");
        System.out.println("System: Hotel Booking Management (v3.0)");
        System.out.println("****************************************\n");

        // 1. Initialize System Components
        RoomInventory inventory = new RoomInventory();
        inventory.updateAvailability("Single Room", 5);
        inventory.updateAvailability("Double Room", 3);

        // Catalog for search/reference
        Map<String, Room> roomCatalog = Map.of(
                "Single Room", new SingleRoom(101, 1500.0),
                "Double Room", new DoubleRoom(201, 2500.0)
        );

        BookingQueue bookingQueue = new BookingQueue();

        // 2. Simulating Incoming Booking Requests (First-Come-First-Served)
        System.out.println("--- Guest Actions: Submitting Booking Requests ---");

        bookingQueue.addRequest(new Reservation("Guest A", "Single Room"));
        bookingQueue.addRequest(new Reservation("Guest B", "Double Room"));
        bookingQueue.addRequest(new Reservation("Guest C", "Single Room"));

        // 3. Display Queue State
        // Note: No room allocation or inventory mutation has occurred yet.
        bookingQueue.displayQueue();

        System.out.println("\n[System Check] Requests are queued in arrival order.");
        System.out.println("[System Check] Inventory remains unchanged during intake.");
    }
}

/**
 * Use Case 5: Reservation Domain Object
 * Represents a guest's intent to book.
 */
class Reservation {
    private final String guestName;
    private final String requestedRoomType;
    private final long timestamp;

    public Reservation(String guestName, String requestedRoomType) {
        this.guestName = guestName;
        this.requestedRoomType = requestedRoomType;
        this.timestamp = System.currentTimeMillis();
    }

    public String getGuestName() { return guestName; }
    public String getRequestedRoomType() { return requestedRoomType; }

    @Override
    public String toString() {
        return String.format("Request[Guest: %s, Room: %s]", guestName, requestedRoomType);
    }
}

/**
 * Use Case 5: Booking Request Queue
 * Manages intake using FIFO principle to ensure fairness.
 */
class BookingQueue {
    // LinkedList implements the Queue interface for FIFO behavior
    private final Queue<Reservation> requestQueue = new LinkedList<>();

    public void addRequest(Reservation request) {
        requestQueue.add(request);
        System.out.println("Added to Queue: " + request);
    }

    public Reservation nextRequest() {
        return requestQueue.poll();
    }

    public void displayQueue() {
        System.out.println("\n--- Current Booking Queue (Arrival Order) ---");
        if (requestQueue.isEmpty()) {
            System.out.println("Queue is empty.");
        } else {
            requestQueue.forEach(req -> System.out.println(" > " + req));
        }
    }
}

/**
 * Use Case 3 & 4: Room Inventory (State Holder)
 */
class RoomInventory {
    private final Map<String, Integer> inventoryMap = new HashMap<>();

    public void updateAvailability(String roomType, int count) {
        inventoryMap.put(roomType, count);
    }

    public int getAvailableCount(String roomType) {
        return inventoryMap.getOrDefault(roomType, 0);
    }
}

/**
 * Domain Models for Rooms
 */
abstract class Room {
    private final double price;
    private final String type;

    public Room(double price, String type) {
        this.price = price;
        this.type = type;
    }

    public String getType() { return type; }
    public double getPrice() { return price; }
    public abstract String getFeatures();
}

class SingleRoom extends Room {
    public SingleRoom(int roomNum, double price) { super(price, "Single Room"); }
    @Override public String getFeatures() { return "1 Single Bed, WiFi"; }
}

class DoubleRoom extends Room {
    public DoubleRoom(int roomNum, double price) { super(price, "Double Room"); }
    @Override public String getFeatures() { return "2 Queen Beds, Mini Fridge"; }
}

class SuiteRoom extends Room {
    public SuiteRoom(int roomNum, double price) { super(price, "Luxury Suite"); }
    @Override public String getFeatures() { return "King Bed, Personal Bar"; }
}
