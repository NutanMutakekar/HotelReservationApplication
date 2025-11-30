package api;

import model.Customer;
import model.IRoom;
import service.CustomerService;
import service.ReservationService;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class AdminResource {

    private static AdminResource adminResourceInstance = null;
    private static final CustomerService customerService = CustomerService.getInstance();
    private static final ReservationService reservationService = ReservationService.getInstance();

    private AdminResource() {}

    public static AdminResource getInstance() {
        if (adminResourceInstance == null) adminResourceInstance = new AdminResource();
        return adminResourceInstance;
    }

    public Customer retrieveCustomer(String email) {
        return customerService.fetchCustomer(email);
    }

    public void addRooms(List<IRoom> rooms) {
        for (IRoom r : rooms) reservationService.registerRoom(r);
    }

    public Collection<IRoom> listRooms() {
        return reservationService.listAllRooms();
    }

    public Collection<Customer> listCustomers() {
        return customerService.listAllCustomers();
    }

    public void displayReservations() {
        reservationService.showAllReservations();
    }
    public IRoom getRoomNumber(String roomNumber) {
        return reservationService.fetchRoomByNumber(roomNumber);
    }

    public void populateTestData() {
        customerService.registerCustomer("liam@gmail.com", "Liam", "Carter");
        customerService.registerCustomer("ethan@gmail.com", "Ethan", "Harris");

        reservationService.registerRoom(new model.Room("101", 100.0, model.RoomType.SINGLE));
        reservationService.registerRoom(new model.Room("102", 200.0, model.RoomType.DOUBLE));
        reservationService.registerRoom(new model.Room("103", 150.0, model.RoomType.SINGLE));
        reservationService.registerRoom(new model.Room("105", 0.0, model.RoomType.SINGLE));
        reservationService.registerRoom(new model.Room("108", 0.0, model.RoomType.SINGLE));
        reservationService.registerRoom(new model.Room("201", 100.01, model.RoomType.SINGLE));
        reservationService.registerRoom(new model.Room("202", 200.0, model.RoomType.DOUBLE));
        reservationService.registerRoom(new model.FreeRoom("203", model.RoomType.SINGLE));

        // one demo reservation
        Customer c = customerService.fetchCustomer("liam@gmail.com");
        IRoom r = reservationService.fetchRoomByNumber("201");
        if (c != null && r != null) {
            Calendar cal = Calendar.getInstance();
            cal.set(2025, Calendar.NOVEMBER, 20, 0, 0, 0);
            Date in = cal.getTime();
            cal.set(2025, Calendar.NOVEMBER, 22, 0, 0, 0);
            Date out = cal.getTime();
            reservationService.bookRoomForCustomer(c, r, in, out);
        }
    }
}
