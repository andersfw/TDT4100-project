package hotel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javafx.util.Pair;

public class MemberTest {
    
    Member member1; 
    Member member2; 

    Room room1; 
    Room room2; 
    Room room3; 
    Room room4; 
    Room room5; 

    // private List<Room> ro

    @BeforeEach
    public void setUp() {
        // Lager nye medlemmer
        member1 = new Member("98765432", "Jordbær123") ; 
        member2 = new Member("42987654", "Potet123") ; 
        // Lager nye rom som kan bestilles 
        room1 = new Room("123", 3, 200); 
        room2 = new Room("432", 5, 500); 
        room3 = new Room("321", 3, 300); 
        room4 = new Room("333", 2, 400); 
        room5 = new Room("765", 5, 600); 
    }

    @Test
    @DisplayName("Sjekke konstruktør") 
    public void checkConstructor() {
        // Sjekker at meldemmene fra beforeEach får riktig nummer
        assertEquals("98765432", member1.getphoneNumber(), "Mobilnummer blir feil i konstruktør");
        assertEquals("42987654", member2.getphoneNumber(), "Mobilnummer blir feil i konstruktør");
        // Sjekker validering av passord
        assertThrows(IllegalArgumentException.class, () -> {new Member("98765432", "a");}, "Passordvalidering er feil"); 
        assertThrows(IllegalArgumentException.class, () -> {new Member("98765432", "A");}, "Passordvalidering er feil"); 
        assertThrows(IllegalArgumentException.class, () -> {new Member("98765432", "1");}, "Passordvalidering er feil"); 
        assertThrows(IllegalArgumentException.class, () -> {new Member("98765432", "Andesuppe");}, "Passordvalidering er feil"); 
        assertThrows(IllegalArgumentException.class, () -> {new Member("98765432", "passord123");}, "Passordvalidering er feil"); 
        assertThrows(IllegalArgumentException.class, () -> {new Member("98765432", "PASSORD");}, "Passordvalidering er feil"); 
        assertThrows(IllegalArgumentException.class, () -> {new Member("98765432", "Pas123");}, "Passordvalidering er feil"); 
        // Sjekker validering av telefonnummer 
        assertThrows(IllegalArgumentException.class, () -> {new Member("Mamma", "Passord123");}, "Telefonvalidering er feil"); 
        assertThrows(IllegalArgumentException.class, () -> {new Member("123", "Passord123");}, "Telefonvalidering er feil"); 
        assertThrows(IllegalArgumentException.class, () -> {new Member("567312Ab", "Passord123");}, "Telefonvalidering er feil"); 
        assertThrows(IllegalArgumentException.class, () -> {new Member("567312a2", "Passord123");}, "Telefonvalidering er feil"); 
        assertThrows(IllegalArgumentException.class, () -> {new Member("567312a2", "Passord123");}, "Telefonvalidering er feil"); 
    }

    @Test
    @DisplayName("Sjekke login metoder") 
    public void checkLogin() {
        // Sjekker at du kan logge inn 
        assertTrue(member1.login("98765432", "Jordbær123"));
        assertTrue(member2.login("42987654", "Potet123"));
        // Sjekker at login ikke er gyldig dersom passord og/eller telefonummer er feil 
        assertFalse(member1.login("42987654", "Potet123"), "Ikke riktig brukernavn eller passord");
        assertFalse(member2.login("98765432", "Jordbær123"), "Ikke riktig brukernavn eller passord");
        assertFalse(member1.login("12345678", "Jordbær123"), "Ikke riktig telefonnummer");
        assertFalse(member1.login("98765432", "Ananas420"), "Ikke riktig passord");
        assertFalse(member2.login("95231234", "Potet123"), "Ikke riktig telefonnummer");
        assertFalse(member2.login("42987654", "Origami96"), "Ikke riktig passord");
    }

    @Test
    @DisplayName("Sjekke bestilling") 
    public void checkOrder() {
        List<Pair<Room, String>> orders = new ArrayList<>();
        // Sjekker at et medlem kan bestille flere rom og at disse legges i liste riktig 
        member1.orderRoom(room1, "2020-03-10", 10);
        orders.add(new Pair<Room,String>(room1, "2020-03-10"));
        orders.sort(new CancelSort());
        assertEquals(orders, member1.getOrdersAndDates());
        // Rekkefølgen på rommene er i henhold til CancelSort.java comparator
        member1.orderRoom(room2, "2020-03-10", 10);
        orders.add(new Pair<Room,String>(room2, "2020-03-10"));
        orders.sort(new CancelSort());
        assertEquals(orders, member1.getOrdersAndDates());
        member1.orderRoom(room3, "2020-03-10", 10);
        orders.add(new Pair<Room,String>(room3, "2020-03-10"));
        orders.sort(new CancelSort());
        assertEquals(orders, member1.getOrdersAndDates());
        member1.orderRoom(room4, "2020-03-10", 10);
        orders.add(new Pair<Room,String>(room4, "2020-03-10"));
        orders.sort(new CancelSort());
        assertEquals(orders, member1.getOrdersAndDates());
        member1.orderRoom(room5, "2020-03-10", 10);
        orders.add(new Pair<Room,String>(room5, "2020-03-10"));
        orders.sort(new CancelSort());
        assertEquals(orders, member1.getOrdersAndDates());

        // Sjekker at man ikke kan booke rom som allerede er booket 
        assertThrows(IllegalStateException.class, () -> {member1.orderRoom(room1, "2020-03-10", 3);}, "Tester å bestille samme rom som en annen bruker");
        assertThrows(IllegalStateException.class, () -> {member1.orderRoom(room1, "2020-03-13", 3);}, "Tester å bestille samme rom som en annen bruker");
        assertThrows(IllegalStateException.class, () -> {member1.orderRoom(room1, "2020-03-16", 3);}, "Tester å bestille samme rom som en annen bruker");
        assertThrows(IllegalStateException.class, () -> {member2.orderRoom(room1, "2020-03-10", 10);}, "Tester å bestille samme rom som en annen bruker");
        assertThrows(IllegalStateException.class, () -> {member2.orderRoom(room1, "2020-03-10", 3);}, "Tester å bestille samme rom som en annen bruker");
        assertThrows(IllegalStateException.class, () -> {member2.orderRoom(room1, "2020-03-13", 3);}, "Tester å bestille samme rom som en annen bruker");
        assertThrows(IllegalStateException.class, () -> {member2.orderRoom(room2, "2020-03-19", 3);}, "Tester å bestille samme rom som en annen bruker");
        assertThrows(IllegalStateException.class, () -> {member2.orderRoom(room2, "2020-03-13", 3);}, "Tester å bestille samme rom som en annen bruker");
        assertThrows(IllegalStateException.class, () -> {member2.orderRoom(room3, "2020-03-12", 3);}, "Tester å bestille samme rom som en annen bruker");
        assertThrows(IllegalStateException.class, () -> {member2.orderRoom(room4, "2020-03-12", 3);}, "Tester å bestille samme rom som en annen bruker");
        assertThrows(IllegalStateException.class, () -> {member2.orderRoom(room5, "2020-03-12", 3);}, "Tester å bestille samme rom som en annen bruker");
    }
    
    @Test
    @DisplayName("Sjekke avbestilling") 
    public void checkCancelOrder() {
        // Bestiller og sjekker at disse er mulig å hente ut igjen 
        member1.orderRoom(room1, "2020-03-10", 10);
        member1.orderRoom(room2, "2020-03-10", 10);
        member1.orderRoom(room3, "2020-03-10", 10);
        member1.orderRoom(room4, "2020-03-10", 10);
        member1.orderRoom(room5, "2020-03-10", 10); 

        List<Pair<Room, String>> orders = new ArrayList<>();
        Pair<Room, String> order1 = new Pair<Room,String>(room1, "2020-03-10");
        Pair<Room, String> order2 = new Pair<Room,String>(room2, "2020-03-10");
        Pair<Room, String> order3 = new Pair<Room,String>(room3, "2020-03-10");
        Pair<Room, String> order4 = new Pair<Room,String>(room4, "2020-03-10");
        Pair<Room, String> order5 = new Pair<Room,String>(room5, "2020-03-10");
        orders.add(order1);
        orders.add(order2);
        orders.add(order3);
        orders.add(order4);
        orders.add(order5);
        orders.sort(new CancelSort()); // Bruker comparatoren her, siden den antas å fungere som tiltenkt, og dette blir testet i egen test 
        assertEquals(orders, member1.getOrdersAndDates(), "Sjekker at bestillte rom er riktig iforhold til forventet.");

        // Sjekker at avbestillingen i en annen rekkefølge enn de bestilit i fungerer som tiltenkt  
        // Rekkefølgen på rommene er i henhold til CancelSort.java comparator
        member1.cancelOrder(room1); 
        orders.remove(order1);
        assertEquals(orders, member1.getOrdersAndDates());
        assertThrows(IllegalStateException.class, () -> {member1.cancelOrder(room1);}, "Sjekker at du ikke kan avbestille et rom du ikke har");
        member1.cancelOrder(room4); 
        orders.remove(order4);
        assertEquals(orders, member1.getOrdersAndDates());
        assertThrows(IllegalStateException.class, () -> {member1.cancelOrder(room4);}, "Sjekker at du ikke kan avbestille et rom du ikke har");
        member1.cancelOrder(room5); 
        orders.remove(order5);
        assertEquals(orders, member1.getOrdersAndDates());
        assertThrows(IllegalStateException.class, () -> {member1.cancelOrder(room5);}, "Sjekker at du ikke kan avbestille et rom du ikke har");
        member1.cancelOrder(room3); 
        orders.remove(order3);
        assertEquals(orders, member1.getOrdersAndDates());
        assertThrows(IllegalStateException.class, () -> {member1.cancelOrder(room3);}, "Sjekker at du ikke kan avbestille et rom du ikke har");
        // Sjekker at et rom er opptatt selvom en annen prøver å avbestille samme rom 
        member1.orderRoom(room3, "2020-03-10", 10);
        member1.orderRoom(room4, "2020-03-10", 10);
        member1.orderRoom(room5, "2020-03-10", 10);
        assertThrows(IllegalStateException.class, () -> {member2.cancelOrder(room3);}, "Sjekker at en annen ikke kan avbestille samme rom som en annen");
        assertThrows(IllegalStateException.class, () -> {member2.cancelOrder(room4);}, "Sjekker at en annen ikke kan avbestille samme rom som en annen");
        assertThrows(IllegalStateException.class, () -> {member2.cancelOrder(room5);}, "Sjekker at en annen ikke kan avbestille samme rom som en annen");
    }

}
