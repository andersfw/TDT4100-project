package hotel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;

public class Room {

    // Felt 
    private String roomNr;      // Romnummer til rommet
    private int beds;           // Antall senger rommet har 
    private double price;       // Prisen til rommet per natt

    // Liste over datoer hvor rommet er opptatt. Hvert er i et par med format <Start, Slutt>
    private List<Pair<LocalDate, LocalDate>> occupiedDates = new ArrayList<>(); 

    // Konstruktør 
    public Room(String roomNr, int beds, double price) {
        checkRoomNr(roomNr);        // Sjekker at romnummer er på rett format
        this.roomNr = roomNr;       // Initialiserer romnummer
        requirePositive(beds);      // Sjekker at antall senger ikke er negativt 
        this.beds = beds;           // Initialiserer antall senger 
        requirePositive(price);     // Sjekker at pris er positivt
        this.price = price;         // Initialiserer pris 
    }

    // Konstruktør for lesing fra fil 
    public Room(String roomNr, int beds, double price, List<Pair<LocalDate, LocalDate>> dates) { 
        checkRoomNr(roomNr);
        this.roomNr = roomNr; 
        requirePositive(beds);
        this.beds = beds;
        requirePositive(price);
        this.price = price;
        this.occupiedDates = dates; 
    }

    // Hentemetoder 
    public String getRoomNr() {
        return roomNr;
    }

    public int getBeds() {
        return beds;
    }
    
    public double getPrice() {
        return price;
    }

    public List<Pair<LocalDate, LocalDate>> getOccupiedDates() {
        return new ArrayList<>(occupiedDates);
    }

    // Sjekker om rommet er opptatt på ønsket datoer
    public boolean isOccupied(String startDate, int nights) {
        // Henter ut datoene 
        Pair<LocalDate, LocalDate> dates = convertDate(startDate, nights);
        LocalDate sDate = dates.getKey();
        LocalDate eDate = dates.getValue();

        // Dersom listen er tom, så er rommet ledig 
        if (occupiedDates.isEmpty()) {
            return false; 
        }

        // For-løkke som sjekker datoparene som er opptatt
        for (Pair<LocalDate,LocalDate> pair : occupiedDates) {
            LocalDate checkStart = pair.getKey(); 
            LocalDate checkEnd = pair.getValue(); 

            // Sjekker om startdato eller sluttdato er lik ønsket start/slutt
            if (checkStart.equals(sDate) || checkEnd.equals(eDate)) {
                return true; 
            }

            // Sjekker om start dato for ønsket booking er mellom to datoer som er opptatt
            if ((sDate.isBefore(checkEnd)) && sDate.isAfter(checkStart)) {
                return true; 
            }

            // Sjekker om sluttdato for ønsket booking er mellom to dager som er opptatt 
            if (eDate.isAfter(checkStart) && eDate.isBefore(checkEnd)) {
                return true; 
            }

            // Sjekker om allerede booket datoer er inne i intervallet til ønsket booking 
            if (checkStart.isAfter(sDate) && checkEnd.isBefore(eDate)) {
                return true; 
            }
        }
        return false;   // Returnerer false dersom ingen av datoene kolliderer, altså rommet er ledig 
    }
    
    // Sjekker om rommet er ledig og eventuelt legger dette til i opptatte rom.
    public void orderRoom(String startDate, int nights) {
        if (isOccupied(startDate, nights)) {    //Sjekker dette på hotell, nødvendig her? 
            throw new IllegalStateException("Rommet er ikke ledig."); 
        }
        occupiedDates.add(convertDate(startDate, nights)); 
    }

    // Gjør tilsvarende som orderRoom(), bare for avbestilling og gjør rommet ledig. 
    public void cancelOrder(String startDate) {
        for (Pair<LocalDate, LocalDate> pair : occupiedDates) {
            if (pair.getKey().equals(LocalDate.parse(startDate))) {
                occupiedDates.remove(pair);  
                return; 
            }    
        }
        throw new IllegalStateException(this.roomNr + " er ikke booket på datoen " + startDate);  
    }
    
    // Hjelpemetoder 
    // Sjekker at romnummer bare er tall 
    private void checkRoomNr(String roomNr) {   
        for(char i : roomNr.toCharArray()) {
            if (!Character.isDigit(i)) {
                throw new IllegalArgumentException("Romnummer må inneholde kun tall");
            }
        }
    }

    // Sjekker at heltallet er positiv 
    private void requirePositive(int n) { 
        try {
            Validate.positiveInt(n);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Antall senger kan ikke være negativt");
        }
    }


    // Sjekker at double er positiv 
    private void requirePositive(double n) {   
        try {
            Validate.positiveDouble(n);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Pris kan ikke være negativ");
        } 
    }

    // Konverterer fra string til Date 
    private Pair<LocalDate, LocalDate> convertDate(String startDate, int nights) {
        // Bruker innebygde funksjoner i LocalDate til å gjøre omgjøringen 
        try {
            LocalDate sDate = LocalDate.parse(startDate); 
            LocalDate eDate = sDate.plusDays(nights);
            return new Pair<LocalDate, LocalDate>(sDate, eDate); 
        } catch (Exception e) { // Utløser unntak dersom formatet til startDate er feil 
            throw new IllegalArgumentException("Feil format på dato"); 
        }
    }

    // Ulike toString-metoder til bruk i gridPane i app
    @Override
    public String toString() {
        return this.getRoomNr();
    }

    public String toStringOrder() {
        return "romNr: "+ roomNr + "\n" + "Pris: " + price + ",-" + "\n" + "Antall senger: " + beds;
    }

    public String toStringCancel() {
        return "romNr: " + roomNr + "\n" + "Pris: " + price + ",-";
    }

    @Override
    public boolean equals(Object object) {
        // Sjekker at objektet er av typen rom 
        if (object instanceof Room) {
            Room room = (Room) object;  // Caster til rom-klassen 

            if (this.getOccupiedDates().size() != room.getOccupiedDates().size()) return false;     // Sjekker at listene er like lange 

            // Sjekker at antall senger, pris og romnummer er like 
            if (!((this.getBeds() == room.getBeds()) && (this.getPrice() == room.getPrice()) && (this.getRoomNr().equals(room.getRoomNr())))) {
                return false; 
            }

            // Sjekker at datoene i listen er like
            for (int i = 0; i < this.getOccupiedDates().size() ; i++) {
                if (!this.getOccupiedDates().get(i).getKey().equals(room.getOccupiedDates().get(i).getKey())) return false;  
                if (!this.getOccupiedDates().get(i).getValue().equals(room.getOccupiedDates().get(i).getValue())) return false; 
            }

            return true;    // Rommene er like dersom ingen av testene over slår ut  
        }
        return false;   // Dersom objektet ikke er av typen rom, så er ikke rommene like 
    }

    /**
     * @startuml 
     * class Room {
     *  - roomNr
     *  - beds
     *  - price 
     *  - occupiedDates
     * 
     *  + getRoomNr() 
     *  + getBeds() 
     *  + getPrice() 
     *  + getOccupiedDates() 
     *  + isOccupied(String, int) 
     *  + orderRoom(String, int) 
     *  + cancelOrder(String) 
     *  + toString() 
     *  + toStringOrder() 
     *  + toStringCancel() 
     *  + equals(Object)
     *  - checkRoomNr(String) 
     *  - requirePositive(int)
     *  - requirePositive(double)
     *  - convertDate(String, int) 
     * }
     * @enduml
     */


}
