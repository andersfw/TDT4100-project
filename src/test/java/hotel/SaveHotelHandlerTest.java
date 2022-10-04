package hotel;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class SaveHotelHandlerTest { 

    private Hotel hotel; 
    private SaveHotelHandler shh; 

    @BeforeEach
    public void setUp() {
        hotel = new Hotel(); 
        shh = new SaveHotelHandler(); 
        List<Room> rooms = new ArrayList<>();
        rooms = hotel.getRooms();
        hotel.newMember("98765432", "HelloWorld98");
        hotel.newMember("91234567", "UnicornWonder12");
        hotel.newMember("92652332", "123PacMan");
        hotel.newMember("42069420", "PlantUML33");
        hotel.login("98765432", "HelloWorld98");
        hotel.chooseRoom(rooms.get(0));
        hotel.orderRoom(LocalDate.now().plusDays(2).toString(), "3");
        hotel.chooseRoom(rooms.get(3));
        hotel.orderRoom(LocalDate.now().plusDays(2).toString(), "3");
        hotel.login("91234567", "UnicornWonder12");
        hotel.chooseRoom(rooms.get(1));
        hotel.orderRoom(LocalDate.now().plusDays(2).toString(), "3");
        hotel.chooseRoom(rooms.get(0));
        hotel.orderRoom(LocalDate.now().plusDays(10).toString(), "2");
    }

    @Test
    @DisplayName("Tester skriving til fil") 
    public void testWriteFile() throws FileNotFoundException {
        shh.save(hotel, "test_file"); // Sjekker at det går an å lagre til fil 
        assertThrows(IllegalArgumentException.class, () -> {shh.save(hotel, "");}, "Kan ikke lagre fil med tomt filnavn"); 
        // Tester at du ikke kan lagre fil som starter på "wrong", siden det er filer forbeholdt testing 
        assertThrows(IllegalArgumentException.class, () -> {shh.save(hotel, "wrong_online");}, "Sjekker at du ikke kan lagre fil med 'wrong', siden det er testfiler");
        assertThrows(IllegalArgumentException.class, () -> {shh.save(hotel, "Wrong");}, "Sjekker at du ikke kan lagre fil med 'wrong', siden det er testfiler");
        assertThrows(IllegalArgumentException.class, () -> {shh.save(hotel, "abakuswrong");}, "Sjekker at du ikke kan lagre fil med 'wrong', siden det er testfiler");
    }
    
    @Test
    @DisplayName("Tester lesing fra fil") 
    public void testLoadFile() throws FileNotFoundException {
        shh.save(hotel, "test_file");
        shh.save(hotel, "test_file_2");
        assertEquals(hotel, shh.load("test_file"), "Sjekker at hotelet lest fra fil blir likt hotellet som ble skrevet til fil");
        hotel.logout();
        hotel.chooseRoom(null);
        shh.save(hotel, "test_file");
        assertEquals(hotel, shh.load("test_file"), "Sjekker at filen oppdateres/overskrives med nye endringer");
        assertNotEquals(hotel, shh.load("test_file_2"), "Sjekker at hotellet lest fra gammel fil ikke har de nye endringene");
    }
    
    @Test
    @DisplayName("Tester lesing fra fil som ikke finnes")
    public void testNonExistantFile() {
        assertThrows(IllegalArgumentException.class, () -> {shh.load("");}, "Kan ikke laste fil med tomt filnavn");
        assertThrows(FileNotFoundException.class, () -> {shh.load("finnes_ikke");}, "Kan ikke lese inn fra en fil som ikke finnes");
        assertThrows(FileNotFoundException.class, () -> {shh.load("non_existing");}, "Kan ikke lese inn fra en fil som ikke finnes");
        assertThrows(FileNotFoundException.class, () -> {shh.load("hva_med_denne");}, "Kan ikke lese inn fra en fil som ikke finnes");
        assertThrows(FileNotFoundException.class, () -> {shh.load("420_69");}, "Kan ikke lese inn fra en fil som ikke finnes");
        assertThrows(FileNotFoundException.class, () -> {shh.load("snack_overflow");}, "Kan ikke lese inn fra en fil som ikke finnes");
        assertThrows(FileNotFoundException.class, () -> {shh.load("abaFile");}, "Kan ikke lese inn fra en fil som ikke finnes");
        assertThrows(FileNotFoundException.class, () -> {shh.load("fileNotFound");}, "Kan ikke lese inn fra en fil som ikke finnes");
    }

    @Test
    @DisplayName("Tester feil i fil")
    public void testWrongFile() throws FileNotFoundException {
        // høre med magnus om hvordan man kan gjøre dette. Enten skrive inn fila i metoden, eller bevisst lage filer med feil. 
        //assertEquals(Files.mismatch(path, path2), actual);
        
        assertThrows(IllegalArgumentException.class, () -> {shh.load("wrong_beds");}, "Sjekker unntak ved feil i fil");
        assertThrows(IllegalArgumentException.class, () -> {shh.load("wrong_date");}, "Sjekker unntak ved feil i fil");
        assertThrows(IllegalArgumentException.class, () -> {shh.load("wrong_format");}, "Sjekker unntak ved feil i fil");
        assertThrows(IllegalArgumentException.class, () -> {shh.load("wrong_format2");}, "Sjekker unntak ved feil i fil");
        assertThrows(IllegalArgumentException.class, () -> {shh.load("wrong_format3");}, "Sjekker unntak ved feil i fil");
        assertThrows(IllegalArgumentException.class, () -> {shh.load("wrong_phoneNumber");}, "Sjekker unntak ved feil i fil");
        assertThrows(IllegalArgumentException.class, () -> {shh.load("wrong_password");}, "Sjekker unntak ved feil i fil");
        assertThrows(IllegalArgumentException.class, () -> {shh.load("wrong_price");}, "Sjekker unntak ved feil i fil");
        assertThrows(IllegalArgumentException.class, () -> {shh.load("wrong_roomList");}, "Sjekker unntak ved feil i fil");
        assertThrows(IllegalArgumentException.class, () -> {shh.load("wrong_roomNr");}, "Sjekker unntak ved feil i fil");

    }

    @AfterAll
    static void teardown() {
        File testFile = new File(SaveHotelHandler.getFilePath("test_file"));
        testFile.delete();
        File testfile2 = new File(SaveHotelHandler.getFilePath("test_file_2"));
        testfile2.delete();
        File wrong1 = new File(SaveHotelHandler.getFilePath("wrong1"));
        wrong1.delete();
    }

    

}
