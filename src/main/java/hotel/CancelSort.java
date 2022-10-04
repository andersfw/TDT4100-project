package hotel;

import java.time.LocalDate;
import java.util.Comparator;

import javafx.util.Pair;

public class CancelSort implements Comparator<Pair<Room, String>> {

    @Override
    public int compare(Pair<Room, String> order1, Pair<Room, String> order2) {
        if (order1.getValue().equals(order2.getValue())) {
            return (order1.getKey().getRoomNr().compareTo(order2.getKey().getRoomNr()));
        }
        else if (LocalDate.parse(order1.getValue()).isBefore(LocalDate.parse(order2.getValue()))) {
            return -1; 
        }
        else {
            return 1; 
        }




    }    
}
