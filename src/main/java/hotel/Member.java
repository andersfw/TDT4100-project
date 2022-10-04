package hotel; 

import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;
public class Member {

    // Felt
    private String password;                                // Lagrer passord
    private String phoneNumber;                             // Larger telefonnummer som brukernavn 
    // Liste med bestilte rom, med tilhørende dato og netter, som hører til member
    private List<Pair<Room, String>> orders = new ArrayList<>();  

    // Konstruktør
    public Member(String phoneNumber, String password) {
        validatePhoneNumber(phoneNumber);
        validatePassword(password);
        this.phoneNumber = phoneNumber; 
        this.password = password; 
    }
    

    // Konstruktør for lesing av fil 
    public Member(String phoneNumber, String password, List<Pair<Room, String>> orders) {
        validatePhoneNumber(phoneNumber);
        validatePassword(password);
        this.phoneNumber = phoneNumber; 
        this.password = password; 
        this.orders = orders; 
    }

    // Metoder
    // Metode for å hente telefonnummer 
    public String getphoneNumber() {
        return phoneNumber;
    }

    protected String getPassword() {
        return password;
    }

    // Metode for å hente ut rommene med tilhørende dato som er bestilt av en bruker
    public List<Pair<Room, String>> getOrdersAndDates() {
        List<Pair<Room, String>> tmp = new ArrayList<>(orders); 
        tmp.sort(new CancelSort());
        if (tmp.size() < 15) return tmp; 
        return tmp.subList(0, 15);
    }

    // Metode for å avbestille rom 
    public void cancelOrder(Room room) {
        for (Pair<Room, String> pair : orders) {
            if (room.equals(pair.getKey())) {
                room.cancelOrder(pair.getValue());  // Siden ingen rom har samme startdato, så vil denne alltid avbestille rett rom 
                this.orders.remove(pair);
                return; 
            }
        }
        throw new IllegalStateException("Kan ikke avbestille et rom du ikke har bestillt.");     
    }

    // Legger til rommet i bestillte rom 
    public void orderRoom(Room room, String startDate, int nights) {  
        // Legger til rommet, dato for overnatting og antall netter i samme liste som pair, siden dataene hører til hverandre 
        room.orderRoom(startDate, nights);
        orders.add(new Pair<Room, String>(room, startDate));
    }

    // Sjekker at telefonnummer og passord er riktig for dette objektet
    public boolean login(String phoneNumber, String password) {
        return (this.phoneNumber.equals(phoneNumber) && this.password.equals(password));
    }
    
    // Hjelpemetoder
    // Sjekker at telefonnummer er på gydlig format 
    private void validatePhoneNumber(String phoneNumber) {
        Validate.phoneNumber(phoneNumber);
    }

    // Sjekker at passordet er på gydlig format 
    private void validatePassword(String password) {
        Validate.password(password);
    }

    @Override
    public String toString() {
        return this.getphoneNumber();
    }

    @Override
    public boolean equals(Object object) {
        // Sjekker at medlemmet er av riktig type objekt 
        if (object instanceof Member) {
            Member member = (Member) object;    // Caster til medlems-klassen 

            if (this.getOrdersAndDates().size() != member.getOrdersAndDates().size()) return false;     // Sjekker at lengden på listene er lik 

            // Sjekker at telefonnummer og passord er likt
            if (!((this.getphoneNumber().equals(member.getphoneNumber())) && (this.getPassword().equals(member.getPassword())))) {
                return false; 
            }

            // Sjekker at dato og rom-objektene i listene er like 
            for (int i = 0; i < this.getOrdersAndDates().size() ; i++) {
                if (!(this.getOrdersAndDates().get(i).getKey().equals(member.getOrdersAndDates().get(i).getKey()))) return false; 
                if (!(this.getOrdersAndDates().get(i).getValue().equals(member.getOrdersAndDates().get(i).getValue()))) return false; 
            }

            return true;    // Medlemmene er like dersom ingen av sjekkene over stemmer 
        }
        return false;   // Medlemmene er ikke like, dersom objektet man sammenligner med ikke er et medlemsobjekt 
    }

    /**
     * @startuml 
     * class Member {
     *  - phoneNumber 
     *  - password 
     *  - orders
     *  + getPhoneNumber() 
     *  # getPassword()
     *  + getOrdersAndDates() 
     *  + cancelOrder(Room)
     *  + orderRoom(Room, String, int) 
     *  + login(String, String)
     *  + toString() 
     *  - validatePhoneNumber(String) 
     *  - ValidatePassword(String)
     *  + equals(Object)
     * }
     * @enduml
     */ 


}
