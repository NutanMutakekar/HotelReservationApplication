package ui;

import api.AdminResource;
import model.Customer;
import model.IRoom;
import model.Room;
import model.RoomType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AdminResource admin = AdminResource.getInstance();

    public static void display() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. See all Customers");
            System.out.println("2. See all Rooms");
            System.out.println("3. See all Reservations");
            System.out.println("4. Add a Room");
            System.out.println("5. Populate Test Data");
            System.out.println("6. Back to Main Menu");
            System.out.print("Please select a number: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    Collection<?> customers = admin.listCustomers();
                    if (customers.isEmpty()) System.out.println("No customers found.");
                    else customers.forEach(System.out::println);
                    break;
                case "2":
                    Collection<IRoom> rooms = admin.listRooms();
                    if (rooms.isEmpty()) System.out.println("No rooms found.");
                    else rooms.forEach(System.out::println);
                    break;
                case "3":
                    admin.displayReservations();
                    break;
                case "4":
                    addRoomMenu();
                    break;
                case "5":
                    admin.populateTestData();
                    System.out.println("Test data populated.");
                    System.out.println("Customer ,Rooms ,Reservations  after populating test data:");

                    System.out.println("\n--- Customers ---");
                    for (Customer c : admin.listCustomers()) {
                        System.out.println(c);
                    }

                    System.out.println("\n--- Rooms ---");
                    for (IRoom r : admin.listRooms()) {
                        System.out.println(r);
                    }

                    System.out.println("\n--- Reservations ---");
                    admin.displayReservations();


                    break;
                case "6":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }



    private static void addRoomMenu() {
        List<IRoom> roomsToAdd = new ArrayList<>();
        boolean adding = true;

        while (adding) {

            String roomNumber;

            // Room number validation loop

            while (true) {
                System.out.print("Enter room number: ");
                roomNumber = scanner.nextLine().trim();

                // Check empty
                if (roomNumber.isEmpty()) {
                    System.out.println("Error: Room number cannot be empty.");
                    continue;
                }

                //  must be numeric only
                if (!roomNumber.matches("\\d+")) {
                    System.out.println("Error: Room number must contain digits only.");
                    continue;
                }

                // Check if room exists in the system already
                if (admin.getRoomNumber(roomNumber) != null) {
                    System.out.println("Error: Room number " + roomNumber + " already exists. Try another.");
                    continue;
                }

                // Check if room exists in current batch
                boolean duplicateInBatch = false;
                for (IRoom r : roomsToAdd) {
                    if (r.getRoomNumber().equals(roomNumber)) {
                        duplicateInBatch = true;
                        break;
                    }
                }

                if (duplicateInBatch) {
                    System.out.println("Error: Room number " + roomNumber + " already entered in this session.");
                    continue;
                }

                break;
            }

            double price;

            // PRICE VALIDATION LOOP

            while (true) {
                System.out.print("Enter price per night: ");
                try {
                    price = Double.parseDouble(scanner.nextLine().trim());
                    if (price < 0) {
                        System.out.println("Error: Price cannot be negative.");
                        continue;
                    }
                    break; // Valid price
                } catch (Exception e) {
                    System.out.println("Invalid price. Please enter a valid numeric value.");
                }
            }

            RoomType type;

            // Room type validation loop
            while (true) {
                System.out.print("Enter room type (SINGLE/DOUBLE): ");
                try {
                    type = RoomType.valueOf(scanner.nextLine().trim().toUpperCase());
                    break; // Valid type
                } catch (Exception e) {
                    System.out.println("Invalid room type. Only SINGLE or DOUBLE allowed.");
                }
            }

            // Add validated room to list
            roomsToAdd.add(new Room(roomNumber, price, type));

            System.out.print("Add another? (Y/N): ");
            String another = scanner.nextLine();
            if (!another.equalsIgnoreCase("Y")) {
                adding = false;
            }
        }

        admin.addRooms(roomsToAdd);
        System.out.println("Rooms added.");
    }

}
