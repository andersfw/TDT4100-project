package hotel;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javafx.util.Pair;

public class CancelSortTest {
    
    private Room room1, room2, room3, room4, room5, room6;
    private String date1, date2, date3; 

    @BeforeEach
    public void setUp() {
        room1 = new Room("123", 5, 500);   
        room2 = new Room("923", 2, 300);   
        room3 = new Room("352", 3, 300);   
        room4 = new Room("323", 5, 500);   
        room5 = new Room("152", 4, 400);   
        room6 = new Room("329", 5, 500);   
        date1 = LocalDate.now().toString();
        date2 = LocalDate.now().plusDays(3).toString();
        date3 = LocalDate.now().plusDays(4).toString();
    }
    
    @Test
    @DisplayName("Tester CancelSort") 
    public void testCancelSort() {
        List<Pair<Room, String>> expected = new ArrayList<>(); 
        expected.add(new Pair<Room,String>(room1, date1));
        expected.add(new Pair<Room,String>(room3, date1));
        expected.add(new Pair<Room,String>(room2, date1));
        expected.add(new Pair<Room,String>(room5, date2));
        expected.add(new Pair<Room,String>(room4, date2));
        expected.add(new Pair<Room,String>(room6, date3));
        
        List<Pair<Room, String>> actual = new ArrayList<>(); 
        actual.add(new Pair<Room,String>(room3, date1));
        actual.add(new Pair<Room,String>(room2, date1));
        actual.add(new Pair<Room,String>(room6, date3));
        actual.add(new Pair<Room,String>(room4, date2));
        actual.add(new Pair<Room,String>(room1, date1));
        actual.add(new Pair<Room,String>(room5, date2));

        actual.sort(new CancelSort());
        assertEquals(expected, actual);    
        Collections.shuffle(actual);
        
        actual.sort(new CancelSort());
        assertEquals(expected, actual);    
        Collections.shuffle(actual);

        actual.sort(new CancelSort());
        assertEquals(expected, actual);    
        Collections.shuffle(actual);
    }
    
}
