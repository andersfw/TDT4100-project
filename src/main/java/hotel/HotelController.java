package hotel;

import java.time.LocalDate;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.Pair;
import javafx.scene.control.PasswordField;

public class HotelController {

    // Felt
    @FXML private TextField bedsField, nightsField, maxPriceField, numberField;  // Importerer tekstfeltene 
    @FXML private PasswordField passwordField; // Importerer passordfeltene
    @FXML private Button loginButton, logoutButton, orderButton, cancelButton, searchButton, backToSearchButton, 
                        newMemberButton, cancelOrderModeButton, orderModeButton, saveButton, loadButton; // Importerer knappene
    @FXML private Label memberStatus, priceStatus, orderingStatus, searchStatus, nameLabel, saveStatus, 
                        newStatus, saveSuccess; // Importerer statusfeltene/tekstområdene 
    @FXML private GridPane possibleRooms; // Importerer gridpane 
    @FXML private DatePicker datePicker; // Importerer DatePicker 

    private Hotel hotel = new Hotel(); // Lager ny instans av hotel som skal brukes i appen
    private SaveHotelHandler shh = new SaveHotelHandler(); 

    // Kilde: http://www.java2s.com/Tutorials/Java/JavaFX/0540__JavaFX_DatePicker.htm 
    final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
        @Override
        public DateCell call(final DatePicker datePicker) {
          return new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
  
                // Sjekker om datoen er før dagen idag, hvis den er det skal fargen bli satt til grå og disable på den datoen 
                // Slik at du ikke kan velge tidligere datoer 
                if (item.isBefore(LocalDate.now()) || (item.isAfter(LocalDate.now().plusYears(1)))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #CCCCCC;");
                }
            }
            };
        }
    };

    // Metode som setter riktig status på knapper og labels når du åpner opp programmet/leser fra fil 
    @FXML private void initialize() {     
        datePicker.setValue(LocalDate.now());
        datePicker.setDayCellFactory(this.dayCellFactory);

        if (hotel.isMember()) {
            loggedInState();
        }
        else{
            loggedOutState();
        }

        if (hotel.getName() != null) {
            nameLabel.setText(hotel.getName());
        }
    }
    
    // Metode som utfører søket til brukeren 
    @FXML private void handleSearch() {
        // Henter ut input fra brukeren
        saveStatus.setText("");
        saveSuccess.setText("");
        String beds = this.bedsField.getText();
        String price = this.maxPriceField.getText();
        String nights = this.nightsField.getText();
        String date = this.datePicker.getValue().toString(); 
        
        try {
            // Henter ut alle mulige rommene som stemmer iforhold til søkkriterinene fra brukeren
            possibleRooms.getChildren().clear();
            List<Room> posSearchRooms = hotel.getRooms(beds, price, date, nights);
            
            // Sjekker at det finnes minst ett rom som stemmer med søket
            if (posSearchRooms.size() > 0) {
                // Oppdaterer statusfeltene 
                priceStatus.setText("Pris: ");
                orderingStatus.setText("Bestillingsstatus: Klikk på ønsket rom");
                orderButton.setVisible(false);
                cancelButton.setVisible(false);
        
                // Tømmer gridpane slik at nye rom kan bli lagt til 
                possibleRooms.getChildren().clear();
            
                // For-løkke som lager en ny knapp for hvert rom og legger disse til i gridpane
                for (int i = 0; i < posSearchRooms.size(); i++) {
                    Room room = posSearchRooms.get(i);
                    possibleRooms.add(createRoomButton(room), i%3, i/3);
                }
            }
            else {
                // Oppdaterer status dersom søket ikke gir resultat 
                orderingStatus.setText("Bestillingsstatus: Ingen rom stemmer med søket");
            }
            searchStatus.setText("");
            // Fjerner muligheten til å endre verdier når søket er blitt gjort, gjør det heller mulig å gå tilbake til søk og endre der
            bedsField.setDisable(true);
            nightsField.setDisable(true);
            datePicker.setDisable(true);
            maxPriceField.setDisable(true);
            searchButton.setVisible(false);
            backToSearchButton.setVisible(true);
        }
        catch (Exception e) {
            // Oppdaterer status dersom søk gir feilmelding
            searchStatus.setText(e.getMessage());
        }

    }

    // Metode som gir brukeren mulighet til å endre søket sitt
    @FXML private void backToSearch() {
        // Dersom brukeren vil gå tilbake til søk, gjøres feltene tilgjengelige igjen for brukeren 
        priceStatus.setText("");
        orderingStatus.setText("");
        searchStatus.setText("");
        bedsField.setDisable(false);
        bedsField.clear();
        nightsField.setDisable(false);
        nightsField.clear();
        datePicker.setDisable(false);
        maxPriceField.setDisable(false);
        maxPriceField.clear();
        searchButton.setVisible(true);
        backToSearchButton.setVisible(false);
        possibleRooms.getChildren().clear();
    }

    // Hjelpemetode som lager ny knapp for hvert rom som skal i gridpane i bestillingsmodus 
    private Button createRoomButton(Room room) {   // Lager en knapp som tar inn ett rom, og som sender deg videre til gridButtonPush dersom knappen trykkes
        Button button = new Button(room.toStringOrder());
        button.setOnAction((event) -> {gridButtonPush(room);});
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMaxHeight(Double.MAX_VALUE);
        return button;
    }

    // Metode som definerer hva som skal skje når man klikker på et rom i gridpane i bestillingsmodus
    @FXML private void gridButtonPush(Room room) { 
        try {
            // Oppdaterer prisstatus 
            priceStatus.setText("Pris: " + Double.toString(hotel.calculatePrice(room, nightsField.getText())));
            hotel.chooseRoom(room); 
             // Oppdaterer knapper 
            orderButton.setVisible(true);
            cancelButton.setVisible(false);
            // I tillegg legges det til romnummeret på det øvre feltet i brukergrensesnittet
            orderingStatus.setText("Bestillingsstatus: Valgt rom er " + room.getRoomNr());
        } catch (IllegalArgumentException iae) {
            orderingStatus.setText("Bestillinggstatus: kan ikke bestille \nrom" + room.getRoomNr() + "\n" + iae.getMessage());
        }
    }

    // Testkode for å få riktig dato 
    // Metode som tar appen inn i "avbestillingsmodus"
    @FXML private void handleCancelOrderMode() {
        saveStatus.setText("");
        saveSuccess.setText("");
        // Legge til listen med bestillinger fra brukeren 
        List<Pair<Room, String>> orders = hotel.getMemberOrders();
        possibleRooms.getChildren().clear();

        // Sjekker at det finnes bestillinger på brukeren 
        if (orders.size() != 0) {
            // For-løkke som lager en ny knapp for hvert rom og legger disse til i gridpane
            for (int i = 0; i < orders.size(); i++) {
                Pair<Room, String> orderPair = orders.get(i);
                Room room = orderPair.getKey();
                String date = orderPair.getValue();
                possibleRooms.add(createCancelRoomButton(room, date), i%3, i/3);
            }

            // Oppdaterer status 
            orderingStatus.setText("Bestillingsstatus: Avbestillingsmodus aktivert");
        } 
        else {
            // Dersom brukeren har null bestillinger oppdateres status i henhold til dette 
            orderingStatus.setText("Du har ingen bestillinger");
        }
        cancelOrderState();
    }
    
    // Metode som lager ny knapp for hvert som som kan avbestilles 
    private Button createCancelRoomButton(Room room, String date) {
        Button button = new Button(room.toStringCancel() + "\n" + date); 
        button.setOnAction((event) -> {cancelRoomButtonPush(room);});
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMaxHeight(Double.MAX_VALUE);
        return button; 
    }
    
    // Metode som sier hva som skal skje når et rom velges i "avbestillingsmodus"
    private void cancelRoomButtonPush(Room room) {
        try {
            hotel.chooseRoom(room);
            orderButton.setVisible(false);
            cancelButton.setVisible(true);
            orderingStatus.setText("Avbestillingsstatus: Valgt rom er " + room.getRoomNr());
        }
        catch (Exception e) {
            orderingStatus.setText("Avbestillingsstatus: Kan ikke avbestille\nRom " + room.getRoomNr() + "\n" + e.getMessage());
        }
    }

    // Metode for å logge inn, dersom innloggingen går bra oppdateres status og visibility til relevante knapper og labels 
    @FXML private void handleLogin() { 
        newStatus.setText("");
        if (hotel.login(numberField.getText(), passwordField.getText())) {  
            loggedInState();
            possibleRooms.getChildren().clear();
            priceStatus.setText("Pris: ");
            orderingStatus.setText("Bestillingsstatus: ");
        }
        // Dersom det ikke går å logge inn så kommuniseres dette til brukeren 
        else {
            memberStatus.setText("Feil brukernavn eller passord");
        }
    }

    // Metode som logger brukeren ut og oppdaterer relevante felter
    @FXML private void handleLogout() { 
        memberStatus.setText(hotel.logout());   
        loggedOutState();
        numberField.clear();
        passwordField.clear();
        nightsField.clear();
        bedsField.clear();
        maxPriceField.clear();   
    }

    // Metode for å bestille et rom 
    @FXML private void handleOrder() {
        // String startDate = this.dateField.getText();
        String startDate = this.datePicker.getValue().toString(); 
        String nights = this.nightsField.getText();
        try {
            hotel.orderRoom(startDate, nights);
            orderingStatus.setText("Bestillingsstatus: Rommet er bestilt");
            handleSearch();

            // Gir forskjellig feilmelding avhengig av hva som er feilen 
        } catch (NullPointerException npe) {
            orderingStatus.setText("Bestillingsstatus: Kunne ikke bestille rom,\ndu er ikke logget inn");
        } catch (IllegalStateException ise) {
            orderingStatus.setText("Bestillingsstatus: "+ise.getMessage());
        } catch (IllegalArgumentException iae) {
            orderingStatus.setText("Bestillingsstatus: " + iae.getMessage());
        }
    }

    // Metode som utfører avbestilling av et rom 
    @FXML private void handleCancelOrder() {
        try {
            hotel.cancelOrder();
            handleCancelOrderMode();
        } catch (Exception e) {
            orderingStatus.setText("Kunne ikke avbestille");
        }
    }

    // Setter appen over i "bestillingsmodus"
    @FXML private void handleOrderMode() {
        orderState();
        handleSearch();
    }

    // Metode for å lage nytt medlem og logge inn som dette medlemmet 
    @FXML private void handleNewMember() {
        try {
            hotel.newMember(this.numberField.getText(), this.passwordField.getText());
            handleLogin();
            this.memberStatus.setText("Logget inn som nytt medlem");
        // Dersom det ikke går å logge inn som ny bruker kommuniseres dette til brukeren 
        } catch (IllegalStateException ise) {
            newStatus.setText("");
            memberStatus.setText("Bruker eksisterer allerede");
        } catch (IllegalArgumentException iae) {
            newStatus.setText(iae.getMessage());
            memberStatus.setText("");
        } catch (Exception e) {
            newStatus.setText("Kunne ikke lage nytt medlem.\nSkriv inn telefonnummer og passord");
            memberStatus.setText("");
        }
    }

    // Lagrer tilstanden til hotellet ved bruk av pop-up 
    @FXML private void saveState() {
        saveSuccess.setText("");
        saveStatus.setText("");
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Lagring");
        dialog.setHeaderText("Skriv inn ønsket filnavn");
        dialog.setContentText("Filnavn");
        
        try {
            String filename = dialog.showAndWait().get();
            shh.save(hotel, filename);
            saveSuccess.setText("Lagret: " + filename + ".txt");
        } catch (IllegalArgumentException iae) {
            saveStatus.setText("Kunne ikke lagre. Feilmelding: " + iae.getMessage());
        } catch (RuntimeException rte ) {
            saveStatus.setText("Avbrutt");
        }
        catch (Exception e) {
            saveStatus.setText("Feil i fillagring.");
        }
    }
    
    // Hennter ut tilstanden til et lagret hotell ved bruk av pop-up 
    @FXML private void loadState() {
        saveSuccess.setText("");
        saveStatus.setText("");
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Lese fra fil");
        dialog.setHeaderText("Skriv inn navnet på filen");
        dialog.setContentText("Filnavn");
        String filename = ""; 
        
        try {
            filename = dialog.showAndWait().get();
            this.hotel = shh.load(filename);
            initialize(); 
            saveSuccess.setText("Åpnet: " + filename + ".txt");
        } catch (IllegalArgumentException iae) {
            saveStatus.setText("Kunne ikke åpne. Feilmelding: " + iae.getMessage());
        } catch (RuntimeException rte ) {
            saveStatus.setText("Avbrutt");
        }
        catch (Exception e) {
            saveStatus.setText("Feil i fillagring.");
        }
    }

    // Oppdaterer relevante knapper og felt når du logger inn 
    @FXML private void loggedInState() {
        logoutButton.setVisible(true);  
        loginButton.setVisible(false);
        cancelButton.setVisible(false);
        cancelOrderModeButton.setVisible(true);
        newMemberButton.setVisible(false);
        numberField.setDisable(true);
        passwordField.setDisable(true);
        memberStatus.setText("Logget inn");
        newStatus.setText("");
    }
    
    // Oppdaterer relevante knapper og felt når du logger ut 
    @FXML private void loggedOutState() {
        logoutButton.setVisible(false);
        loginButton.setVisible(true);
        cancelButton.setVisible(false);
        newMemberButton.setVisible(true);
        numberField.setDisable(false);
        passwordField.setDisable(false);
        memberStatus.setText("Vennligst logg inn");
        orderingStatus.setText("Bestillingsstatus: ");
        orderState();
        cancelOrderModeButton.setVisible(false);
        newMemberButton.setVisible(true);
        newStatus.setText("");
    }

    // Oppdaterer relevante knapper og felt når du vil avbestille rom 
    @FXML private void cancelOrderState() {
        datePicker.setDisable(true);
        nightsField.setDisable(true);
        bedsField.setDisable(true);
        maxPriceField.setDisable(true);
        cancelButton.setVisible(true);
        orderButton.setVisible(false);
        searchButton.setDisable(true);
        orderModeButton.setVisible(true);
        cancelOrderModeButton.setVisible(false);
    }

    // Oppdaterer relevante knapper og felt når du vil bestille rom 
    @FXML private void orderState() {
        cancelOrderModeButton.setVisible(true);
        orderModeButton.setVisible(false);
        cancelButton.setVisible(false);
        possibleRooms.getChildren().clear();
        datePicker.setDisable(false);
        nightsField.setDisable(false);
        bedsField.setDisable(false);
        maxPriceField.setDisable(false);
        searchButton.setDisable(false);
    }

}
