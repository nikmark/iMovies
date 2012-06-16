/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author nicolo
 */
public class UserCert {
    
    private String nameFile;
    
    private String serial;
    
//    private String validity;
    
    private String ver;
    
    private String startD;
    
    private String dateE;
    
    private String dateR;
    
    private String passwordPkcs12;
    
    private String passwordKey;
    
    private String user;
    
   
    public UserCert(){
        this.ver = this.dateE = null;
        this.dateR = null;
        this.passwordKey = this.passwordPkcs12 = "";
    }
//    public UserCert(String nameFile, String serial, String validity){
//        this.nameFile=nameFile;
//        this.serial=serial;
//        this.validity=validity;
//        this.ver = this.dateE = null;
//        this.dateR = "Not Revoked";
//    }

    public String getDateE() {
        return dateE;
    }

    public void setDateE(String dateE) {
        this.dateE = dateE;
    }

    public String getDateR() {
        return dateR;
    }

    public void setDateR(String dateR) {
        this.dateR = dateR;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }
    

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

//    public String getValidity() {
//        return validity;
//    }
//
//    public void setValidity(String validity) {
//        this.validity = validity;
//    }
    
    public String getPasswordPkcs12() {
        return passwordPkcs12;
    }

    public void setPasswordPkcs12(String passwordPkcs12) {
        this.passwordPkcs12 = passwordPkcs12;
    }
    
        public String getPasswordKey() {
        return passwordKey;
    }

    public void setPasswordKey(String passwordKey) {
        this.passwordKey = passwordKey;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the startD
     */
    public String getStartD() {
        return startD;
    }

    /**
     * @param startD the startD to set
     */
    public void setStartD(String startD) {
        this.startD = startD;
    }
    

}
