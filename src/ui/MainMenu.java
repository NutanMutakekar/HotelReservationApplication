package ui;

import api.HotelResource;
import model.Customer;
import model.IRoom;
import model.Reservation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainMenu {

    private static final Scanner scanner = new Scanner(System.in);
    private static final HotelResource hotel = HotelResource.getInstance();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Initialize dateFormat with strict parsing
    static {
        dateFormat.setLenient(false); // Reject invalid dates
    }

    public static void display() {
        boolean running = true;

        while (running) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1. Find and reserve a room");
            System.out.println("2. View my reservations");
            System.out.println("3. Create a new account");
            System.out.println("4. Admin options");
            System.out.println("5. Exit");
            System.out.print("Please Select an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    findAndReserveRoom();
                    break;
                case "2":
                    viewReservations();
                    break;
                case "3":
                    createAccount();
                    break;
                case "4":
                    AdminMenu.display();
                    break;
                case "5":
                    running = false;
                    System.out.println("Thank you for using the reservation system.");
                    break;
                default:
                    System.out.println("Invalid selection. Please try again.");
            }
        }
    }


    private static Date parseAndValidateDate(String dateStr, String fieldName) {
        try {
            // First try raw parsing
            Date parsedDate = dateFormat.parse(dateStr);

            // Extract year, month, day manually
            String[] parts = dateStr.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            // FEBRUARY SPECIAL VALIDATION
            if (month == 2) {

                boolean isLeap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
                int maxFebDays = isLeap ? 29 : 28;

                if (day > maxFebDays) {
                    System.out.println("Error: February in " + year +
                            " cannot have more than " + maxFebDays + " days.");
                    return null;
                }
            }

            // Reject past dates
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            Date currentDate = today.getTime();

            if (parsedDate.before(currentDate)) {
                System.out.println("Error: " + fieldName +
                        " cannot be in the past. Please enter a current or future date.");
                return null;
            }

            return parsedDate;

        } catch (Exception e) {
            System.out.println("Error: Invalid date format for " + fieldName + ".");
            System.out.println("Please use yyyy-MM-dd format (e.g., 2025-12-25)");
            System.out.println("Note: Month must be 01–12 and date must be valid for the given month.");
            return null;
        }
    }



    private static void findAndReserveRoom() {
        Date checkIn = null;
        Date checkOut = null;

        // Get and validate check-in date
        while (checkIn == null) {
            System.out.print("Enter check-in date (yyyy-MM-dd): ");
            String checkInStr = scanner.nextLine().trim();
            checkIn = parseAndValidateDate(checkInStr, "Check-in date");
        }

        // Get and validate check-out date
        while (checkOut == null) {
            System.out.print("Enter check-out date (yyyy-MM-dd): ");
            String checkOutStr = scanner.nextLine().trim();
            checkOut = parseAndValidateDate(checkOutStr, "Check-out date");

            if (checkOut != null && !checkIn.before(checkOut)) {
                System.out.println("Error: Check-out date must be after check-in date.");
                checkOut = null;
            }
        }

        // Validate search type
        int searchType;
        while (true) {
            System.out.print("Search type → (1) All rooms  (2) Free rooms only  (3) Paid rooms only: ");
            String input = scanner.nextLine().trim();

            try {
                searchType = Integer.parseInt(input);

                if (searchType == 1 || searchType == 2 || searchType == 3) break;

                System.out.println("Error: Please enter only 1, 2 or 3.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Only numbers 1, 2, or 3 are allowed.");
            }
        }

        Boolean onlyFree = null;
        if (searchType == 2) onlyFree = true;
        if (searchType == 3) onlyFree = false;

        //VALIDATE DAYS AHEAD
        int dayWindow = 7;
        while (true) {
            System.out.print("If rooms are unavailable, how many days ahead should we search? (default: 7): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) break; // keep default 7

            try {
                int parsed = Integer.parseInt(input);
                if (parsed > 0) {
                    dayWindow = parsed;
                    break;
                } else {
                    System.out.println("Please enter a positive number.");
                }
            } catch (Exception e) {
                System.out.println("Invalid number. Try again.");
            }
        }

        //  SEARCH ROOMS
        Collection<IRoom> availableRooms =
                hotel.searchRooms(checkIn, checkOut, dayWindow, onlyFree);

        if (!availableRooms.isEmpty()) {
            System.out.println("\nAvailable rooms for your dates:");
            availableRooms.forEach(System.out::println);

            System.out.print("Enter a room number to reserve (or 'N' to cancel): ");
            String selection = scanner.nextLine().trim();

            if (selection.equalsIgnoreCase("N")) return;

            IRoom roomSelected = hotel.getRoomByNumber(selection);

            if (roomSelected == null) {
                System.out.println("Invalid room number.");
                return;
            }

            System.out.print("Enter your account email: ");
            String email = scanner.nextLine().trim().toLowerCase();

            Customer customer = hotel.getCustomerByEmail(email);
            if (customer == null) {
                System.out.println("No customer found. Please create an account first.");
                return;
            }

            Reservation reservation =
                    hotel.reserveRoom(email, roomSelected, checkIn, checkOut);

            if (reservation == null) {
                System.out.println("The room is already booked for these dates.");

                System.out.println("\nChecking for recommended dates...");
                Map<IRoom, Date[]> rec =
                        hotel.searchRoomsWithRecommendations(checkIn, checkOut, dayWindow, onlyFree);

                Date[] alt = rec.get(roomSelected);

                if (alt == null) {
                    System.out.println("No recommended dates available for this room within "
                            + dayWindow + " days.");
                    return;
                }

                System.out.println("\nSuggested dates for Room " + roomSelected.getRoomNumber() + ":");
                System.out.println("  Available from: " + dateFormat.format(alt[0]) +
                        " to " + dateFormat.format(alt[1]));

                System.out.print("Would you like to book these suggested dates? (y/n): ");
                String ans = scanner.nextLine().trim();

                if (!ans.equalsIgnoreCase("y")) return;

                Reservation altReservation =
                        hotel.reserveRoom(email, roomSelected, alt[0], alt[1]);

                if (altReservation != null) {
                    System.out.println("Reservation confirmed with suggested dates:");
                    System.out.println(altReservation);
                } else {
                    System.out.println("Unable to reserve the suggested dates.");
                }

                return;
            }

            System.out.println("Reservation confirmed!");
            System.out.println(reservation);
            return;
        }

        //  NO ROOMS FOUND → SHOW RECOMMENDATIONS

        System.out.println("\nNo rooms available for your chosen dates.");

        Map<IRoom, Date[]> recommendations =
                hotel.searchRoomsWithRecommendations(checkIn, checkOut, dayWindow, onlyFree);

        if (recommendations.isEmpty()) {
            System.out.println("No alternative dates available within your search window.");
            return;
        }

        System.out.println("\nSuggested rooms with nearby available dates:");
        List<Map.Entry<IRoom, Date[]>> list = new ArrayList<>(recommendations.entrySet());

        int idx = 1;
        for (Map.Entry<IRoom, Date[]> entry : list) {
            System.out.println(idx + ". " + entry.getKey());
            System.out.println("   Available from: " +
                    dateFormat.format(entry.getValue()[0]) + " to " +
                    dateFormat.format(entry.getValue()[1]));
            idx++;
        }

        System.out.print("Would you like to book one of these suggested options? (y/n): ");
        String answer = scanner.nextLine().trim();

        if (!answer.equalsIgnoreCase("y")) return;

        System.out.print("Enter the number of the room you want to book: ");
        String pickInput = scanner.nextLine().trim();

        int pick;
        try {
            pick = Integer.parseInt(pickInput);
            if (pick < 1 || pick > list.size()) {
                System.out.println("Invalid selection.");
                return;
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
            return;
        }

        Map.Entry<IRoom, Date[]> chosen = list.get(pick - 1);
        IRoom selectedRoom = chosen.getKey();
        Date altCheckIn = chosen.getValue()[0];
        Date altCheckOut = chosen.getValue()[1];

        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim().toLowerCase();

        Customer customer = hotel.getCustomerByEmail(email);
        if (customer == null) {
            System.out.println("Customer not found. Please create an account before booking.");
            return;
        }

        Reservation altRes =
                hotel.reserveRoom(email, selectedRoom, altCheckIn, altCheckOut);

        if (altRes == null) {
            System.out.println("Could not complete the reservation.");
        } else {
            System.out.println("Reservation confirmed!");
            System.out.println(altRes);
        }
    }


    private static void viewReservations() {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim().toLowerCase();

        Collection<Reservation> reservations = hotel.getCustomerReservations(email);

        if (reservations == null || reservations.isEmpty()) {
            System.out.println("You have no reservations.");
        } else {
            reservations.forEach(System.out::println);
        }
    }

    private static void createAccount() {
        String email;
        boolean valid = false;

        while (!valid) {
            System.out.print("Enter email (example: user@example.com): ");
            email = scanner.nextLine().trim().toLowerCase();

            try {
                new Customer("test", "user", email); // email validation
                valid = true;

                System.out.print("Enter first name: ");
                String firstName = scanner.nextLine().trim();

                System.out.print("Enter last name: ");
                String lastName = scanner.nextLine().trim();

                boolean created = hotel.createCustomer(email, firstName, lastName);

                if (created) {
                    System.out.println("✔ Account created successfully.");
                } else {
                    System.out.println("An account with this email already exists.");
                }

            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid email format. Try again.");
            }
        }
    }
}






