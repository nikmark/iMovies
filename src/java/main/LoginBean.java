package main;


import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author mattia
 */
public class LoginBean {

    private String username;
    private String password;
    
    
    /**
     * Creates a new instance of loginBean
     */
    public LoginBean() {
        
    }
    
    /* public String login()
    {
        return "Ciao, sono il bean di login";
    }*/

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * 
     */
    public String login() throws NoSuchAlgorithmException, IOException {
        
        RequestContext context = RequestContext.getCurrentInstance();  
        FacesMessage msg;  
        boolean loggedIn;  
        
        /* if (username == null || password == null){
            // prima apertura allora non fare niente
           loggedIn = false;  
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Username e Password vuoti", "Invalid credentials");
        }else if (username.isEmpty()){
            loggedIn = false;  
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Username vuoto", "Invalid credentials");
        }else if (password.isEmpty()){
            loggedIn = false;  
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Password vuota", "Invalid credentials");
        }*/
        // sanitizzazione dell'input
        // DA FARE
        
        PersonaBean pb = new PersonaBean();
        pb.setUid("cia");
        // query
        try {
            DBMS dbms = new DBMS();
            pb = dbms.getUser(username, SHAsum(password.getBytes()));
        } catch (ClassNotFoundException cnfe) {
            cnfe.getMessage();
        } catch (NoSuchAlgorithmException nsae) {
            nsae.getMessage();
        }
        
        if (pb == null) {
            loggedIn = false;  
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Invalid credentials"); 
            FacesContext.getCurrentInstance().addMessage(null, msg);  
             context.addCallbackParam("loggedIn", loggedIn); 
            //FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
             return null;

        }else{
            loggedIn = true;  
            //msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", pb.getUid());  
            //FacesContext.getCurrentInstance().addMessage(null, msg);  
            context.addCallbackParam("loggedIn", loggedIn);
            //FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
            //FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
            //context.addCallbackParam("url", "index.xhtml");
            return "success";

        }
        
     //  FacesContext.getCurrentInstance().addMessage(null, msg);  
       // context.addCallbackParam("loggedIn", loggedIn); 
        
                
        //return this.SHAsum(password.getBytes());
               //return "controllo credenziali con query SQL...";

    }
    
   public static String SHAsum(byte[] convertme) throws NoSuchAlgorithmException{
   MessageDigest md = MessageDigest.getInstance("SHA-1"); 
   return byteArray2Hex(md.digest(convertme));
}

private static String byteArray2Hex(final byte[] hash) {
   Formatter formatter = new Formatter();
   for (byte b : hash) {
       formatter.format("%02x", b);
   }
   return formatter.toString();
}
}
