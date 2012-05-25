package main;

/**
 *
 * @author ale
 */
public class PersonaBean {

    private String uid, lastname, firstname, email, pwd;

    PersonaBean() {
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

    public String getLastname(String lastname) {
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
}
