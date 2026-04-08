import java.util.*;

/**
 * The entry point for the Hotel Booking Management System.
 * Use Case 7: Add-On Service Selection (Business Extensibility).
 *
 * @author Karthik
 * @version 5.0
 */
public class Book_My_Stay {

    public static void main(String[] args) {
        System.out.println("****************************************");
        System.out.println("Welcome to Book My Stay!");
        System.out.println("System: Hotel Booking Management (v5.0)");
        System.out.println("****************************************\n");

        // 1. Setup Core Services
        RoomInventory inventory = new RoomInventory();
        inventory.updateAvailability("Single Room", 2);

        BookingService bookingService = new BookingService(inventory);
        AddOnServiceManager addOnManager = new AddOnServiceManager();

        // 2. Process a Booking (Use Case 6)
        Reservation guestA = new Reservation("Guest A", "Single Room");
        String roomID = bookingService.processBooking(guestA);

        // 3. Add-On Selection (Use Case 7)
        if (roomID != null) {
            System.out.println("\n--- Adding Optional Services ---");
            addOnManager.addServiceToBooking(roomID, new AddOn("Breakfast Buffet", 500.0));
            addOnManager.addServiceToBooking(roomID, new AddOn("Airport Pickup", 1200.0));

            // 4. Calculate and Display Final Bill
            double basePrice = 1500.0; // Example base price
            double addOnTotal = addOnManager.getTotalAddOnCost(roomID);

            System.out.println("\n--- Final Invoice for " + roomID + " ---");
            System.out.println("Base Room Price: ₹" + basePrice);
            System.out.println("Add-On Services Total: ₹" + addOnTotal);
            System.out.println("Total Amount Payable: ₹" + (basePrice + addOnTotal));
        }
    }
}

/**
 * Use Case 7: Add-On Service Model
 */
class AddOn {
    private final String name;
    private final double price;

    public AddOn(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
}

/**
 * Use Case 7: Add-On Service Manager
 * Handles the mapping between Reservation IDs and extra services.
 */
class AddOnServiceManager {
    // Map and List Combination: One-to-Many relationship
    private final Map<String, List<AddOn>> bookingAddOns = new HashMap<>();

    public void addServiceToBooking(String roomID, AddOn service) {
        bookingAddOns.putIfAbsent(roomID, new ArrayList<>());
        bookingAddOns.get(roomID).add(service);
        System.out.println("[ADD-ON] Added " + service.getName() + " to " + roomID);
    }

    public double getTotalAddOnCost(String roomID) {
        List<AddOn> services = bookingAddOns.getOrDefault(roomID, Collections.emptyList());
        // Cost Aggregation Logic
        return services.stream().mapToDouble(AddOn::getPrice).sum();
    }
}

/**
 * Updated Booking Service to return Room ID for Use Case 7 linkage.
 */
class BookingService {
    private final RoomInventory inventory;
    private int idCounter = 100;

    public BookingService(RoomInventory inventory) { this.inventory = inventory; }

    public String processBooking(Reservation reservation) {
        String type = reservation.getRequestedRoomType();
        if (inventory.getAvailableCount(type) > 0) {
            String roomId = type.substring(0, 1) + (++idCounter);
            inventory.reduceAvailability(type);
            System.out.println("[CONFIRMED] " + reservation.getGuestName() + " assigned " + roomId);
            return roomId;
        }
        System.out.println("[FAILED] No availability for " + type);
        return null;
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

class RoomInventory {
    private final Map<String, Integer> inventoryMap = new HashMap<>();
    public void updateAvailability(String roomType, int count) { inventoryMap.put(roomType, count); }
    public int getAvailableCount(String roomType) { return inventoryMap.getOrDefault(roomType, 0); }
    public void reduceAvailability(String roomType) { inventoryMap.put(roomType, getAvailableCount(roomType) - 1); }
}
