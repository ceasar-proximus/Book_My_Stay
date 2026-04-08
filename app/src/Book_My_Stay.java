import java.util.*;

/**
 * Use Case 9: Error Handling & Validation (System Reliability).
 * Introduces structured guarding and custom exceptions.
 *
 * @author Karthik
 * @version 7.0
 */
public class Book_My_Stay {

    public static void main(String[] args) {
        System.out.println("****************************************");
        System.out.println("Welcome to Book My Stay!");
        System.out.println("System: Reliability & Validation (v7.0)");
        System.out.println("****************************************\n");

        RoomInventory inventory = new RoomInventory();
        inventory.updateAvailability("Single Room", 1);

        BookingService bookingService = new BookingService(inventory);

        // Test Cases for Validation
        Reservation[] testRequests = {
                new Reservation("", "Single Room"),          // Invalid: Empty Name
                new Reservation("Guest A", "Penthouse"),     // Invalid: Non-existent Type
                new Reservation("Guest B", "Single Room"),   // Valid
                new Reservation("Guest C", "Single Room")    // Invalid: Out of Stock
        };

        for (Reservation res : testRequests) {
            try {
                System.out.println("Processing: " + res.getGuestName() + " for " + res.getRequestedRoomType());
                bookingService.processBooking(res);
            } catch (BookingValidationException e) {
                // Graceful Failure Handling
                System.err.println("[VALIDATION ERROR] " + e.getMessage());
            } catch (Exception e) {
                System.err.println("[SYSTEM ERROR] An unexpected error occurred.");
            }
            System.out.println("--------------------------------------");
        }
    }
}

/**
 * Custom Exception for Domain-Specific Failures
 */
class BookingValidationException extends Exception {
    public BookingValidationException(String message) {
        super(message);
    }
}

/**
 * Use Case 9: Guarded Booking Service
 */
class BookingService {
    private final RoomInventory inventory;
    private int idCounter = 100;

    public BookingService(RoomInventory inventory) { this.inventory = inventory; }

    public String processBooking(Reservation res) throws BookingValidationException {
        // 1. Fail-Fast Input Validation
        if (res.getGuestName() == null || res.getGuestName().trim().isEmpty()) {
            throw new BookingValidationException("Guest name cannot be empty.");
        }

        String type = res.getRequestedRoomType();

        // 2. Validate Room Type Existence
        if (!inventory.hasRoomType(type)) {
            throw new BookingValidationException("Room type '" + type + "' does not exist in inventory.");
        }

        // 3. Guard System State (Check Availability)
        if (inventory.getAvailableCount(type) <= 0) {
            throw new BookingValidationException("No rooms available for type: " + type);
        }

        // 4. Proceed with Valid State
        String roomId = type.substring(0, 1) + (++idCounter);
        inventory.reduceAvailability(type);
        System.out.println("[SUCCESS] Room " + roomId + " assigned to " + res.getGuestName());
        return roomId;
    }
}

class RoomInventory {
    private final Map<String, Integer> inventoryMap = new HashMap<>();

    public void updateAvailability(String roomType, int count) {
        inventoryMap.put(roomType, count);
    }

    public boolean hasRoomType(String type) {
        return inventoryMap.containsKey(type);
    }

    public int getAvailableCount(String type) {
        return inventoryMap.getOrDefault(type, 0);
    }

    public void reduceAvailability(String type) throws BookingValidationException {
        int current = getAvailableCount(type);
        // Integrity check to prevent negative inventory
        if (current <= 0) {
            throw new BookingValidationException("Critical Error: Inventory synchronization failure.");
        }
        inventoryMap.put(type, current - 1);
    }
}

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
