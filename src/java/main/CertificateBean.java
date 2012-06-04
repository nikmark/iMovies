/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.Serializable;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.swing.text.Utilities;
import utils.UserCert;

/**
 *
 * @author nicolo
 */
public class CertificateBean implements Serializable{
    
    private ArrayList<UserCert> uCert=new ArrayList<UserCert>();

    private UserCert selectedUserCert;

    
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
    
    public boolean revokeCertificate(){
        System.out.println("Revoke Certificate di CertificateBean. Nome file da revocare= "+getSelectedUserCert().getNameFile());
        
        setSelectedUserCert(utils.Utilities.revokeCertificate(getSelectedUserCert()));
        return true;
    }
    
    public boolean deleteCertificate(){
         setSelectedUserCert(utils.Utilities.revokeCertificate(getSelectedUserCert()));
         utils.Utilities.deleteCertificate(getSelectedUserCert());
         return true;

    }
}
