import java.util.*;

/**
 * The entry point for the Hotel Booking Management System.
 * Combined Use Case 6 (Allocation) and Use Case 7 (Add-On Services).
 *
 * @author Karthik
 * @version 5.0 (Integrated)
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
        inventory.updateAvailability("Double Room", 1);

        BookingService bookingService = new BookingService(inventory);
        AddOnServiceManager addOnManager = new AddOnServiceManager();
        BookingQueue bookingQueue = new BookingQueue();

        // 2. Intake: Guests submit requests
        bookingQueue.addRequest(new Reservation("Guest A", "Single Room"));
        bookingQueue.addRequest(new Reservation("Guest B", "Single Room"));
        bookingQueue.addRequest(new Reservation("Guest C", "Double Room"));

        // 3. Process Allocation and Add-Ons
        System.out.println("--- Processing Bookings & Services ---");
        while (bookingQueue.hasRequests()) {
            Reservation request = bookingQueue.nextRequest();
            String roomID = bookingService.processBooking(request);

            // If booking was successful, add a default service (Example: Breakfast)
            if (roomID != null) {
                addOnManager.addServiceToBooking(roomID, new AddOn("Breakfast Buffet", 500.0));
                
                // Calculate Final Bill
                double basePrice = 1500.0; 
                double addOnTotal = addOnManager.getTotalAddOnCost(roomID);
                
                System.out.println("Final Invoice for " + roomID + ": ₹" + (basePrice + addOnTotal));
                System.out.println("--------------------------------------");
            }
        }
        
        bookingService.displayAllocations();
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
 */
class AddOnServiceManager {
    private final Map<String, List<AddOn>> bookingAddOns = new HashMap<>();

    public void addServiceToBooking(String roomID, AddOn service) {
        bookingAddOns.putIfAbsent(roomID, new ArrayList<>());
        bookingAddOns.get(roomID).add(service);
        System.out.println("[ADD-ON] Added " + service.getName() + " to " + roomID);
    }

    public double getTotalAddOnCost(String roomID) {
        List<AddOn> services = bookingAddOns.getOrDefault(roomID, Collections.emptyList());
        return services.stream().mapToDouble(AddOn::getPrice).sum();
    }
}

/**
 * Use Case 6: Booking Service
 */
class BookingService {
    private final RoomInventory inventory;
    private final Map<String, Set<String>> allocatedRooms = new HashMap<>();
    private int idCounter = 100;

    public BookingService(RoomInventory inventory) { this.inventory = inventory; }

    public String processBooking(Reservation reservation) {
        String type = reservation.getRequestedRoomType();
        if (inventory.getAvailableCount(type) > 0) {
            String roomId = type.substring(0, 1) + (++idCounter);
            
            allocatedRooms.putIfAbsent(type, new HashSet<>());
            allocatedRooms.get(type).add(roomId);
            inventory.reduceAvailability(type);
            
            System.out.println("[CONFIRMED] " + reservation.getGuestName() + " assigned " + roomId);
            return roomId;
        }
        System.out.println("[FAILED] No availability for " + reservation.getGuestName());
        return null;
    }

    public void displayAllocations() {
        System.out.println("\n--- Final Allocation Report ---");
        allocatedRooms.forEach((type, ids) -> System.out.println(type + " Assignments: " + ids));
    }
}

class BookingQueue {
    private final Queue<Reservation> requestQueue = new LinkedList<>();
    public void addRequest(Reservation request) { requestQueue.add(request); }
    public Reservation nextRequest() { return requestQueue.poll(); }
    public boolean hasRequests() { return !requestQueue.isEmpty(); }
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
    public int getAvailableCount(String type) { return inventoryMap.getOrDefault(type, 0); }
    public void reduceAvailability(String type) {
        inventoryMap.put(type, inventoryMap.get(type) - 1);
    }
}
