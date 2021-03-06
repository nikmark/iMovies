package utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Oggetto per gestire una riga del file di log degli accessi
 *
 * @author Alessandro Gottoli
 * @author Nicolò Marchi
 * @author Mattia Peretti
 * @version 1.0
 */
public class AcLog {

    private Date data;
    private String testo;
    private String uid;

    /**
     * Inizializza i campi dell'oggett AcLog
     *
     * @param data un oggetti di tipo date che rappresenta la data in cui un
     * accesso è stato registrato
     * @param data un oggetti di tipo date che rappresenta la data in cui un
     * accesso è stato registrato
     * @param testo una stringa di testo con i dati di accesso
     * @param uid una stringa con lo userid dell'utente che ha effettuato
     * l'accesso
     * @param uid una stringa con lo userid dell'utente che ha effettuato
     * l'accesso
     */
    public AcLog(Date data, String testo, String uid) {
        this.data = data;
        this.testo = testo;
        this.uid = uid;
    }

    /**
     * Metodo che restituisce la data formattata
     *
     * @return la data formattata
     *
     */
    public String getData() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(data);
    }

    /**
     *
     * Metodo che setta la data passata come parametro.
     *
     * @param data la data da settare
     *
     */
    public void setData(Date data) {
        this.data = data;
    }

    /**
     *
     * Metodo che restituisce una stringa di testo con i dati di accesso
     *
     * @return il testo contenente i dati
     *
     */
    public String getTesto() {
        return testo;
    }

    /**
     * Metodo che setta una stringa di testo con i dati di accesso
     *
     * @param testo il testo da settare
     *
     */
    public void setTesto(String testo) {
        this.testo = testo;
    }

    /**
     * Metodo che restituisce una stringa con lo userid dell'utente che ha
     * effettuato l'accesso
     *
     * @return lo userid dell'utente
     *
     */
    public String getUid() {
        return uid;
    }

    /**
     * Metodo che setta una stringa con lo userid dell'utente che ha effettuato
     * l'accesso
     *
     * @param uid lo userid da settare
     *
     */
    public void setUid(String uid) {
        this.uid = uid;
    }
}
