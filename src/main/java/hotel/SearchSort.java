package hotel;

import java.util.Comparator;

public class SearchSort implements Comparator<Room> {

    @Override
    public int compare(Room room1, Room room2) {
        if (room1.getBeds() == room2.getBeds()) {
            return room1.getRoomNr().compareTo(room2.getRoomNr());
        }
        return room2.getBeds() - room1.getBeds(); 
    }
    
}
