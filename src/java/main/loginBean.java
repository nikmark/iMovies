package main;

/**
 *
 * @author mattia
 */
public class loginBean {

    private String username;
    private String password;
    
    
    /**
     * Creates a new instance of loginBean
     */
    public loginBean() {
        
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
    public String query() throws ClassNotFoundException {
        
        DBMS prova = new DBMS();
        prova.getUsers();
        if (username == null || password == null)
            // prima apertura allora non fare niente
            return "";
        else if (username.isEmpty())
            return "user non può essere vuoto";
        else if (password.isEmpty())
            return "password non può essere vuota";
        
        // sanitizzazione dell'input
        // DA FARE
        
        
        // query
        return "controllo credenziali con query SQL...";

    }
}
