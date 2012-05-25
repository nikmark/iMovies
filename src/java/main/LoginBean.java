package main;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

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
    
    public String login()
    {
        return "Ciao, sono il bean di login";
    }

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
    public String query() throws NoSuchAlgorithmException {
        if (username == null || password == null)
            // prima apertura allora non fare niente
            return "";
        else if (username.isEmpty())
            return "user non può essere vuoto";
        else if (password.isEmpty())
            return "password non può essere vuota";
        
        // sanitizzazione dell'input
        // DA FARE
        
        PersonaBean pb = new PersonaBean();
        pb.setUid("cia");
        // query
        try {
            DBMS dbms = new DBMS();
            pb = dbms.getUser(username, this.SHAsum(password.getBytes()));
        } catch (ClassNotFoundException cnfe) {
            cnfe.getMessage();
        } catch (NoSuchAlgorithmException nsae) {
            nsae.getMessage();
        }
        
        if (pb == null)
            return "Login Fallito";
        return "Benvenuto " + pb.getUid();
        
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
