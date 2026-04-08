import java.util.*;

/**
 * Use Case 10: Booking Cancellation & Inventory Rollback.
 * Introduces state reversal using a Stack-based LIFO logic.
 *
 * @author Karthik
 * @version 8.0
 */
public class Book_My_Stay {

    public static void main(String[] args) {
        System.out.println("****************************************");
        System.out.println("Welcome to Book My Stay!");
        System.out.println("System: Rollback & Recovery (v8.0)");
        System.out.println("****************************************\n");

        // 1. Setup Services
        RoomInventory inventory = new RoomInventory();
        inventory.updateAvailability("Single Room", 2);

        BookingService bookingService = new BookingService(inventory);
        CancellationService cancelService = new CancellationService(inventory, bookingService);

        try {
            // 2. Perform Bookings
            String id1 = bookingService.processBooking(new Reservation("Guest A", "Single Room"));
            String id2 = bookingService.processBooking(new Reservation("Guest B", "Single Room"));

            System.out.println("\nCurrent Inventory: " + inventory.getAvailableCount("Single Room"));

            // 3. Perform Cancellation (Rollback)
            System.out.println("\n--- Initiating Cancellation ---");
            cancelService.cancelBooking(id1, "Single Room");

            // 4. Verify System State
            System.out.println("Inventory after rollback: " + inventory.getAvailableCount("Single Room"));

            // 5. Re-booking using the rolled-back ID
            System.out.println("\n--- Processing New Booking ---");
            bookingService.processBooking(new Reservation("Guest C", "Single Room"));

        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }
}

/**
 * Use Case 10: Cancellation Service
 * Handles inventory restoration and room ID release.
 */
class CancellationService {
    private final RoomInventory inventory;
    private final BookingService bookingService;

    public CancellationService(RoomInventory inventory, BookingService bookingService) {
        this.inventory = inventory;
        this.bookingService = bookingService;
    }

    public void cancelBooking(String roomID, String roomType) throws BookingValidationException {
        // 1. Validate existence in active allocations
        if (!bookingService.getActiveAllocations().contains(roomID)) {
            throw new BookingValidationException("Cancellation Failed: Room ID " + roomID + " is not active.");
        }

        // 2. Perform Rollback: Release ID back to pool
        bookingService.releaseRoomID(roomID);

        // 3. State Reversal: Restore Inventory
        inventory.restoreInventory(roomType);

        System.out.println("[CANCELLED] Room " + roomID + " released and inventory restored.");
    }
}

class BookingService {
    private final RoomInventory inventory;
    private final Set<String> activeAllocations = new HashSet<>();
    // Stack used for LIFO rollback logic (tracking released IDs)
    private final Stack<String> releasedIds = new Stack<>();
    private int idCounter = 100;

    public BookingService(RoomInventory inventory) { this.inventory = inventory; }

    public String processBooking(Reservation res) throws BookingValidationException {
        String type = res.getRequestedRoomType();
        if (inventory.getAvailableCount(type) <= 0) {
            throw new BookingValidationException("No rooms available for " + type);
        }

        // Reuse an ID from the rollback stack if available, otherwise generate new
        String roomId = !releasedIds.isEmpty() ? releasedIds.pop() : type.substring(0, 1) + (++idCounter);

        inventory.reduceAvailability(type);
        activeAllocations.add(roomId);

        System.out.println("[SUCCESS] " + res.getGuestName() + " assigned Room: " + roomId);
        return roomId;
    }

    public void releaseRoomID(String roomID) {
        activeAllocations.remove(roomID);
        releasedIds.push(roomID); // LIFO: Last released is the first to be reused
    }

    public Set<String> getActiveAllocations() { return activeAllocations; }
}

class RoomInventory {
    private final Map<String, Integer> inventoryMap = new HashMap<>();

    public void updateAvailability(String roomType, int count) { inventoryMap.put(roomType, count); }
    public int getAvailableCount(String type) { return inventoryMap.getOrDefault(type, 0); }

    public void reduceAvailability(String type) {
        inventoryMap.put(type, inventoryMap.get(type) - 1);
    }

    public void restoreInventory(String type) {
        inventoryMap.put(type, inventoryMap.get(type) + 1);
    }
}

/* --- Standard Domain Objects maintained for continuity --- */
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

class BookingValidationException extends Exception {
    public BookingValidationException(String message) { super(message); }
}
