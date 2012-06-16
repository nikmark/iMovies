package main;

import java.io.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.swing.text.Utilities;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import utils.AcLog;
import utils.IMoviesLogger;
import utils.UserCert;

/**
 * Bean di gestione certificati
 *
 * @author Gottoli, Marchi, Peretti
 * @version 1.0
 */
public class CertificateBean implements Serializable {

    private List<UserCert> uCert = new ArrayList<UserCert>();
    private UserCert selectedUserCert;
    private StreamedContent file;
    private String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//pkcs12//");
    private IMoviesLogger log = new IMoviesLogger("main.CertificateBean");
    private int num_valid, num_revoked;

    /**
     * Inizializza il bean dei certificati eseguendo il metodo refreshBean
     */
    public CertificateBean() {
        refreshBean();
    }

    /**
     *
     * @return Un oggetto di tipo List che rappresenta gli UserCert
     */
    public List<UserCert> getUCert() {
        return uCert;
    }

    public UserCert getSelectedUserCert() {
        return selectedUserCert;
    }

    public void setSelectedUserCert(UserCert selectedUserCert) {
        this.selectedUserCert = selectedUserCert;
    }

    /**
     * Il metodo permette di revocare il certificato selezionato
     *
     * @return una stringa di outcome che rappresenta l'azione da svolgere
     */
    public String revokeCertificate() {
        System.out.println("Revoke Certificate di CertificateBean. Nome file da revocare= " + getSelectedUserCert().getNameFile());

        setSelectedUserCert(utils.Utilities.revokeCertificate(getSelectedUserCert()));
        return "refresh";
    }

//    public String maliEstremi(){
//        System.out.println("MALI di CertificateBean. Nome file da revocare= "+getSelectedUserCert().getNameFile());
//        
//        //setSelectedUserCert(utils.Utilities.revokeCertificate(getSelectedUserCert()));
//        return "refresh";
//    }
    /**
     * Il metodo permette di scaricare il certificato impostando lo stream del
     * file pkcs12 nella variabile globale file
     *
     * @throws IOException
     */
    public void downloadCertificate() throws IOException {
        //System.out.println("sono in downloadCertificate.");
        utils.Utilities.pkcs12Certificate(getSelectedUserCert());
        //System.out.println("dopo pkcs12!!");

        //File f = new File("/pkcs12/" + getSelectedUserCert().getNameFile().replace(".pem", ".p12"));

        //if (f.length() == 0) {
        //    this.file = null;
        //} else {
        InputStream stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/pkcs12/" + getSelectedUserCert().getNameFile().replace(".pem", ".p12"));
        //System.out.println("test file = " + stream.available() + " esiste? =" + stream.toString());
        //System.out.println("da scaricare= " + path + "/" + getSelectedUserCert().getNameFile().replace(".pem", ".p12"));
        this.file = new DefaultStreamedContent(stream, "application/x-pkcs12", getSelectedUserCert().getNameFile().replace(".pem", ".p12"));
        //}
    }

    /**
     * Elimina il certificato selezionando (prima dell'eliminazione esegue la
     * revoca)
     *
     * @return una stringa di outcome che rappresenta l'azione da svolgere
     */
    public String deleteCertificate() {
        setSelectedUserCert(utils.Utilities.revokeCertificate(getSelectedUserCert()));
        utils.Utilities.deleteCertificate(getSelectedUserCert());
        CertificateBean cb = (CertificateBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("CertificateBean");
        cb.getUCert().remove(getSelectedUserCert());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("CertificateBean", cb);
        return "refresh";

    }

    /**
     * Il metodo restituisce lo stream del file impostato
     *
     * @return un oggetto di tipo StreamedContent
     * @throws IOException
     */
    public StreamedContent getFile() throws IOException {
        System.out.println("adesso ritorno il file da getFile()");
        downloadCertificate();
        //if(file == null)
        //{
        log.info(true, "Stream available", "", ": " + file.getStream().available());

        if (file.getStream().available() == 0) {
            /**
             * La password è sbagliata (oppure si è verificato qualcos'altro di
             * strano)
             */
            file = null;
            log.err(false, "Wrong password", "The password is wrong hence no certificate will be downloaded", "Password errata");
        }
        //}
        return file;
    }

    /**
     * Il metodo imposta la variabile uCert che contiene i certificati
     * dell'utente attualmente connesso.<br /> Inoltre, imposta le variabili che
     * rappresentano il numero di certificati rilasciati e revocati.
     */
    public void refreshBean() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("LoginBean");
            System.out.println(((LoginBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("LoginBean")).getUsername());
            this.uCert = utils.Utilities.getCertificateUser(((LoginBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("LoginBean")).getUsername(),
                    ((LoginBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("LoginBean")).isAdmin());
        } catch (CertificateException ex) {
            Logger.getLogger(CertificateBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        /**
         * Controllo sul numero di certficati rilasciati(ma validi) e revocati
         */
        num_revoked = 0;
        num_valid = 0;
        for (int i = 0; i < this.uCert.size(); i++) {
            if (!this.uCert.get(i).getDateR().equals("Not Revoked")) {
                this.num_revoked++;
            } else {
                this.num_valid++;
            }
        }

    }

    /**
     * Restituisce il numero di certificati validi
     *
     * @return un intero che rappresenta il numero di certificati validi
     */
    public int getNum_valid() {
        return num_valid;
    }

    /**
     * Restituisce il numero di certificati revocati
     *
     * @return un intero che rappresenta il numero di certificati revocati
     */
    public int getNum_revoked() {
        return num_revoked;
    }

    /**
     * Restituisce il seriale corrente per la generazione di un certificato
     *
     * @return una stringa che rappresenta il seriale
     */
    public String getCurrent_sn() {
        String current_sn = "";

        FileReader stream = null;
        try {
            stream = new FileReader("/etc/ssl/CA_iMovies/serial");
        } catch (IOException ex) {
            Logger.getLogger(IMoviesLogger.class.getName()).log(Level.SEVERE, null, ex);
        }

        BufferedReader in = new BufferedReader(stream);
        String line;

        try {
            while ((line = in.readLine()) != null) {
                current_sn = line;
            }
        } catch (IOException e) {
            Logger.getLogger(IMoviesLogger.class.getName()).log(Level.SEVERE, null, e);
        }

        try {
            in.close();
            //stream.close();
        } catch (IOException e) {
            Logger.getLogger(IMoviesLogger.class.getName()).log(Level.SEVERE, null, e);
        }

        return current_sn;
    }
}
