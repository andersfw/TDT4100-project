package hotel;

import java.io.FileNotFoundException;

public interface ISaveHotelHandler {
    
    void save(Hotel hotel, String filename) throws FileNotFoundException;
    Hotel load(String filename) throws FileNotFoundException; 


}
