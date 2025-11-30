package service;

import model.Customer;
import model.IRoom;
import model.Reservation;

import java.util.*;

public class ReservationService {

    private static ReservationService instance;

    private final Map<String, IRoom> rooms = new HashMap<>();
    private final List<Reservation> reservations = new ArrayList<>();

    private ReservationService() {}

    public static ReservationService getInstance() {
        if (instance == null) instance = new ReservationService();
        return instance;
    }

    // avoid duplicate room numbers
    public boolean registerRoom(IRoom room) {
        if (room == null) return false;
        if (rooms.containsKey(room.getRoomNumber())) return false;
        rooms.put(room.getRoomNumber(), room);
        return true;
    }

    public IRoom fetchRoomByNumber(String roomNumber) {
        return rooms.get(roomNumber);
    }

    public Collection<IRoom> listAllRooms() {
        return rooms.values();
    }

    private boolean matchesFreeFilter(IRoom room, Boolean onlyFree) {
        if (onlyFree == null) return true;
        boolean isFree = (room.getRoomPrice() != null && room.getRoomPrice() == 0.0) || room.isFree();
        return onlyFree.equals(isFree);
    }




//     true if two date ranges overlap
    private boolean overlaps(Date checkIn, Date checkOut, Reservation r) {
        return !(checkOut.compareTo(r.getCheckInDate()) <= 0 ||
                checkIn.compareTo(r.getCheckOutDate()) >= 0);
    }

    // booking with conflict detection
    public Reservation bookRoomForCustomer(Customer customer, IRoom room, Date checkIn, Date checkOut) {

        if (customer == null || room == null || checkIn == null || checkOut == null
                || !checkIn.before(checkOut)) {
            System.out.println("Error: Invalid input.");
            return null;
        }

        // checking real availability here
        for (Reservation r : reservations) {
            if (r.getRoom().getRoomNumber().equals(room.getRoomNumber())) {
                if (overlaps(checkIn, checkOut, r)) {
                    System.out.println("Error: Room " + room.getRoomNumber() + " is already booked for these dates.");
                    return null;
                }
            }
        }

        Reservation res = new Reservation(customer, room, checkIn, checkOut);
        reservations.add(res);
        return res;
    }


    // shows ALL rooms, booking conflict is handled later
    public Collection<IRoom> searchRoomsForDisplay(Date checkIn, Date checkOut, Boolean onlyFree) {
        List<IRoom> result = new ArrayList<>();
        for (IRoom room : rooms.values()) {
            if (matchesFreeFilter(room, onlyFree)) {
                result.add(room);
            }
        }
        return result;
    }


    public Map<IRoom, Date[]> findRecommendedRoomsWithDates(Date checkIn, Date checkOut,
                                                            int searchWindowDays, Boolean onlyFree) {

        Map<IRoom, Date[]> recommendations = new LinkedHashMap<>();
        long duration = checkOut.getTime() - checkIn.getTime();
        if (duration <= 0) return recommendations;

        //  Limit searchWindowDays (user input) to maximum 365
        if (searchWindowDays > 365) {
            System.out.println("Note: Maximum search range allowed is 365 days. Using 365 days.");
            searchWindowDays = 365;
        } else if (searchWindowDays < 1) {
            searchWindowDays = 1; // Minimum positive window
        }

        // Compute today and latest bookable date (+365 days)
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();

        cal.add(Calendar.DAY_OF_YEAR, 365);
        Date latestBookableDate = cal.getTime();

        //  Sliding window: user window + next 6 days (7-day window)
        int startOffset = searchWindowDays;
        int endOffset = searchWindowDays + 6;

        //  Iterate through all rooms
        for (IRoom room : rooms.values()) {

            if (!matchesFreeFilter(room, onlyFree)) continue;

            boolean immediateBooked = false;

            // Check if room is booked for the original dates
            for (Reservation r : reservations) {
                if (r.getRoom().getRoomNumber().equals(room.getRoomNumber())
                        && overlaps(checkIn, checkOut, r)) {
                    immediateBooked = true;
                    break;
                }
            }

            if (!immediateBooked) continue;

            // Try alternative dates within the sliding window
            for (int offset = startOffset; offset <= endOffset; offset++) {

                Date altIn = new Date(checkIn.getTime() + offset * 24L * 60 * 60 * 1000);
                Date altOut = new Date(altIn.getTime() + duration);

                // Stop if alternative exceeds +365-day limit
                if (altIn.after(latestBookableDate)) {
                    break;
                }

                boolean conflict = false;
                for (Reservation r : reservations) {
                    if (r.getRoom().getRoomNumber().equals(room.getRoomNumber())
                            && overlaps(altIn, altOut, r)) {
                        conflict = true;
                        break;
                    }
                }

                if (!conflict) {
                    recommendations.put(room, new Date[]{altIn, altOut});
                    break;
                }
            }
        }

        return recommendations;
    }

    public Collection<Reservation> fetchReservationsForCustomer(Customer customer) {
        List<Reservation> list = new ArrayList<>();
        if (customer == null) return list;

        for (Reservation r : reservations) {
            if (r.getCustomer().getEmail().equals(customer.getEmail()))
                list.add(r);
        }
        return list;
    }

    public void showAllReservations() {
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }
        reservations.forEach(System.out::println);
    }


}
