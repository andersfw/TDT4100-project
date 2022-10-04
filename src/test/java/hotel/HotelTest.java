package hotel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javafx.util.Pair;

public class HotelTest {
    
    Hotel hotel; 
    List<Room> rooms = new ArrayList<>(); 
    List<Member> members = new ArrayList<>(); 

    @BeforeEach
    public void setUp() {
        rooms.add(new Room("113", 5, 500));
        rooms.add(new Room("163", 5, 500));
        rooms.add(new Room("124", 3, 500));
        rooms.add(new Room("273", 4, 200));
        rooms.add(new Room("263", 3, 800));
        rooms.add(new Room("533", 4, 600));
        rooms.add(new Room("413", 3, 300));
        rooms.add(new Room("843", 6, 900));
        rooms.add(new Room("122", 4, 700));
        rooms.add(new Room("128", 3, 300));
        rooms.add(new Room("456", 2, 500));
        rooms.add(new Room("693", 2, 500));
        rooms.add(new Room("323", 3, 700));
        rooms.add(new Room("210", 4, 800));
        rooms.add(new Room("211", 4, 800));
        rooms.add(new Room("223", 4, 500));

        members.add(new Member("98765432", "WOW123wow"));
        members.add(new Member("45636565", "Online420"));
        members.add(new Member("42987898", "Abakus123"));
        
        hotel = new Hotel(); 
        hotel.newMember("98765432", "WOW123wow");
        hotel.newMember("45636565", "Online420");
        hotel.newMember("42987898", "Abakus123");
    }

    @Test
    @DisplayName("Sjekke tom konstruktør") 
    public void checkEmptyConstructor() {
        Hotel emptyHotel = new Hotel();     // Bruker tom konstruktør, og sjekker at hotellet starter i tiltenkt tilstand 
        assertEquals(false, emptyHotel.isMember(), "Sjekke at ingen er logget inn");
        assertEquals(null, emptyHotel.getMember(), "Sjekke at ingen er logget inn");
        assertEquals(null, emptyHotel.getChosenRoom(), "Sjekke at ingen rom er valgt");
        assertEquals(new ArrayList<>(), emptyHotel.getMembers(), "Sjekke at hotellet ikke har noen medlemmer");
        assertEquals("HotelBooking AS", emptyHotel.getName(), "Sjekker at hotellet blir satt til default navn");
        assertEquals(rooms, emptyHotel.getRooms(), "Sjekker at rommene er satt riktig");
    }
    
    @Test
    @DisplayName("Sjekke konstruktør med input") 
    public void checkConstructor() {
        Hotel fullHotel = new Hotel("Best Hotel", members.get(1), rooms.get(1), members, rooms);
        assertEquals(true, fullHotel.isMember(), "Sjekke at ingen er logget inn");
        assertEquals(members.get(1) , fullHotel.getMember(), "Sjekke at riktig medlem er logget inn");
        assertEquals(rooms.get(1), fullHotel.getChosenRoom(), "Sjekke at riktig rom er valgt");
        assertEquals(rooms, fullHotel.getRooms());
        assertEquals(members, fullHotel.getMembers());
        // Sjekker to ulike tilfeller, slik at ting ikke kan være hardkodet 
        Hotel fullHotel2 = new Hotel("Best Hotel", members.get(2), rooms.get(2), members, rooms);
        assertEquals(true, fullHotel2.isMember(), "Sjekke at ingen er logget inn");
        assertEquals(members.get(2) , fullHotel2.getMember(), "Sjekke at riktig medlem er logget inn");
        assertEquals(rooms.get(2), fullHotel2.getChosenRoom(), "Sjekke at riktig rom er valgt");
        assertEquals(rooms, fullHotel2.getRooms());
        assertEquals(members, fullHotel2.getMembers());
    }

    @Test
    @DisplayName("Sjekke søk etter rom") 
    public void checkGetRooms() {
        // Sjekker søk som stemmer bare med ett rom
        assertEquals(Arrays.asList(rooms.get(3)), hotel.getRooms("1", "200", "2020-03-12", "3"));
        // Sjekker søk som stemmer med så mange at begrensningen på 15 blir gjeldende
        List<Room> tmp = new ArrayList<>(rooms);
        tmp.sort(new SearchSort());
        assertEquals(tmp.subList(0, 15), hotel.getRooms("1", "2000", "2020-03-12", "3"));
        assertEquals(tmp.subList(0, 15), hotel.getRooms("1", "3000", "2020-03-12", "3"));
        assertEquals(tmp.subList(0, 15), hotel.getRooms("2", "2000", "2020-03-12", "3"));
        // Sjekker søk som stemmer med få rom
        assertEquals(Arrays.asList(rooms.get(7), rooms.get(0), rooms.get(1)), hotel.getRooms("5", "2000", "2020-03-12", "1"));
        // Sjekker søk som stemmer med ingen rom 
        assertEquals(new ArrayList<>(), hotel.getRooms("10", "2000", "2020-03-12", "1"));
        assertEquals(new ArrayList<>(), hotel.getRooms("10", "0", "2020-03-12", "1"));
        // Sjekker validering av getRooms()
        // Sjekker ugyldig antall senger
        assertThrows(IllegalArgumentException.class, () -> {hotel.getRooms("a", "2000", "2020-12-12", "3");});
        assertThrows(IllegalArgumentException.class, () -> {hotel.getRooms("-", "2000", "2020-12-12", "3");});
        assertThrows(IllegalArgumentException.class, () -> {hotel.getRooms("A", "2000", "2020-12-12", "3");});
        assertThrows(IllegalArgumentException.class, () -> {hotel.getRooms("-1", "2000", "2020-12-12", "3");});
        assertThrows(IllegalArgumentException.class, () -> {hotel.getRooms("0", "1000", "2020-11-12", "?!");});
        assertThrows(IllegalArgumentException.class, () -> {hotel.getRooms(null, "1000", "2020-11-12", "?!");});
        // Sjekker ugyldig pris 
        assertThrows(IllegalArgumentException.class, () -> {hotel.getRooms("2", "-1000", "2020-12-12", "3");});
        assertThrows(IllegalArgumentException.class, () -> {hotel.getRooms("2", "a", "2020-12-12", "3");});
        assertThrows(IllegalArgumentException.class, () -> {hotel.getRooms("2", "?", "2020-12-12", "3");});
        // Sjekker ugyldig dato 
        assertThrows(IllegalArgumentException.class, () -> {hotel.getRooms("2", "1000", "år-måned-dag", "3");});
        assertThrows(IllegalArgumentException.class, () -> {hotel.getRooms("2", "1000", "yyyy-mm-dd", "3");});
        assertThrows(IllegalArgumentException.class, () -> {hotel.getRooms("2", "1000", "2020-31-12", "3");});
        assertThrows(IllegalArgumentException.class, () -> {hotel.getRooms("2", "1000", "2020-11-50", "3");});
        // Sjekker ugydlig antall netter 
        assertThrows(IllegalArgumentException.class, () -> {hotel.getRooms("2", "1000", "2020-11-12", "-1");});
        assertThrows(IllegalArgumentException.class, () -> {hotel.getRooms("2", "1000", "2020-11-12", "a");});
        assertThrows(IllegalArgumentException.class, () -> {hotel.getRooms("2", "1000", "2020-11-12", "--");});
        assertThrows(IllegalArgumentException.class, () -> {hotel.getRooms("2", "1000", "2020-11-12", "?!");});
    }

    @Test
    @DisplayName("Sjekke bestilling av rom") 
    public void checkOrderRoom(){
        // Sjekker at du ikke kan bestille om du ikke er logget inn og om du ikke har valgt et rom 
        assertThrows(IllegalStateException.class, () -> hotel.orderRoom("2020-12-12", "3"), "Du må ha valgt rom/være logget inn "); 
        hotel.login("98765432", "WOW123wow");
        assertThrows(IllegalStateException.class, () -> hotel.orderRoom("2020-12-12", "3"), "Du må ha valgt rom/være logget inn "); 
        hotel.logout();
        hotel.chooseRoom(rooms.get(1));
        assertThrows(IllegalStateException.class, () -> hotel.orderRoom("2020-12-12", "3"), "Du må ha valgt rom/være logget inn "); 
        hotel.login("98765432", "WOW123wow");

        // Sjekker at du kan bestille 
        hotel.orderRoom("2020-12-12", "3");
        assertEquals(Arrays.asList(new Pair<Room, String>(rooms.get(1), "2020-12-12")), hotel.getMemberOrders(), "Sjekke at bestillingen blir lagt på medlemmet");
    }

    @Test
    @DisplayName("Sjekke utregning av pris") 
    public void checkPrice(){
        // Sjekker pris uten å være logget inn
        assertEquals(1500, hotel.calculatePrice(rooms.get(1), "3"), "Sjekker at prisen blir regnet riktig med antall netter");
        assertEquals(2000, hotel.calculatePrice(rooms.get(3), "10"), "Sjekker at prisen blir regnet riktig med antall netter");
        assertEquals(600, hotel.calculatePrice(rooms.get(5), "1"), "Sjekker at prisen blir regnet riktig med antall netter");
        assertEquals(1800, hotel.calculatePrice(rooms.get(7), "2"), "Sjekker at prisen blir regnet riktig med antall netter");
        assertEquals(1000, hotel.calculatePrice(rooms.get(10), "2"), "Sjekker at prisen blir regnet riktig med antall netter");
        assertEquals(1000, hotel.calculatePrice(rooms.get(0), "2"), "Sjekker at prisen blir regnet riktig med antall netter");
        // Sjekker pris etter du er logget inn 
        hotel.login("98765432", "WOW123wow");
        assertEquals(1200, hotel.calculatePrice(rooms.get(1), "3"), "Sjekker at prisen blir regnet riktig med antall netter og medlemsbonus på 20%");
        assertEquals(1600, hotel.calculatePrice(rooms.get(3), "10"), "Sjekker at prisen blir regnet riktig med antall netter og medlemsbonus på 20%");
        assertEquals(480, hotel.calculatePrice(rooms.get(5), "1"), "Sjekker at prisen blir regnet riktig med antall netter og medlemsbonus på 20%");
        assertEquals(1440, hotel.calculatePrice(rooms.get(7), "2"), "Sjekker at prisen blir regnet riktig med antall netter og medlemsbonus på 20%");
        assertEquals(800, hotel.calculatePrice(rooms.get(10), "2"), "Sjekker at prisen blir regnet riktig med antall netter og medlemsbonus på 20%");
        assertEquals(800, hotel.calculatePrice(rooms.get(0), "2"), "Sjekker at prisen blir regnet riktig med antall netter og medlemsbonus på 20%");
        // Sjekker ugyldige verdier
        assertThrows(IllegalArgumentException.class, () -> hotel.calculatePrice(rooms.get(1), "-1"));
        assertThrows(IllegalArgumentException.class, () -> hotel.calculatePrice(rooms.get(1), "a"));
        assertThrows(IllegalArgumentException.class, () -> hotel.calculatePrice(rooms.get(1), "?"));
        assertThrows(IllegalArgumentException.class, () -> hotel.calculatePrice(rooms.get(1), "-"));
        assertThrows(IllegalArgumentException.class, () -> hotel.calculatePrice(rooms.get(1), ""));
    }

    @Test
    @DisplayName("Sjekke medlemsfunksjoner") 
    public void checkMemberMethods() {
        // Sjekker login
        // Sjekker at du ikke kan logge inn med brukere som ikke finnes
        assertFalse(hotel.login("98789878", "MuchWow08"));
        assertFalse(hotel.login("42069420", "DogeMuchWow123"));
        assertFalse(hotel.login("90909090", "3lonMusk"));
        assertFalse(hotel.login("41111111", "XÆA-12_Musk"));
        // Sjekker at du ikke kan logge inn med feil telefonnummer/passord
        assertFalse(hotel.login("98765432", "ABCdefghjiKLMno123"));
        assertFalse(hotel.login("98765432", "Ubuntu87"));
        assertFalse(hotel.login("45636565", "Commodore64"));
        // Sjekker at du ikke kan logge inn med andre sine passord 
        assertFalse(hotel.login("45636565", "WOW123wow"));
        assertFalse(hotel.login("42987898", "WOW123wow"));

        // Sjekker at du kan logge inn 
        assertTrue(hotel.login("98765432", "WOW123wow"));
        hotel.login("98765432", "WOW123wow");
        assertTrue(hotel.isMember());
        assertEquals(Arrays.asList(members.get(0)), Arrays.asList(hotel.getMember()));
        hotel.logout();
        assertFalse(hotel.isMember());
        assertTrue(hotel.login("45636565", "Online420"));
        hotel.login("45636565", "Online420");
        assertEquals(Arrays.asList(members.get(1)), Arrays.asList(hotel.getMember()));
        assertTrue(hotel.isMember());
        hotel.logout();
        assertFalse(hotel.isMember());
    }
}
