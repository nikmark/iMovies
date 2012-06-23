package utils;

/**
 * Classe di gestione del certificato dell'utente
 *
 * @author Alessandro Gottoli
 * @author Nicolò Marchi
 * @author Mattia Peretti
 * @version 1.0
 */
public class UserCert {

    private String nameFile;
    private String serial;
    private String ver;
    private String startD;
    private String dateE;
    private String dateR;
    private String passwordPkcs12;
    private String passwordKey;
    private String user;

    /**
     * Inizializza a valori di default le variabili da utilizzare
     */
    public UserCert() {
        this.ver = this.dateE = null;
        this.dateR = null;
        this.passwordKey = this.passwordPkcs12 = "";
        this.dateR = "";
    }

    /**
     * Restituisce la data di scadenza del certificato
     *
     * @return una stringa che rappresenta la data di scadenza
     */
    public String getDateE() {
        return dateE;
    }

    /**
     * Imposta la data di scadenza del certificato
     *
     * @param dateE una stringa che rappresenta la data di scadenza
     */
    public void setDateE(String dateE) {
        this.dateE = dateE;
    }

    /**
     * Restituisce la data di revoca del certificato
     *
     * @return una stringa che rappresenta la data di revoca
     */
    public String getDateR() {
        return dateR;
    }

    /**
     * Imposta la data di revoca del certificato
     *
     * @param dateR una stringa che rappresenta la data di revoca
     */
    public void setDateR(String dateR) {
        this.dateR = dateR;
    }

    /**
     * Metodo che restituisce l'attributo di validità, espirazione, revocazione
     * contentuto nel file index.txt
     *
     * @return l'attributo V o E o R
     */
    public String getVer() {
        return ver;
    }

    /**
     * Metodo che assegna l'attributo di validità, espirazione, revocazione
     * contentuto nel file index.txt all'oggetto UserCert
     *
     * @param l'attributo V o E o R
     */
    public void setVer(String ver) {
        this.ver = ver;
    }

    /**
     * Restituisce il nome del file
     *
     * @return una stringa che rappresenta il nome del file
     */
    public String getNameFile() {
        return nameFile;
    }

    /**
     * Imposta il nome del file
     *
     * @param nameFile una stringa che rappresenta il nome del file
     */
    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    /**
     * Restituisce il seriale assegnato al certificato
     *
     * @return una stringa che rappresenta il seriale
     */
    public String getSerial() {
        return serial;
    }

    /**
     * Imposta il seriale da assegnare al certificato
     *
     * @param serial una stringa che rappresenta il seriale
     */
    public void setSerial(String serial) {
        this.serial = serial;
    }

    /**
     * Restituisce la password del file pkcs12
     *
     * @return una stringa che rappresenta la password
     */
    public String getPasswordPkcs12() {
        return passwordPkcs12;
    }

    /**
     * Imposta la password del pkcs12
     *
     * @param passwordPkcs12 una stringa che rappresenta la password
     */
    public void setPasswordPkcs12(String passwordPkcs12) {
        this.passwordPkcs12 = passwordPkcs12;
    }

    /**
     * Restituisce la password della chiave privata dell'utente contenuta nel
     * pkcs12
     *
     * @return una stringa che rappresenta la password
     */
    public String getPasswordKey() {
        return passwordKey;
    }

    /**
     * Imposta la password della chiave privata dell'utente contenuta nel pkcs12
     *
     * @param passwordKey una stringa che rappresenta la password
     */
    public void setPasswordKey(String passwordKey) {
        this.passwordKey = passwordKey;
    }

    /**
     * Ritorna lo username dell'utente
     *
     * @return lo username
     */
    public String getUser() {
        return user;
    }

    /**
     * Assegna lo username dell'utente
     *
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Ritorna la data di inizio di validità del certificato
     *
     * @return una stringa rappresentante la data a partire dalla quale il
     * certificato è valido
     */
    public String getStartD() {
        return startD;
    }

    /**
     * Imposta la data di inizio di validità del certificato
     *
     * @param startD una stringa rappresentante la data a partire dalla quale il
     * certificato è valido
     */
    public void setStartD(String startD) {
        this.startD = startD;
    }
}
