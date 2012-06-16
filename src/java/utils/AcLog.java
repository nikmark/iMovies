package utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  Oggetto per gestire una riga del file di log degli accessi
 *  @author Gottoli, Marchi, Peretti
 *  @version 1.0
 */
public class AcLog {
    private Date data;
    private String testo;
    private String uid;

    /**
     * Inizializza i campi dell'oggett AcLog
     * @param data un oggetti di tipo date che rappresenta la data in cui un accesso è stato registrato
     * @param testo una stringa di testo con i dati di accesso
     * @param uid una stringa con lo userid dell'utente che ha effettuato l'accesso
     */
    public AcLog(Date data,String testo,String uid)
    {
        this.data = data;
        this.testo = testo;
        this.uid = uid;
    }
    
    /**
     * @return the data
     */
    public String getData() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(data);
    }

    /**
     * @param data the data to set
     */
    public void setData(Date data) {
        this.data = data;
    }

    /**
     * @return the testo
     */
    public String getTesto() {
        return testo;
    }

    /**
     * @param testo the testo to set
     */
    public void setTesto(String testo) {
        this.testo = testo;
    }

    /**
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }
    
    
}
