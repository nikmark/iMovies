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
    
    private String validity;
   
    public UserCert(){
        
    }
    public UserCert(String nameFile, String serial, String validity){
        this.nameFile=nameFile;
        this.serial=serial;
        this.validity=validity;
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

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }
    

}
