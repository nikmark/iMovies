package main;

/**
 * Classe per la rappresentazione di un utente
 * @author Gottoli, Marchi, Peretti
 */
public class Persona {

    private String uid, lastname, firstname, email, pwd;
    
    /**
     * Inizializzazione di default delle stringhe
     */
    public Persona() {
        uid = lastname = firstname = email = pwd = "";
    }

    /**
     * Imposta lo user id dell'utente
     * @param uid una stringa che rappresenta lo user id dell'utente
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Ritorda lo user id dell'utente
     * @return una stringa che rappresenta lo user id dell'utente
     */
    public String getUid() {
        return uid;
    }

    /**
     * Imposta il cognome dell'utente
     * @param lastname una stringa che rappresenta il cognome dell'utente
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * Restituisce il cognome dell'utente
     * @return una stringa che rappresenta il cognome dell'utente
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Imposta il nome dell'utente
     * @param firstname una stringa che rappresenta il nome dell'utente
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Restituisce il nome dell'utente
     * @return una stringa che rappresenta il nome dell'utente
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Imposta l'email dell'utente
     * @param email una stringa che rappresenta l'email dell'utente
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Restituisce l'email dell'utente
     * @return una stringa che rappresenta l'email dell'utente
     */
    public String getEmail() {
        return email;
    }

    /**
     * Imposta la password dell'utente
     * @param pwd una stringa che rappresenta la password dell'utente (in chiaro)
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * Restituisce la password dell'utente
     * @return una stringa che rappresenta la password dell'utente (in chiaro)
     */
    public String getPwd() {
        return pwd;
    }

//    public void matchingPsw(String strOld, String strNew1, String strNew2) {
//
//
//        RequestContext context = RequestContext.getCurrentInstance();
//        boolean matches;
//        FacesMessage msg = null;
//
//
//        if (strOld.equals(getPwd())) {
//            if (strNew1.equals(strNew2)) {
//                matches = true;
//                context.addCallbackParam("matches", matches);
//                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Passwords OK", "No Problems");
//
//            } else {
//                matches = false;
//                context.addCallbackParam("matches", matches);
//                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Password Error", "The new passwords don't match");
//
//            }
//        } else {
//            matches = false;
//            context.addCallbackParam("matches", matches);
//            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Password Error", "The password don't match with the old password");
//
//        }
//
//        FacesContext.getCurrentInstance().addMessage(null, msg);
//
//    }
    

}
