package hotel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.util.Pair;

public class SaveHotelHandler implements ISaveHotelHandler {
    
    @Override
    public void save(Hotel hotel, String filename) throws FileNotFoundException {
        if (filename.equals("")) throw new IllegalArgumentException("Kan ikke lagre tomt filnavn");
        if (filename.toLowerCase().contains("wrong")) throw new IllegalArgumentException("Filnavnet kan ikke inneholdet 'wrong' pga testing");

        try (PrintWriter writer = new PrintWriter(new File(getFilePath(filename)))) {
            for (Room room : hotel.getRooms()) {
                writer.println(room.getRoomNr());
                writer.println(room.getPrice());
                writer.println(room.getBeds());
                writer.println(room.getOccupiedDates());
            }

            writer.println("Members;");
            for (Member member : hotel.getMembers()) {
                writer.println(member.getphoneNumber());
                writer.println(member.getPassword());
                writer.println(member.getOrdersAndDates());
            }

            writer.println("Hotel;");
            writer.println(hotel.getName());
            writer.println(hotel.getMember());
            writer.println(hotel.getChosenRoom());
            
            writer.flush();
            writer.close();
        }        
    }

    @Override
    public Hotel load(String filename) throws FileNotFoundException {
        if (filename.equals("")) throw new IllegalArgumentException("Kan ikke laste tomt filnavn");

        try (Scanner scanner = new Scanner(new File(getFilePath(filename)))) {
            String line; 
            
            List<Room> rooms = new ArrayList<>(); 
            List<Member> members = new ArrayList<>();

            line = scanner.nextLine(); 

            while (!line.equals("Members;")) {
                List<Pair<LocalDate, LocalDate>> dates = new ArrayList<>(); 
                String roomNr = line; 
                double price = Double.parseDouble(scanner.nextLine());
                int beds = Integer.parseInt(scanner.nextLine());
                String tmp = scanner.nextLine();
                tmp = tmp.substring(1, tmp.length()-1);
                
                if (!tmp.equals(""))  {
                    String[] tmpList = tmp.split(",");
                    for (String string : tmpList) {
                        String[] stringSplit = string.strip().split("=");
                        if (stringSplit.length != 2) throw new IllegalArgumentException("Feil format på liste i fil");
                        dates.add(new Pair<LocalDate,LocalDate>(LocalDate.parse(stringSplit[0]), LocalDate.parse(stringSplit[1])));
                    }
                }
                
                rooms.add(new Room(roomNr, beds, price, dates));
                
                line = scanner.nextLine(); 
            }
            
            line = scanner.nextLine(); 

            while (!line.equals("Hotel;")) {
                List<Pair<Room, String>> orders = new ArrayList<>(); 

                String phoneNumber = line; 
                String password = scanner.nextLine(); 
                String tmp = scanner.nextLine();
                tmp = tmp.substring(1, tmp.length()-1);

                if (!tmp.equals("")) {
                    String[] tmpList = tmp.split(",");
                    for (String string : tmpList) {
                        String[] stringSplit = string.strip().split("=");
                        // Sjekker at pair er på riktig format 
                        if (stringSplit.length != 2) throw new IllegalArgumentException("Feil format på dato i fil");

                        // Sjekker om datoen er på rett format, siden den lagres som String
                        try {LocalDate.parse(stringSplit[1]);}
                        catch (Exception e) {throw new IllegalArgumentException("Feil format på dato i fil");}

                        orders.add(new Pair<Room,String>(getRoom(stringSplit[0], rooms), stringSplit[1]));
                    }
                }

                members.add(new Member(phoneNumber, password, orders));
                
                line = scanner.nextLine(); 
            }

            String hotelName = scanner.nextLine(); 
            String chosenMember = scanner.nextLine(); 
            String chosenRoom = scanner.nextLine(); 

            Hotel hotel = new Hotel(hotelName, getMember(chosenMember, members), getRoom(chosenRoom, rooms), members, rooms);
            
            scanner.close();

            return hotel;
        }
        catch (DateTimeParseException dt) {
            throw new IllegalArgumentException("Feil dato i fil");
        }
    }

    

    private Room getRoom(String string, List<Room> rooms) {
        if (string.equals("null")) {
            return null; 
        }
        
        for (Room room : rooms) {
            if (room.getRoomNr().equals(string)) {
                return room; 
            }
        }
        throw new IllegalArgumentException("Feil i fil"); 
    }

    private Member getMember(String string, List<Member> members) {
        if (string.equals("null")) {
            return null; 
        }

        for (Member member : members) {
            if (member.getphoneNumber().equals(string)) {
                return member; 
            }
        }
        throw new IllegalArgumentException("Feil i fil"); 
    }

    public static String getFilePath(String filename) {
        if (filename.contains("wrong")) {
            return SaveHotelHandler.class.getResource("test_files/").getFile() + filename + ".txt";
        }
		return SaveHotelHandler.class.getResource("hoteldatabase/").getFile() + filename + ".txt";
	}
}