package model;

import java.util.Objects;

public class Room implements IRoom {
    private final String roomNumber;
    private final Double price;
    private final RoomType roomType;

    public Room(String roomNumber, Double price, RoomType roomType) {
        this.roomNumber = roomNumber;
        this.price = price;
        this.roomType = roomType;
    }

    @Override
    public String getRoomNumber() { return roomNumber; }

    @Override
    public Double getRoomPrice() { return price; }

    @Override
    public RoomType getRoomType() { return roomType; }

    @Override
    public boolean isFree() { return price != null && price == 0.0; }

    @Override
    public String toString() {
        return "Room Number: " + roomNumber +
                ", Price: " + (isFree() ? "Free" : "$" + String.format("%.2f", price)) +
                ", Type: " + roomType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return roomNumber.equals(room.roomNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomNumber);
    }
}
