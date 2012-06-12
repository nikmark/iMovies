/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.swing.text.Utilities;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import utils.IMoviesLogger;
import utils.UserCert;

/**
 *
- * @author nicolo
 */
public class CertificateBean implements Serializable {

    private ArrayList<UserCert> uCert = new ArrayList<UserCert>();
    private UserCert selectedUserCert;
    private StreamedContent file;
    private String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//pkcs12//");
    private IMoviesLogger log = new IMoviesLogger("main.CertificateBean");

    public CertificateBean() {
        refreshBean();
    }

    public ArrayList<UserCert> getuCert() {
        return uCert;
    }

    public UserCert getSelectedUserCert() {
        return selectedUserCert;
    }

    public void setSelectedUserCert(UserCert selectedUserCert) {
        this.selectedUserCert = selectedUserCert;
    }

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
    public void downloadCertificate() throws IOException {
        System.out.println("sono in downloadCertificate.");
        utils.Utilities.pkcs12Certificate(getSelectedUserCert());
        System.out.println("dopo pkcs12!!");

        //File f = new File("/pkcs12/" + getSelectedUserCert().getNameFile().replace(".pem", ".p12"));
        
        //if (f.length() == 0) {
        //    this.file = null;
        //} else {
            InputStream stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/pkcs12/" + getSelectedUserCert().getNameFile().replace(".pem", ".p12"));
            System.out.println("test file = " + stream.available() + " esiste? =" + stream.toString());
            System.out.println("da scaricare= " + path + "/" + getSelectedUserCert().getNameFile().replace(".pem", ".p12"));
            this.file = new DefaultStreamedContent(stream, "application/x-pkcs12", getSelectedUserCert().getNameFile().replace(".pem", ".p12"));
        //}
    }

    public String deleteCertificate() {
        setSelectedUserCert(utils.Utilities.revokeCertificate(getSelectedUserCert()));
        utils.Utilities.deleteCertificate(getSelectedUserCert());
        CertificateBean cb = (CertificateBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("CertificateBean");
        cb.getuCert().remove(getSelectedUserCert());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("CertificateBean", cb);
        return "refresh";

    }

    public StreamedContent getFile() throws IOException {
        System.out.println("adesso ritorno il file da getFile()");
        downloadCertificate();
        //if(file == null)
        //{
        log.info(true, "Stream available", "", ": "+file.getStream().available());
        
        if(file.getStream().available() == 0)
        {
            /**
             * La password è sbagliata (oppure si è verificato qualcos'altro di strano)
             */
            file = null;
            log.err(false, "Wrong password", "The password is wrong hence no certificate will be downloaded", "Password errata");
        }
        //}
        return file;
    }

    public void refreshBean() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("LoginBean");
            System.out.println(((LoginBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("LoginBean")).getUsername());
            this.uCert = utils.Utilities.getCertificateUser(((LoginBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("LoginBean")).getUsername());
        } catch (CertificateException ex) {
            Logger.getLogger(CertificateBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
