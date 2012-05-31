package main;

import utils.Utilities;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Formatter;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;

/**
 *
 * @author mattia
 */
public class LoginBean {

    private String username;
    private String password;
    protected Persona pb;

    public Persona getPb() {
        return pb;
    }

    public void setPb(Persona pb) {
        this.pb = pb;
    }

    /**
     * Creates a new instance of loginBean
     */
    public LoginBean() {
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
    public String login() throws NoSuchAlgorithmException, IOException {

        
        FacesContext fcontext = FacesContext.getCurrentInstance();
        //Persona bean = (Persona) fcontext.getApplication().evaluateExpressionGet(fcontext, "#{Persona}", Persona.class);

        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Invalid credentials");

        boolean loggedIn=false;
        String ret = null;
//                (Persona) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("Persona");
        
//        HttpServletRequest request = (HttpServletRequest)
        HttpSession session= (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(true);  
        // create actual session  
        // getSession() or getSession(true) or getSession(false)???  
        // anyway, 'getSession' checks if a session exists, if not, a new one is created  
  
        /*
         * if (username == null || password == null){ // prima apertura allora
         * non fare niente loggedIn = false; msg = new
         * FacesMessage(FacesMessage.SEVERITY_WARN, "Username e Password vuoti",
         * "Invalid credentials"); }else if (username.isEmpty()){ loggedIn =
         * false; msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Username
         * vuoto", "Invalid credentials"); }else if (password.isEmpty()){
         * loggedIn = false; msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
         * "Password vuota", "Invalid credentials");
        }
         */
        // sanitizzazione dell'input
        // DA FARE

        /**
         * The form component needs to have a UIForm in its ancestry.
         * Suggestion: enclose the necessary components within
         */
        // query
        try {
            DBMS dbms = new DBMS();
            pb = dbms.getUser(username, SHAsum(password.getBytes()));
//            session = session.getSession(true);  

        } catch (ClassNotFoundException cnfe) {
            msg = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Login Error", "Database Connection Failed");
            cnfe.getMessage();

        } catch (NoSuchAlgorithmException nsae) {
            nsae.getMessage();
        }

        
        if (pb != null){
//            if (session.isNew() == false) {  
//                // the session the we got above was not a new one, so destroy it and create new one.  
//                session.invalidate();  
//                session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(true);
//            }  
            //session.setAttribute("Persona", pb);  
            loggedIn = true;
            ret = "success";
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome in iMovies","Welcome in iMovies");
        }
        
        //        {
//            loggedIn = false;
//            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Invalid credentials");
//        } 
//        else 
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("loggedIn", loggedIn);

        return ret;

        //  FacesContext.getCurrentInstance().addMessage(null, msg);  
        // context.addCallbackParam("loggedIn", loggedIn); 


        //return this.SHAsum(password.getBytes());
        //return "controllo credenziali con query SQL...";

    }
    
    public String modify(){
        
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();  
        // create actual session  
        // getSession() or getSession(true) or getSession(false)???  
        // anyway, 'getSession' checks if a session exists, if not, a new one is created  
        HttpSession session = request.getSession(); 
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean loggedIn;
        String ret = null;
//        Persona pb=(Persona) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("Persona");
        
        try {
            DBMS dbms = new DBMS();
            pb = dbms.change(pb);
            
        } catch (ClassNotFoundException cnfe) {
            msg = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Login Error", "Database Connection Failed");
            cnfe.getMessage();
        }
//        catch (NoSuchAlgorithmException nsae) {
//            nsae.getMessage();
//        }
//
//        if (pb == null) {
//            loggedIn = false;
//            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid", "Invalid credentials");
//        } else {
//        this.pb=pb;
//        session.setAttribute("Persona", pb);
            loggedIn = true;
            ret = "success";
            msg = new FacesMessage("Modifies Ok", "Modifies OK on ");
//        }

        context.addCallbackParam("loggedIn", loggedIn);
        FacesContext.getCurrentInstance().addMessage(null, msg);

        return ret;
        
    }
    
    public void makeCertificate() throws IOException, InterruptedException{
        
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();  
        // create actual session  
        // getSession() or getSession(true) or getSession(false)???  
        // anyway, 'getSession' checks if a session exists, if not, a new one is created  
        HttpSession session = request.getSession(); 
//        (Persona) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("Persona")
        Utilities.createCertificate(pb);
        
    }
        
    
    public String logout(){
        
        
//        HttpSession session = request.getSession(); 
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean loggedIn=true;
//        request.getSession().removeAttribute("Persona");
//        request.getSession().invalidate();
//        
//        pb=null;
        
        msg = new FacesMessage("Logout. Bye Bye");
//        }

        context.addCallbackParam("loggedIn", loggedIn);
        FacesContext.getCurrentInstance().addMessage(null, msg);
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        return "success";
    }

    public static String SHAsum(byte[] convertme) throws NoSuchAlgorithmException {
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
