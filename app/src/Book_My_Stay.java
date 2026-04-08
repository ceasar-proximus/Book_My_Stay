import java.util.*;
import java.util.concurrent.*;

/**
 * Use Case 11: Concurrent Booking Simulation (Thread Safety).
 * Demonstrates safe resource sharing in a multi-user environment.
 *
 * @author Karthik
 * @version 9.0
 */
public class Book_My_Stay {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("****************************************");
        System.out.println("Welcome to Book My Stay!");
        System.out.println("System: Concurrent Multi-User (v9.0)");
        System.out.println("****************************************\n");

        // 1. Setup Shared Resources
        RoomInventory inventory = new RoomInventory();
        inventory.updateAvailability("Luxury Suite", 2); // Only 2 rooms for many guests
        BookingService bookingService = new BookingService(inventory);

        // 2. Simulate 5 Guests attempting to book 2 rooms simultaneously
        ExecutorService executor = Executors.newFixedThreadPool(5);
        String[] guests = {"Guest 1", "Guest 2", "Guest 3", "Guest 4", "Guest 5"};

        System.out.println("--- Starting Concurrent Requests ---");
        for (String name : guests) {
            executor.execute(() -> {
                try {
                    bookingService.processBooking(new Reservation(name, "Luxury Suite"));
                } catch (Exception e) {
                    System.err.println("[" + name + "] Error: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // 3. Final Integrity Check
        System.out.println("\n--- Final System State ---");
        System.out.println("Remaining Inventory: " + inventory.getAvailableCount("Luxury Suite"));
        bookingService.displayAllocations();
    }
}

/**
 * Use Case 11: Thread-Safe Booking Service
 */
class BookingService {
    private final RoomInventory inventory;
    private final Map<String, Set<String>> allocatedRooms = new HashMap<>();
    private int idCounter = 100;

    public BookingService(RoomInventory inventory) { this.inventory = inventory; }

    /**
     * The 'synchronized' keyword ensures that only one thread can
     * perform the check-and-update logic at a time.
     */
    public synchronized String processBooking(Reservation reservation) throws Exception {
        String type = reservation.getRequestedRoomType();

        // Critical Section: Check inventory and allocate
        if (inventory.getAvailableCount(type) > 0) {
            // Artificial delay to simulate processing and highlight potential race conditions
            Thread.sleep(100);

            String roomId = type.substring(0, 1) + (++idCounter);
            inventory.reduceAvailability(type);

            allocatedRooms.putIfAbsent(type, new HashSet<>());
            allocatedRooms.get(type).add(roomId);

            System.out.println("[SUCCESS] " + reservation.getGuestName() + " secured " + roomId);
            return roomId;
        } else {
            System.out.println("[FAILED] " + reservation.getGuestName() + ": No availability.");
            return null;
        }
    }

    public synchronized void displayAllocations() {
        System.out.println("Current Allocations: " + allocatedRooms);
    }
}

/**
 * RoomInventory with thread-safe internal updates.
 */
class RoomInventory {
    // ConcurrentHashMap provides thread-safety for the map itself
    private final Map<String, Integer> inventoryMap = new ConcurrentHashMap<>();

    public void updateAvailability(String roomType, int count) {
        inventoryMap.put(roomType, count);
    }

    public int getAvailableCount(String type) {
        return inventoryMap.getOrDefault(type, 0);
    }

    public void reduceAvailability(String type) {
        // Atomic-like update: inventory logic is wrapped by caller's synchronization
        inventoryMap.put(type, inventoryMap.get(type) - 1);
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
