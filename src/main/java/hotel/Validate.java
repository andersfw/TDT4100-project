package hotel;

// Static klasse som gir oss muligheten til å bruke samme valideringsmetoder flere steder i prosjektet uten å skrive inn metodene flere ganger
// Validering klassen sine metoder blir testet i testene til Hotel, Room og Member ved bruk av delegering

public class Validate {
    
    public static void password(String password) {
        // Bruker regex uttrykk for å sjekke at passordet inneholder minst en stor bokstav, en liten bokstav og et tall. Sjekker og om lengden er minst 8
        // Selve regex uttrykket er hentet fra: 
        // https://stackoverflow.com/questions/40336374/how-do-i-check-if-a-java-string-contains-at-least-one-capital-letter-lowercase
        if ((password.length() < 8)) {
            throw new IllegalArgumentException("Ugydlig passord.\nMå minst ha lengde 8");
        }
        if (!(password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).+$"))) {         
            throw new IllegalArgumentException("Ugydlig passord. Må inneholde minst\net tall, en stor og en liten bokstav"); 
        }
    }

    public static void phoneNumber(String phoneNumber) {
        // Sjekker at telefonnummer starter på 4 eller 9 og har totalt 7 tall
        if (!phoneNumber.matches("^[4,9][0-9]{7}$")) {
            throw new IllegalArgumentException("Ugyldig telefonnummer\nMå starte på 4 eller 9"); 
        }
    }

    public static void positiveInt(int n) {
        // Sjekker at tallet er positivt 
        if (n < 0) {
            throw new IllegalArgumentException("Feil i innskrivning,\n Tallet kan ikke være negativt.");
        }
    }

    public static void positiveDouble(double n) {
        // Sjekker at double er positiv
        if (n < 0) {
            throw new IllegalArgumentException("Feil i innskrivning,\n Tallet kan ikke være negativt.");
        }
    }

    public static int parseInt(String n) {
        // Omgjør fra string til int
        int i = 0; 
        try {
            i = Integer.parseInt(n);
        } catch (Exception e) {throw new IllegalArgumentException("Feil i innskrivning:\n" + n + " er ikke et tall");}
        return i; 
    }

    public static double parseDouble(String n) {
        // Omgjøring fra string til double
        double i = 0;
        try {
            i = Double.parseDouble(n);
        } catch (Exception e) {throw new IllegalArgumentException("Feil i innskrivning:\n"+ n + " er ikke et tall");}
        return i; 
    }

}
