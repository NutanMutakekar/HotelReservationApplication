import service.ReservationService;
import model.Room;
import model.RoomType;
import ui.MainMenu;

public class HotelReservation {
    public static void main(String[] args) {
        ReservationService rs = ReservationService.getInstance();
        // preload sample rooms
        rs.registerRoom(new Room("101", 100.0, RoomType.SINGLE));
        rs.registerRoom(new Room("102", 200.0, RoomType.DOUBLE));
        rs.registerRoom(new Room("103", 150.0, RoomType.SINGLE));
        rs.registerRoom(new Room("105", 0.0, RoomType.SINGLE));
        rs.registerRoom(new Room("108", 0.0, RoomType.SINGLE));


        MainMenu.display();
    }
}
