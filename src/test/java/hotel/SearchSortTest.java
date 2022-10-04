package hotel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SearchSortTest {
    
    @Test
    @DisplayName("Tester search sort")
    public void testSort() {
        // Lager alle objektene som trengs til testen 
        Room room1 = new Room("123", 5, 500);   
        Room room2 = new Room("923", 2, 300);   
        Room room3 = new Room("352", 3, 300);   
        Room room4 = new Room("152", 4, 400);   
        Room room5 = new Room("323", 5, 500);   
        Room room6 = new Room("329", 5, 500);   
        List<Room> rooms = new ArrayList<Room>(Arrays.asList(room1, room2, room3, room4, room5, room6)); 
        List<Room> expected = new ArrayList<Room>(Arrays.asList(room1, room5, room6, room4, room3, room2)); 
        SearchSort sorter = new SearchSort(); 
        // Sorterer ved bruk av searchSort
        Collections.sort(rooms, sorter);
        // Sjekker at sortert liste stemmer med forventet sortering
        assertEquals(expected, rooms);
        // Sjekker at tilfeldig "rotet" liste blir sortert riktig
        Collections.shuffle(rooms);
        Collections.sort(rooms, sorter); 
        assertEquals(expected, rooms); 
        // Sjekker at tilfeldig "rotet" liste blir sortert riktig
        Collections.shuffle(rooms);
        Collections.sort(rooms, sorter); 
        assertEquals(expected, rooms); 
        // Sjekker at tilfeldig "rotet" liste blir sortert riktig
        Collections.shuffle(rooms);
        Collections.sort(rooms, sorter); 
        assertEquals(expected, rooms); 
    }
}
