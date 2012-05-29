package utils;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
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
    /**
     * Oggetto per mantenere una coda con gli ultimi messaggi di errore
     */
    private Queue<FacesMessage> err_messages;

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
        err_messages = new ArrayBlockingQueue(10);
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
            err_messages.add(msg);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void printErrMessages() {
        FacesMessage msg = err_messages.poll();

        while (msg != null) {
            FacesContext.getCurrentInstance().addMessage(null, msg);
            msg = err_messages.poll();
        }
    }
}
