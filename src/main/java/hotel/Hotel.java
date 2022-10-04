package hotel;

import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;

public class Hotel { 

    private List<Room> availableRooms = new ArrayList<>();  // Liste med rommene til hotellet  
    private List<Member> members = new ArrayList<>();       // Liste med medlemmer tilhørende hotellet 
    private Member member;                                  // Medlemmet som er logget inn, ellers null
    private Room chosenRoom;                                // Valgt rom
    private String hotelName;                               // Hotelnavn til bruk i filnavnet 


    // Konstruktør for testing
    public Hotel() {
        this.hotelName = "HotelBooking AS"; 
        this.availableRooms.add(new Room("113", 5, 500));
        this.availableRooms.add(new Room("163", 5, 500));
        this.availableRooms.add(new Room("124", 3, 500));
        this.availableRooms.add(new Room("273", 4, 200));
        this.availableRooms.add(new Room("263", 3, 800));
        this.availableRooms.add(new Room("533", 4, 600));
        this.availableRooms.add(new Room("413", 3, 300));
        this.availableRooms.add(new Room("843", 6, 900));
        this.availableRooms.add(new Room("122", 4, 700));
        this.availableRooms.add(new Room("128", 3, 300));
        this.availableRooms.add(new Room("456", 2, 500));
        this.availableRooms.add(new Room("693", 2, 500));
        this.availableRooms.add(new Room("323", 3, 700));
        this.availableRooms.add(new Room("210", 4, 800));
        this.availableRooms.add(new Room("211", 4, 800));
        this.availableRooms.add(new Room("223", 4, 500));
    }

    // Konstruktør som bruker data fra fil 
    public Hotel(String hotelname, Member member, Room room, List<Member> members, List<Room> rooms) {
        this.hotelName = hotelname; 
        this.member = member; 
        this.chosenRoom = room; 
        this.members = members; 
        this.availableRooms = rooms; 
    }
    
    
    // Metoder 
    public String getName() {
        return this.hotelName; 
    }

    public void chooseRoom(Room room) {
        this.chosenRoom = room; 
    }
    
    // Metode som sjekker om et medlem er logget inn 
    public boolean isMember() {
        return member != null;
    }

    public Member getMember() {
        return this.member; 
    }

    public Room getChosenRoom() {
        return this.chosenRoom; 
    }

    // Returnere alle medlemmene som hører til hotellet 
    public List<Member> getMembers() {
        return new ArrayList<>(members);
    }

    // Returnerer alle rommene som hører til hotellet 
    public List<Room> getRooms() {
        return new ArrayList<>(availableRooms);
    }
        
    // Henter ut en ny liste med ledige rom i henhold til søksparameterne 
    public List<Room> getRooms(String beds, String price, String startDate, String nights) {

        int intBeds = parsingInt(beds); 
        int intNights = parsingInt(nights);
        double doublePrice = parsingDouble(price);

        // Bruker lambda streams for å filtrere ut ønskede rom 
        List<Room> possibleRooms = availableRooms.stream()
            .filter(r -> r.getPrice() <= doublePrice && r.getBeds() >= intBeds)
            .filter(r -> !r.isOccupied(startDate, intNights))
            .sorted(new SearchSort())   // Sorterer ved bruk av vår egen comparator
            .toList();
        if (possibleRooms.size() <= 15) {
            return possibleRooms;
        }
        return possibleRooms.subList(0, 15);   // Returnerer den nye listen med ledige rom ihh. søket 
    }

    // Delegering. Controller ber hotell om bestillingene, og hotel spør medlem dersom logget inn
    public List<Pair<Room, String>> getMemberOrders() {
        if (this.isMember()) return this.getMember().getOrdersAndDates();
        throw new IllegalStateException("Du er ikke logget inn.");
    }

    // Hjelpemetoder for omgjøring fra string til int/double
    private int parsingInt(String n) {
        int i = Validate.parseInt(n);
        Validate.positiveInt(i);
        return i; 
    }

    private double parsingDouble(String n) {
        double i = Validate.parseDouble(n);
        Validate.positiveDouble(i);
        return i;
    }

    // Utfører bestilling av rom
    public void orderRoom(String startDate, String nights) {
        if (this.getChosenRoom() == null) {
            throw new IllegalStateException("Er ikke valgt noen rom.");
        }
        if (!this.isMember()) {
            throw new IllegalStateException("Du er ikke logget inn!");
        }
        int nightsInt = parsingInt(nights);
        
        if (!getChosenRoom().isOccupied(startDate, nightsInt)) {
            this.getMember().orderRoom(getChosenRoom(), startDate, nightsInt);
        }
        else {
            throw new IllegalStateException("Rommet er opptatt."); 
        }
    }

    public void cancelOrder() {
        if (!isMember()) {
            throw new IllegalStateException("Du er ikke logget inn."); 
        }

        this.member.cancelOrder(getChosenRoom()); 
    }

    // Utregning av pris, basert på rommet sin pris, antall netter og om du er medlem 
    public double calculatePrice(Room room, String nights) {
        int nightsInt = parsingInt(nights);
        double totalPrice = 0; 
        double memberDiscount = 1; 
        
        if (isMember()) {
            memberDiscount = 0.8; 
        }

        totalPrice = room.getPrice() * nightsInt * memberDiscount;
        return totalPrice;        
    }

    // Metode for å logge inn med nytt medlem 
    public void newMember(String phoneNumber, String password) { 
        for (Member member : members) {
            // Sjekker at ingen allerede har brukt samme telefonnummer 
            if (member.getphoneNumber().equals(phoneNumber)) {
                throw new IllegalStateException("En bruker med det telefonnummeret eksisterer allerede"); 
            }
        }
        members.add(new Member(phoneNumber, password));
    }

    // Login metode som sjekker alle medlemmene til hotellet og prøver logge inn, dersom telefonnummeret er likt 
    public boolean login(String phoneNumber, String password) {
        for (Member member : members) {
            // Sjekker om telefonnummeret er likt
            if (member.getphoneNumber().equals(phoneNumber)) {
                // Prøver logge inn om telefonnummeret er likt
                if (member.login(phoneNumber, password)) {
                    this.member = member; 
                    return true;    // Returnerer true, og setter medlem til dette objektet, dersom passord stemmer. 
                }
            }
        }
        return false; 
    }

    // Metode som logger deg ut 
    public String logout() {
        this.member = null;                             // Når this.member = null, så er ingen logget inn 
        return "Logget ut.\nLogg inn på nytt";          // Returnerer streng til GUI 
    }

    @Override
    public boolean equals(Object object) {
        // Sjekker at hotel er av riktig type objekt 
        if (object instanceof Hotel) {
            Hotel hotel = (Hotel) object;   // Caster til hotel-klassen

            if (this.getMembers().size() != hotel.getMembers().size()) return false;    // Sjekker størrelsen på listene
            if (this.getRooms().size() != hotel.getRooms().size()) return false;        // Sjekker størrelsen på listene
            if (!this.getName().equals(hotel.getName())) return false;                  // Sjekker at hotelnavnet er likt 

            // Sjekker at ikke valgt rom/innlogget medlem er null på et av hotellene
            if ((this.getChosenRoom() == null) && (hotel.getChosenRoom() != null) || ((this.getChosenRoom() != null) && (hotel.getChosenRoom() == null))) return false; 
            if ((this.getMember() == null) && (hotel.getMember() != null) || ((this.getChosenRoom() != null) && (hotel.getChosenRoom() == null))) return false; 

            // Sjekker at innlogget medlem ikke er null, for å så sjekke om de er like 
            if ((this.getMember() != null) && (hotel.getMember() != null)) {
                if (!(this.getMember().equals(hotel.getMember()))) return false; 
            }
            // Sjekker at valgt rom ikke er null, for å så sjekke om de er like 
            if ((this.getChosenRoom() != null) && (hotel.getChosenRoom() != null)) {
                if (!(this.getChosenRoom().equals(hotel.getChosenRoom()))) return false; 
            }

            // For-løkke som sjekker at alle rommene i listen er like på samme plass
            for (int i = 0; i < this.getMembers().size() ; i++) {
                if (!this.getMembers().get(i).equals(hotel.getMembers().get(i))) return false; 
            }
            
            // For-løkke som sjekker at alle medlemmene i listen er like på samme plass
            for (int i = 0; i < this.getMembers().size() ; i++) {
                if (!this.getRooms().get(i).equals(hotel.getRooms().get(i))) return false; 
            }

            return true;    // Hotellene er like dersom alle dataene over er like
        }
        return false; // Hotellene er ikke like, dersom det ene objektet ikke er av type hotell 
    }

	    /**
     * @startuml
     * class Hotel {
     *  - List<Room> availableRooms
     *  - List<Member> members 
     *  - Member member
     *  - Room chosenRoom
     *  - String hotelName
     * 
     *  + String getName() 
     *  + void chooseRoom(Room) 
     *  + boolean isMember() 
     *  + Member getMember() 
     *  + Room getChosenRoom() 
     *  + List<Member> getMembers() 
     *  + List<Room> getRooms() 
     *  + List<Room> getRooms(String, String, String, String)
     *  + List<Pair<Room, String>> getMemberOrders()
     *  + void orderRoom(String, String)
     *  + void cancelOrder() 
     *  + double calculatePrice(Room, String) 
     *  + void newMember(String, String)
     *  + boolean login(String, String)
     *  + String logout() 
     *  + boolean equals(Object)
     *  - int parsingInt(String)
     *  - double parsingDouble(String) 
     * }
     * 
     * class Room {
     *  - String roomNr
     *  - int beds
     *  - double price 
     *  - List<Pair<LocalDate, LocalDate>> occupiedDates
     * 
     *  + String getRoomNr() 
     *  + int getBeds() 
     *  + double getPrice() 
     *  + List<Pair<LocalDate, LocalDate>> getOccupiedDates() 
     *  + boolean isOccupied(String, int) 
     *  + void orderRoom(String, int) 
     *  + void cancelOrder(String) 
     *  + String toString() 
     *  + String toStringOrder() 
     *  + String toStringCancel() 
     *  + boolean equals(Object)
     *  - void checkRoomNr(String) 
     *  - void requirePositive(int)
     *  - void requirePositive(double)
     *  - Pair <LocalDate, LocalDate> convertDate(String, int) 
     * }
     * 
     * class Member {
     *  - String phoneNumber 
     *  - String password 
     *  - List<Pair<Room, String>> orders
     *  # String getPassword()
     *  + String getPhoneNumber() 
     *  + List<Pair<Room, String>> getOrdersAndDates() 
     *  + void cancelOrder(Room)
     *  + void orderRoom(Room, String, int) 
     *  + boolean login(String, String)
     *  + String toString() 
     *  + boolean equals(Object)
     *  - void validatePhoneNumber(String) 
     *  - void ValidatePassword(String)
     * }
     * 
     * interface ISaveHotelHandler {
     *  + void save(Hotel, String)
     *  + Hotel load(String)
     * }
     * 
     * class SaveHotelHandler implements ISaveHotelHandler{
     *  - Room getRoom(String, List<Room>)
     *  - Member getMember(String, List<Member>)
     *  + String getFilePath(String)
     * }
     * 
     * class SearchSort {
     *  + int compare(Room, Room)
     * }
     * 
     * class CancelSort {
     *  + int compare (Pair<Room, String>, Pair<Room, String>)
     * }
     * 
     * Hotel "1" -down- "*" Room : "rooms\t"
     * Hotel "1" -down- "1" Room : "chosenRoom\t"
     * Hotel "1" -down- "*" Member : "members\t"
     * Hotel "1" -down- "1" Member : "member\t"  
     * Member "1" -right- "*" Room :"\torders\t"
     * 
     * Hotel -left-> SearchSort
     * Hotel -> CancelSort
     * Hotel <- ISaveHotelHandler
     * 
     * @enduml
     */


}
