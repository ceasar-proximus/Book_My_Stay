import java.io.*;
import java.util.*;

/**
 * Use Case 12: Data Persistence & System Recovery.
 * Enables state to survive application restarts via Serialization.
 *
 * @author Karthik
 * @version 10.0
 */
public class Book_My_Stay {
    private static final String STORAGE_FILE = "hotel_state.ser";

    public static void main(String[] args) {
        System.out.println("****************************************");
        System.out.println("Welcome to Book My Stay!");
        System.out.println("System: Persistence & Recovery (v10.0)");
        System.out.println("****************************************\n");

        PersistenceService persistence = new PersistenceService(STORAGE_FILE);

        // 1. Attempt System Recovery
        System.out.println("--- System Startup: Loading Data ---");
        HotelState state = persistence.loadState();

        RoomInventory inventory = state.getInventory();
        BookingHistory history = state.getHistory();
        BookingService bookingService = new BookingService(inventory);

        // 2. Demonstrate Continuity
        if (history.getAllRecords().isEmpty()) {
            System.out.println("No previous records found. Starting fresh.");
            inventory.updateAvailability("Deluxe", 5);
        } else {
            System.out.println("Successfully recovered " + history.getAllRecords().size() + " records.");
            System.out.println("Current Deluxe Inventory: " + inventory.getAvailableCount("Deluxe"));
        }

        // 3. Process a new booking
        try {
            String id = bookingService.processBooking(new Reservation("Guest_" + System.currentTimeMillis() % 1000, "Deluxe"));
            if (id != null) history.recordBooking("Recent Guest", id, "Deluxe");
        } catch (Exception e) {
            System.err.println("Booking Error: " + e.getMessage());
        }

        // 4. Persistence: Save on Shutdown
        System.out.println("\n--- System Shutdown: Persisting Data ---");
        persistence.saveState(new HotelState(inventory, history));
        System.out.println("State saved to " + STORAGE_FILE + ". Run again to see recovery!");
    }
}

/**
 * Use Case 12: Persistence Service
 * Handles File I/O and Object Serialization.
 */
class PersistenceService {
    private final String fileName;

    public PersistenceService(String fileName) { this.fileName = fileName; }

    public void saveState(HotelState state) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(state);
        } catch (IOException e) {
            System.err.println("Save Failed: " + e.getMessage());
        }
    }

    public HotelState loadState() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (HotelState) ois.readObject();
        } catch (FileNotFoundException e) {
            return new HotelState(); // First time run
        } catch (Exception e) {
            System.err.println("Recovery Failed (Data Corrupt). Starting fresh.");
            return new HotelState();
        }
    }
}

/**
 * Wrapper for the entire system state (Must be Serializable)
 */
class HotelState implements Serializable {
    private static final long serialVersionUID = 1L;
    private final RoomInventory inventory;
    private final BookingHistory history;

    public HotelState() {
        this.inventory = new RoomInventory();
        this.history = new BookingHistory();
    }

    public HotelState(RoomInventory inv, BookingHistory hist) {
        this.inventory = inv;
        this.history = hist;
    }

    public RoomInventory getInventory() { return inventory; }
    public BookingHistory getHistory() { return history; }
}

/* --- Domain Objects updated to implement Serializable --- */

class RoomInventory implements Serializable {
    private final Map<String, Integer> inventoryMap = new HashMap<>();
    public void updateAvailability(String roomType, int count) { inventoryMap.put(roomType, count); }
    public int getAvailableCount(String type) { return inventoryMap.getOrDefault(type, 0); }
    public void reduceAvailability(String type) { inventoryMap.put(type, inventoryMap.get(type) - 1); }
}

class BookingHistory implements Serializable {
    private final List<String> records = new ArrayList<>();
    public void recordBooking(String guest, String id, String type) {
        records.add(guest + " booked " + type + " (ID: " + id + ")");
    }
    public List<String> getAllRecords() { return records; }
}

class BookingService {
    private final RoomInventory inventory;
    public BookingService(RoomInventory inventory) { this.inventory = inventory; }
    public String processBooking(Reservation res) {
        String type = res.getRequestedRoomType();
        if (inventory.getAvailableCount(type) > 0) {
            inventory.reduceAvailability(type);
            String id = "R" + (100 + (int)(Math.random() * 900));
            System.out.println("[SUCCESS] Allocated " + id);
            return id;
        }
        return null;
    }
}

class Reservation {
    private final String guestName;
    private final String requestedRoomType;
    public Reservation(String guestName, String type) { this.guestName = guestName; this.requestedRoomType = type; }
    public String getGuestName() { return guestName; }
    public String getRequestedRoomType() { return requestedRoomType; }
}
