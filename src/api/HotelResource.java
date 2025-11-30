package api;

import model.Customer;
import model.IRoom;
import model.Reservation;
import service.CustomerService;
import service.ReservationService;

import java.util.*;

public class HotelResource {

    private static final HotelResource instance = new HotelResource();

    private final CustomerService customerService = CustomerService.getInstance();
    private final ReservationService reservationService = ReservationService.getInstance();

    private HotelResource() {}

    public static HotelResource getInstance() {
        return instance;
    }

    //    Fetch a customer using their email
    public Customer getCustomerByEmail(String email) {
        return customerService.fetchCustomer(email);
    }

    // Create a new customer account
    public boolean createCustomer(String email, String firstName, String lastName) {
        return customerService.registerCustomer(email, firstName, lastName);
    }

    //     Get a room using room number
    public IRoom getRoomByNumber(String roomNumber) {
        return reservationService.fetchRoomByNumber(roomNumber);
    }



    public Collection<IRoom> searchRooms(Date checkIn, Date checkOut, int daysWindow, Boolean onlyFree) {
        return reservationService.searchRoomsForDisplay(checkIn, checkOut, onlyFree);
    }


//      Recommended future rooms

    public Map<IRoom, Date[]> searchRoomsWithRecommendations(
            Date checkIn,
            Date checkOut,
            int daysWindow,
            Boolean onlyFree
    ) {
        return reservationService.findRecommendedRoomsWithDates(checkIn, checkOut, daysWindow, onlyFree);
    }

    //    Reserve a room for a customer
    public Reservation reserveRoom(String customerEmail, IRoom room, Date checkIn, Date checkOut) {
        Customer customer = customerService.fetchCustomer(customerEmail);
        if (customer == null) return null;

        return reservationService.bookRoomForCustomer(customer, room, checkIn, checkOut);
    }

    //   Fetch all reservations of a customer
    public Collection<Reservation> getCustomerReservations(String email) {
        Customer customer = customerService.fetchCustomer(email);
        if (customer == null) return Collections.emptyList();

        return reservationService.fetchReservationsForCustomer(customer);
    }
}





