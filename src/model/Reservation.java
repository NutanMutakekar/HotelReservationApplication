package model;

import java.util.Date;
import java.util.Objects;

public class Reservation {
    private final Customer customer;
    private final IRoom room;
    private final Date checkInDate;
    private final Date checkOutDate;

    public Reservation(Customer customer, IRoom room, Date checkInDate, Date checkOutDate) {
        this.customer = customer;
        this.room = room;
        this.checkInDate = new Date(checkInDate.getTime());
        this.checkOutDate = new Date(checkOutDate.getTime());
    }

    public Customer getCustomer() { return customer; }
    public IRoom getRoom() { return room; }
    public Date getCheckInDate() { return new Date(checkInDate.getTime()); }
    public Date getCheckOutDate() { return new Date(checkOutDate.getTime()); }

    @Override
    public String toString() {
        return "Reservation:\n" +
                "Customer: First Name: " + customer.getFirstName() + ", Last Name: " + customer.getLastName() + ", Email: " + customer.getEmail() + "\n" +
                "Room: " + room + "\n" +
                "Check-In Date: " + checkInDate + "\n" +
                "Check-Out Date: " + checkOutDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation)) return false;
        Reservation that = (Reservation) o;
        return customer.equals(that.customer) &&
                room.equals(that.room) &&
                checkInDate.equals(that.checkInDate) &&
                checkOutDate.equals(that.checkOutDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customer, room, checkInDate, checkOutDate);
    }
}
