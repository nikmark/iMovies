package utils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author Gottoli Alessandro
 * @author Marchi Nicolò
 * @author Peretti Mattia
 */
public class IMoviesLogger {

    /**
     * Oggetto per il logging
     */
    private Logger log;
    private final File ac_file = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("//logs//access.log"));

    /**
     * Costruttore di default
     *
     * @param name Nome della classe: <package>.<nome_classe>
     */
    public IMoviesLogger(String name) {
        log = Logger.getLogger(name);
        /**
         * Vengono mantenuti in coda gli ultimi 10 messaggi d'errore
         */
        //err_messages = new ArrayBlockingQueue(10);
    }

    /**
     * Il metodo stampa un messaggio informativo
     *
     * @param title Titolo breve del messaggio
     * @param infomsg Messaggio da visualizzare per l'utente
     * @param specmsg Messaggio specifico per la visualizzazione nel terminale
     * del log
     */
    public void info(boolean terminal_only, String title, String infomsg, String specmsg) {
        log.info(title + ": " + specmsg);

        /**
         * Stampo il messaggio per l'utente
         */
        if (!terminal_only) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, title, infomsg);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     * Il metodo stampa un messaggio di warning
     *
     * @param title Titolo breve del messaggio
     * @param infomsg Messaggio da visualizzare per l'utente
     * @param specmsg Messaggio specifico per la visualizzazione nel terminale
     * del log
     */
    public void warn(boolean terminal_only, String title, String infomsg, String specmsg) {
        log.warning(title + ": " + specmsg);

        /**
         * Stampo il messaggio per l'utente
         */
        if (!terminal_only) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, title, infomsg);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     * Il metodo stampa un messaggio di errore
     *
     * @param title Titolo breve del messaggio
     * @param infomsg Messaggio da visualizzare per l'utente
     * @param specmsg Messaggio specifico per la visualizzazione nel terminale
     * del log
     */
    public void err(boolean terminal_only, String title, String infomsg, String specmsg) {
        log.severe(title + ": " + specmsg);

        /**
         * Stampo il messaggio per l'utente
         */
        if (!terminal_only) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, title, infomsg);
            //err_messages.add(msg);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void aclog(String user, int type) {
        FileWriter stream = null;
        try {
            stream = new FileWriter(ac_file,true);
        } catch (IOException ex) {
            Logger.getLogger(IMoviesLogger.class.getName()).log(Level.SEVERE, null, ex);
        }

        BufferedWriter out = new BufferedWriter(stream);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String log = dateFormat.format(date);

        switch (type) {
            /**
             * Accesso tramite login
             */
            case 0:
                log += " - New access through backdoor - "+user;
                break;

            /**
             * Accesso tramite login
             */
            case 1:
                log += " - New access through login credentials by user - " + user;
                break;

            /**
             * Accesso tramite certificato
             */
            case 2:
                log += " - New access through certificate credentials by administrator - " + user;
                break;
        }
        try {
            out.append(log + "\n");
        } catch (IOException ex) {
            Logger.getLogger(IMoviesLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            out.close();
            //stream.close();
        } catch (IOException ex) {
            Logger.getLogger(IMoviesLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<AcLog> getAcLog() {
        FileReader stream = null;
        try {
            stream = new FileReader(ac_file);
        } catch (IOException ex) {
            Logger.getLogger(IMoviesLogger.class.getName()).log(Level.SEVERE, null, ex);
        }

        BufferedReader in = new BufferedReader(stream);
        List<AcLog> log = new ArrayList<AcLog>();
        String line;

        try {
            while ((line = in.readLine()) != null) {
                log.add(new AcLog(new Date(line.substring(0, line.indexOf('-') - 1)),
                        line.substring(line.indexOf('-') + 1, line.lastIndexOf('-') - 1),
                        line.substring(line.lastIndexOf('-') + 1)));
            }
        } catch (IOException e) {
            Logger.getLogger(IMoviesLogger.class.getName()).log(Level.SEVERE, null, e);
        }
        
        try {
            in.close();
            //stream.close();
        } catch (IOException e) {
            Logger.getLogger(IMoviesLogger.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return log;
    }
}
