package main;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author ale
 */
public class Persona {

    private String uid, lastname, firstname, email, pwd;

    public Persona() {
        uid = lastname = firstname = email = pwd = "";
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

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
