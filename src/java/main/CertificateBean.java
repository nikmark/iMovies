/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.InputStream;
import java.io.Serializable;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.swing.text.Utilities;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import utils.UserCert;

/**
 *
 * @author nicolo
 */
public class CertificateBean implements Serializable{
    
    private ArrayList<UserCert> uCert=new ArrayList<UserCert>();

    private UserCert selectedUserCert;
    
    private StreamedContent file;  
    
    private String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//pkcs12//");
    
    public CertificateBean(){
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("LoginBean");
            System.out.println(((LoginBean)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("LoginBean")).getUsername());
            this.uCert=utils.Utilities.getCertificateUser(((LoginBean)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("LoginBean")).getUsername());
        } catch (CertificateException ex) {
            Logger.getLogger(CertificateBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    
    public String revokeCertificate(){
        System.out.println("Revoke Certificate di CertificateBean. Nome file da revocare= "+getSelectedUserCert().getNameFile());
        
        setSelectedUserCert(utils.Utilities.revokeCertificate(getSelectedUserCert()));
        return "refresh";
    }
    
//    public String maliEstremi(){
//        System.out.println("MALI di CertificateBean. Nome file da revocare= "+getSelectedUserCert().getNameFile());
//        
//        //setSelectedUserCert(utils.Utilities.revokeCertificate(getSelectedUserCert()));
//        return "refresh";
//    }
    
    public void downloadCertificate(){
        System.out.println("sono in downloadCertificate.");
        utils.Utilities.pkcs12Certificate(getSelectedUserCert());
        
        InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(path+getSelectedUserCert().getSerial()+".p12");  
        System.out.println("da scaricare= "+"/pkcs12/"+getSelectedUserCert().getNameFile().replace(".pem",".p12"));
        this.file=new DefaultStreamedContent(stream);
    }
    
    public String deleteCertificate(){
         setSelectedUserCert(utils.Utilities.revokeCertificate(getSelectedUserCert()));
         utils.Utilities.deleteCertificate(getSelectedUserCert());
         return "refresh";

    }
    public StreamedContent getFile(){
        System.out.println("adesso ritorno il file da getFile()");
        downloadCertificate();  
        return file;  
    } 
}
