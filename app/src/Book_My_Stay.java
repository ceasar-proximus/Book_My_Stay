import java.util.*;

/**
 * Use Case 8: Booking History & Reporting (Operational Visibility).
 * Integrated into the Hotel Booking Management System.
 *
 * @author Karthik
 * @version 6.0
 */
public class Book_My_Stay {

    public static void main(String[] args) {
        System.out.println("****************************************");
        System.out.println("Welcome to Book My Stay!");
        System.out.println("System: Hotel Booking Management (v6.0)");
        System.out.println("****************************************\n");

        // 1. Setup Services
        RoomInventory inventory = new RoomInventory();
        inventory.updateAvailability("Single Room", 2);

        BookingService bookingService = new BookingService(inventory);
        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService(history);

        // 2. Simulate Bookings
        processAndArchive(new Reservation("Guest A", "Single Room"), bookingService, history);
        processAndArchive(new Reservation("Guest B", "Single Room"), bookingService, history);
        processAndArchive(new Reservation("Guest C", "Single Room"), bookingService, history); // Should fail

        // 3. Admin Reporting (Use Case 8)
        System.out.println("\n--- Admin: Generating Operational Reports ---");
        reportService.printSummaryReport();
        reportService.printDetailedHistory();
    }

    /**
     * Helper to link confirmation flow with historical tracking.
     */
    private static void processAndArchive(Reservation res, BookingService service, BookingHistory history) {
        String roomID = service.processBooking(res);
        if (roomID != null) {
            history.recordBooking(res, roomID);
        }
    }
}

/**
 * Use Case 8: Booking History
 * Maintains a persistent-style record of confirmed transactions in insertion order.
 */
class BookingHistory {
    // List preserves the chronological order of confirmations
    private final List<HistoricalRecord> records = new ArrayList<>();

    public void recordBooking(Reservation res, String roomID) {
        records.add(new HistoricalRecord(res.getGuestName(), roomID, res.getRequestedRoomType(), new Date()));
    }

    public List<HistoricalRecord> getAllRecords() {
        // Return unmodifiable list to ensure reporting does not modify stored data
        return Collections.unmodifiableList(records);
    }

    // Inner class to represent a completed transaction
    public static class HistoricalRecord {
        private final String guest;
        private final String roomID;
        private final String type;
        private final Date timestamp;

        public HistoricalRecord(String guest, String roomID, String type, Date timestamp) {
            this.guest = guest;
            this.roomID = roomID;
            this.type = type;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s booked %s (Room: %s)", timestamp, guest, type, roomID);
        }
    }
}

/**
 * Use Case 8: Booking Report Service
 * Decouples reporting logic from the data storage.
 */
class BookingReportService {
    private final BookingHistory history;

    public BookingReportService(BookingHistory history) {
        this.history = history;
    }

    public void printSummaryReport() {
        long total = history.getAllRecords().size();
        System.out.println(">> Total Successful Bookings: " + total);
    }

    public void printDetailedHistory() {
        System.out.println(">> Audit Trail (Chronological):");
        history.getAllRecords().forEach(System.out::println);
    }
}

/* --- Core Logic from Previous Use Cases (Maintained for context) --- */

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
        System.out.println("[FAILED] No availability for " + reservation.getGuestName());
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
    public int getAvailableCount(String type) { return inventoryMap.getOrDefault(type, 0); }
    public void reduceAvailability(String type) {
        inventoryMap.put(type, inventoryMap.get(type) - 1);
    }
}
