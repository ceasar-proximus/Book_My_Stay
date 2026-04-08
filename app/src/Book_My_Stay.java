import java.util.*;

/**
 * The entry point for the Hotel Booking Management System.
 * Use Case 6: Reservation Confirmation & Room Allocation.
 *
 * @author Karthik
 * @version 4.0
 */
public class Book_My_Stay {

    public static void main(String[] args) {
        System.out.println("****************************************");
        System.out.println("Welcome to Book My Stay!");
        System.out.println("System: Hotel Booking Management (v4.0)");
        System.out.println("****************************************\n");

        // 1. Initialize System Components
        RoomInventory inventory = new RoomInventory();
        inventory.updateAvailability("Single Room", 2); // Limited stock for testing
        inventory.updateAvailability("Double Room", 1);

        BookingQueue bookingQueue = new BookingQueue();
        BookingService bookingService = new BookingService(inventory);

        // 2. Intake: Guests submit requests (Use Case 5)
        bookingQueue.addRequest(new Reservation("Guest A", "Single Room"));
        bookingQueue.addRequest(new Reservation("Guest B", "Single Room"));
        bookingQueue.addRequest(new Reservation("Guest C", "Single Room")); // Will fail (out of stock)
        bookingQueue.addRequest(new Reservation("Guest D", "Double Room"));

        // 3. Allocation: Process Queue (Use Case 6)
        System.out.println("\n--- Processing Booking Queue (FIFO) ---");
        while (bookingQueue.hasRequests()) {
            Reservation request = bookingQueue.nextRequest();
            bookingService.processBooking(request);
        }

        // 4. Final State Verification
        bookingService.displayAllocations();
    }
}

/**
 * Use Case 6: Booking Service
 * Handles room allocation, uniqueness enforcement, and inventory synchronization.
 */
class BookingService {
    private final RoomInventory inventory;
    // Map of Room Type -> Set of Assigned Unique Room IDs (Uniqueness Enforcement)
    private final Map<String, Set<String>> allocatedRooms;
    private int idCounter = 100; // Helper for generating unique IDs

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
        this.allocatedRooms = new HashMap<>();
    }

    public void processBooking(Reservation reservation) {
        String type = reservation.getRequestedRoomType();

        // 1. Check Availability
        if (inventory.getAvailableCount(type) > 0) {
            // 2. Generate Unique Room ID
            String roomId = type.substring(0, 1) + (++idCounter);

            // 3. Prevent Double Booking (Set ensures uniqueness)
            allocatedRooms.putIfAbsent(type, new HashSet<>());
            allocatedRooms.get(type).add(roomId);

            // 4. Synchronize Inventory (Atomic-like update)
            inventory.reduceAvailability(type);

            System.out.println("[CONFIRMED] " + reservation.getGuestName() +
                    " assigned Room ID: " + roomId + " (" + type + ")");
        } else {
            System.out.println("[FAILED] " + reservation.getGuestName() +
                    " - No " + type + "s available.");
        }
    }

    public void displayAllocations() {
        System.out.println("\n--- Final Allocation Report ---");
        allocatedRooms.forEach((type, ids) ->
                System.out.println(type + " Assignments: " + ids));
    }
}

/**
 * Use Case 5: Request Intake Logic
 */
class BookingQueue {
    private final Queue<Reservation> requestQueue = new LinkedList<>();

    public void addRequest(Reservation request) {
        requestQueue.add(request);
    }

    public Reservation nextRequest() { return requestQueue.poll(); }
    public boolean hasRequests() { return !requestQueue.isEmpty(); }
}

/**
 * Use Case 5: Reservation Domain Object
 */
class Reservation {
    private final String guestName;
    private final String requestedRoomType;

    public Reservation(String guestName, String requestedRoomType) {
        this.guestName = guestName;
        this.requestedRoomType = requestedRoomType;
    }

    public String getGuestName() { return guestName; }
    public String getRequestedRoomType() { return requestedRoomType; }
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

    public void reduceAvailability(String roomType) {
        inventoryMap.put(roomType, getAvailableCount(roomType) - 1);
    }
}
