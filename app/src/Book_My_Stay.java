/**
 * The entry point for the Hotel Booking Management System.
 * Demonstrates Inheritance, Abstraction, and Polymorphism.
 *
 * @author Karthik
 * @version 1.0
 */
public class Book_My_Stay {

    // Static availability (demonstrating simple state management before Collections)
    private static int singleRoomAvailability = 5;
    private static int doubleRoomAvailability = 3;
    private static int suiteRoomAvailability = 2;

    public static void main(String[] args) {
        System.out.println("****************************************");
        System.out.println("Welcome to Book My Stay!");
        System.out.println("System: Hotel Booking Management");
        System.out.println("Version: 1.0");
        System.out.println("****************************************\n");

        // Polymorphism: Referencing concrete objects via the Abstract Type
        Room single = new SingleRoom(101, 1500.0);
        Room doubleRm = new DoubleRoom(201, 2500.0);
        Room suite = new SuiteRoom(301, 5000.0);

        // Displaying Room Details and Availability
        displayRoomInfo(single, singleRoomAvailability);
        displayRoomInfo(doubleRm, doubleRoomAvailability);
        displayRoomInfo(suite, suiteRoomAvailability);
    }

    /**
     * Helper method to display room details uniformly.
     */
    public static void displayRoomInfo(Room room, int availableCount) {
        System.out.println("Room Type: " + room.getType());
        System.out.println("Base Price: ₹" + room.getPrice());
        System.out.println("Features: " + room.getFeatures());
        System.out.println("Current Availability: " + availableCount);
        System.out.println("----------------------------------------");
    }
}

/**
 * Abstract Class: Defines the template for all rooms.
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

    // Abstract method to be implemented by sub-classes
    public abstract String getFeatures();
}

/**
 * Concrete Implementation: Single Room
 */
class SingleRoom extends Room {
    public SingleRoom(int roomNumber, double price) {
        super(roomNumber, price, "Single Room");
    }

    @Override
    public String getFeatures() {
        return "1 Single Bed, High-speed WiFi, Work Desk";
    }
}

/**
 * Concrete Implementation: Double Room
 */
class DoubleRoom extends Room {
    public DoubleRoom(int roomNumber, double price) {
        super(roomNumber, price, "Double Room");
    }

    @Override
    public String getFeatures() {
        return "2 Queen Beds, Mini Fridge, City View";
    }
}

/**
 * Concrete Implementation: Suite Room
 */
class SuiteRoom extends Room {
    public SuiteRoom(int roomNumber, double price) {
        super(roomNumber, price, "Luxury Suite");
    }

    @Override
    public String getFeatures() {
        return "King Bed, Living Area, Personal Bar, Bathtub";
    }
}
