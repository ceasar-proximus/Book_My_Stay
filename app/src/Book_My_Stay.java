import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The entry point for the Hotel Booking Management System.
 * Use Case 4: Room Search & Availability Check (Read-Only access).
 *
 * @author Karthik
 * @version 2.0
 */
public class Book_My_Stay {

    public static void main(String[] args) {
        System.out.println("****************************************");
        System.out.println("Welcome to Book My Stay!");
        System.out.println("System: Hotel Booking Management (v2.0)");
        System.out.println("****************************************\n");

        // 1. Initialize Centralized Inventory (State Holder)
        RoomInventory inventory = new RoomInventory();
        inventory.updateAvailability("Single Room", 5);
        inventory.updateAvailability("Double Room", 3);
        inventory.updateAvailability("Luxury Suite", 0); // Out of stock

        // 2. Define Room Domain Objects (Data Source for Search)
        Map<String, Room> roomCatalog = new HashMap<>();
        roomCatalog.put("Single Room", new SingleRoom(101, 1500.0));
        roomCatalog.put("Double Room", new DoubleRoom(201, 2500.0));
        roomCatalog.put("Luxury Suite", new SuiteRoom(301, 5000.0));

        // 3. Initialize Search Service (Read-Only Logic)
        SearchService searchService = new SearchService(inventory, roomCatalog);

        // 4. Guest Action: Initiate Search
        System.out.println("--- Guest Search: Finding Available Rooms ---");
        List<Room> availableOptions = searchService.findAvailableRooms();

        if (availableOptions.isEmpty()) {
            System.out.println("No rooms available at the moment.");
        } else {
            for (Room room : availableOptions) {
                int count = inventory.getAvailableCount(room.getType());
                System.out.println("[AVAILABLE] " + room.getType() +
                        " | Price: ₹" + room.getPrice() +
                        " | Stock: " + count +
                        " | Features: " + room.getFeatures());
            }
        }

        // 5. System State Verification
        System.out.println("\n[System Check] Search complete. System state remains unchanged.");
    }
}

/**
 * Use Case 4: Search Service
 * Separates Read-Only search logic from state-mutating booking logic.
 */
class SearchService {
    private final RoomInventory inventory;
    private final Map<String, Room> roomCatalog;

    public SearchService(RoomInventory inventory, Map<String, Room> roomCatalog) {
        this.inventory = inventory;
        this.roomCatalog = roomCatalog;
    }

    /**
     * Filters and retrieves only room types with availability > 0.
     * Implements Validation Logic and Defensive Programming.
     */
    public List<Room> findAvailableRooms() {
        List<Room> results = new ArrayList<>();

        for (Room room : roomCatalog.values()) {
            // Read-only check from inventory
            if (inventory.getAvailableCount(room.getType()) > 0) {
                results.add(room);
            }
        }
        return results;
    }
}

/**
 * Use Case 3 & 4: Centralized Inventory
 * Manages the current state of room counts.
 */
class RoomInventory {
    private Map<String, Integer> inventoryMap = new HashMap<>();

    public void updateAvailability(String roomType, int count) {
        inventoryMap.put(roomType, count);
    }

    public int getAvailableCount(String roomType) {
        return inventoryMap.getOrDefault(roomType, 0);
    }

    public void reduceAvailability(String roomType) {
        int currentCount = getAvailableCount(roomType);
        if (currentCount > 0) {
            inventoryMap.put(roomType, currentCount - 1);
        }
    }
}

/**
 * Domain Model for Room types.
 */
abstract class Room {
    private int roomNumber;
    private double price;
    private String type;

    public Room(int roomNumber, double price, String type) {
        this.roomNumber = roomNumber;
        this.price = price;
        this.type = type;
    }

    public String getType() { return type; }
    public double getPrice() { return price; }
    public abstract String getFeatures();
}

class SingleRoom extends Room {
    public SingleRoom(int roomNumber, double price) { super(roomNumber, price, "Single Room"); }
    @Override public String getFeatures() { return "1 Single Bed, WiFi"; }
}

class DoubleRoom extends Room {
    public DoubleRoom(int roomNumber, double price) { super(roomNumber, price, "Double Room"); }
    @Override public String getFeatures() { return "2 Queen Beds, Mini Fridge"; }
}

class SuiteRoom extends Room {
    public SuiteRoom(int roomNumber, double price) { super(roomNumber, price, "Luxury Suite"); }
    @Override public String getFeatures() { return "King Bed, Personal Bar"; }
}
