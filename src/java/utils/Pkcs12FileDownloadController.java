/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.InputStream;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import main.CertificateBean;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author nicolo
 */
public class Pkcs12FileDownloadController {
    
    private StreamedContent file;
    private UserCert ue;

    public UserCert getUe() {
        return ue;
    }

    public void setUe(UserCert ue) {
        this.ue = ue;
    }
    private final String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//pkcs12//");
      
    public Pkcs12FileDownloadController() {          
        
        CertificateBean ce=(CertificateBean)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("CertificateBean");
         System.out.println("sono in pkcs12downloadCertificate.");
         System.out.println("stampo ue se esiste= "+ue.getNameFile());

         
        utils.Utilities.pkcs12Certificate(ce.getSelectedUserCert());
        
        InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(path+ue.getSerial()+".p12");  
        System.out.println("da scaricare= "+"/pkcs12/"+ue.getNameFile().replace(".pem",".p12"));
        file=new DefaultStreamedContent(stream);
    }  
  
    public StreamedContent getFile() {  
        return file;  
    }


    
}
