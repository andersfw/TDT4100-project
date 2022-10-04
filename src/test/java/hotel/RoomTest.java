package hotel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RoomTest {
    
    Room rom1; 
    Room rom2; 
    Room rom3; 
    Room rom4; 

    @BeforeEach
    public void setUp() {
        rom1 = new Room("123", 5, 500); 
        rom2 = new Room("543", 2, 300); 
        rom3 = new Room("672", 2, 300); 
        rom4 = new Room("823", 3, 300); 
    }

    @Test
    @DisplayName("Sjekke at konstruktøren er rett")
    public void checkConstructor() {
        // Sjekker rom1 
        assertEquals("123", rom1.getRoomNr(), "Feil romnummer i konstruktør.");
        assertEquals(5, rom1.getBeds(), "Feil antall senger i konstruktør");
        assertEquals(500, rom1.getPrice(), "Feil pris i konstruktør");
        // Sjekker rom2 
        assertEquals("543", rom2.getRoomNr(), "Feil romnummer i konstruktør.");
        assertEquals(2, rom2.getBeds(), "Feil antall senger i konstruktør");
        assertEquals(300, rom2.getPrice(), "Feil pris i konstruktør");
        // Sjekker innkapsling i konstruktør 
        assertThrows(IllegalArgumentException.class, () -> {new Room("A12", 3, 200);}, "Ingen bokstaver i romnummeret");
        assertThrows(IllegalArgumentException.class, () -> {new Room("123", -1, 200);}, "Kan ikke ha negative verdier");
        assertThrows(IllegalArgumentException.class, () -> {new Room("123", -1, -200);}, "Kan ikke ha negative verdier");
    }

    @Test
    @DisplayName("Sjekke at bokkede datoer blir behandlet riktig")
    public void checkIsOccupied() {
        // Sjekker for en bestilling
        rom1.orderRoom("2022-06-10", 2);
        assertTrue(rom1.isOccupied("2022-06-10", 1), "Kan ikke bestille samme rom som noen andre til samme tid");
        assertTrue(rom1.isOccupied("2022-06-10", 2), "Kan ikke bestille samme rom som noen andre til samme tid");
        assertTrue(rom1.isOccupied("2022-06-10", 3), "Kan ikke bestille samme rom som noen andre til samme tid");
        assertFalse(rom1.isOccupied("2022-06-12", 2), "Kan bestille fra dagen de andre drar");
        assertFalse(rom1.isOccupied("2022-06-13", 2), "Kan bestille alle dager etter");
        assertFalse(rom1.isOccupied("2022-06-08", 2), "Kan bestille slik at du drar dagen de andre kommer");
        
        // Sjekke for to bestillinger (at det ikke bare fungerer for en dato)
        rom1.orderRoom("2022-06-15", 2);
        assertTrue(rom1.isOccupied("2022-06-15", 1), "Kan ikke bestille samme rom som noen andre til samme tid");
        assertTrue(rom1.isOccupied("2022-06-15", 2), "Kan ikke bestille samme rom som noen andre til samme tid");
        assertTrue(rom1.isOccupied("2022-06-15", 3), "Kan ikke bestille samme rom som noen andre til samme tid");
        assertTrue(rom1.isOccupied("2022-06-12", 6), "Kan ikke bestille samme rom som noen andre til samme tid");
        assertTrue(rom1.isOccupied("2022-06-08", 10), "Kan ikke bestille samme rom som noen andre til samme tid");
        assertFalse(rom1.isOccupied("2022-06-12", 3), "Kan bestille mellom to bookede datoer");
    }

    @Test
    @DisplayName("Sjekke at bestilling og avbestilling fungerer som tiltenkt")
    public void checkOrdering() {
        rom1.orderRoom("2022-06-01", 5);
        rom1.orderRoom("2022-06-15", 10);
        rom2.orderRoom("2022-08-05", 5);
        rom2.orderRoom("2022-08-15", 10);
        rom3.orderRoom("2022-10-15", 10);
        rom4.orderRoom("2022-06-20", 10);
        // Sjekke at du ikke kan bestille et rom som er opptatt
        assertThrows(IllegalStateException.class, () -> {rom1.orderRoom("2022-06-03", 3);});
        assertThrows(IllegalStateException.class, () -> {rom1.orderRoom("2022-06-15", 3);});
        assertThrows(IllegalStateException.class, () -> {rom1.orderRoom("2022-06-20", 3);});
        assertThrows(IllegalStateException.class, () -> {rom2.orderRoom("2022-08-13", 10);});
        assertThrows(IllegalStateException.class, () -> {rom2.orderRoom("2022-08-17", 3);});
        assertThrows(IllegalStateException.class, () -> {rom3.orderRoom("2022-10-15", 3);});
        assertThrows(IllegalStateException.class, () -> {rom4.orderRoom("2022-06-19", 3);});
        // Sjekke at du ikke kan avbestille datoer som ikke er booket
        assertThrows(IllegalStateException.class, () -> {rom1.cancelOrder("2020-10-10");});
        assertThrows(IllegalStateException.class, () -> {rom1.cancelOrder("2020-07-10");});
        assertThrows(IllegalStateException.class, () -> {rom1.cancelOrder("2020-10-15");});
        assertThrows(IllegalStateException.class, () -> {rom2.cancelOrder("2021-10-10");});
        assertThrows(IllegalStateException.class, () -> {rom2.cancelOrder("2021-03-22");});
        assertThrows(IllegalStateException.class, () -> {rom2.cancelOrder("2021-06-23");});
        assertThrows(IllegalStateException.class, () -> {rom3.cancelOrder("2020-10-10");});
        assertThrows(IllegalStateException.class, () -> {rom3.cancelOrder("2022-10-10");});
        assertThrows(IllegalStateException.class, () -> {rom4.cancelOrder("2024-10-10");});
        assertThrows(IllegalStateException.class, () -> {rom4.cancelOrder("2022-10-10");});
    }

    @Test
    @DisplayName("Teste sjekk av datoformat")
    public void checkDateFormat() {
        // Sjekker diverse ugydlige formater på dato ved bruk av orderRoom metoden
        assertThrows(IllegalArgumentException.class, () -> {rom1.orderRoom("12.12.2020", 3);});
        assertThrows(IllegalArgumentException.class, () -> {rom1.orderRoom("12.12.20", 3);});
        assertThrows(IllegalArgumentException.class, () -> {rom1.orderRoom("2020-3-3", 3);});
        assertThrows(IllegalArgumentException.class, () -> {rom1.orderRoom("2020-20-12", 3);});
        assertThrows(IllegalArgumentException.class, () -> {rom1.orderRoom("2020-0-0", 3);});
        assertThrows(IllegalArgumentException.class, () -> {rom1.orderRoom("24 desember", 3);});
        assertThrows(IllegalArgumentException.class, () -> {rom1.orderRoom("2020-mai-12", 3);});
        assertThrows(IllegalArgumentException.class, () -> {rom1.orderRoom("dette er en dato", 3);});
        assertThrows(IllegalArgumentException.class, () -> {rom1.orderRoom("20", 3);});
        assertThrows(IllegalArgumentException.class, () -> {rom1.orderRoom("bra", 3);});
        assertThrows(IllegalArgumentException.class, () -> {rom1.orderRoom("2020-03", 3);});
        assertThrows(IllegalArgumentException.class, () -> {rom1.orderRoom("12-12-20", 3);});
        assertThrows(IllegalArgumentException.class, () -> {rom1.orderRoom("12.12.20", 3);});
        assertThrows(IllegalArgumentException.class, () -> {rom1.orderRoom("2020.12.02", 3);});
    }
}
