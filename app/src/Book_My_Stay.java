import java.util.HashMap;
import java.util.Map;

/**
 * The entry point for the Hotel Booking Management System.
 * Use Case 3: Centralized Room Inventory Management using HashMap.
 *
 * @author Karthik
 * @version 1.0
 */
public class Book_My_Stay {

    public static void main(String[] args) {
        System.out.println("****************************************");
        System.out.println("Welcome to Book My Stay!");
        System.out.println("System: Hotel Booking Management");
        System.out.println("Version: 1.0");
        System.out.println("****************************************\n");

        // Initialize Centralized Inventory
        RoomInventory inventory = new RoomInventory();

        // Register Room Types and their initial counts (Key: Type, Value: Count)
        inventory.updateAvailability("Single Room", 5);
        inventory.updateAvailability("Double Room", 3);
        inventory.updateAvailability("Luxury Suite", 2);

        // Create Room Objects
        Room single = new SingleRoom(101, 1500.0);
        Room doubleRm = new DoubleRoom(201, 2500.0);
        Room suite = new SuiteRoom(301, 5000.0);

        // Display current inventory state
        System.out.println("--- Current Room Inventory ---");
        displayStatus(single, inventory);
        displayStatus(doubleRm, inventory);
        displayStatus(suite, inventory);

        // Simulating a booking: Reduce availability for a Single Room
        System.out.println("\n[Action] Booking 1 Single Room...");
        inventory.reduceAvailability("Single Room");

        // Show updated state
        System.out.println("\n--- Updated Inventory ---");
        displayStatus(single, inventory);
    }

    public static void displayStatus(Room room, RoomInventory inventory) {
        int available = inventory.getAvailableCount(room.getType());
        System.out.println("Type: " + room.getType() + " | Available: " + available + " | Price: ₹" + room.getPrice());
    }
}

/**
 * Use Case 3: Centralized Inventory Logic
 * Encapsulates a HashMap to manage room counts efficiently.
 */
class RoomInventory {
    // HashMap provides O(1) lookup and avoids scattered variables
    private Map<String, Integer> inventoryMap;

    public RoomInventory() {
        this.inventoryMap = new HashMap<>();
    }

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
        } else {
            System.out.println("Error: No " + roomType + "s left!");
        }
    }
}

/**
 * Abstract Class: Domain Model for a Room
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
