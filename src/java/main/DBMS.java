/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.sql.*;
import java.util.*;
import utils.IMoviesLogger;


/**
 *
 * @author ale
 */
public class DBMS {

    //Dati di identificazione dell'utente (da personalizzare)
    private String user = "imovies";
    private String passwd = "imovies";
    /**
     * URL per la connessione alla base di dati e' formato dai seguenti
     * componenti: <protocollo>://<host del server>/<nome base di dati>.
     */
    private String url = "jdbc:mysql://192.168.1.4:3306/iMoviesDB";
    /**
     * Driver da utilizzare per la connessione e l'esecuzione delle query.
     */
    private String driver = "com.mysql.jdbc.Driver";
    //Recupera tutte le informazioni di un particolare corso di studi
    private String login = "SELECT uid,lastname,firstname,email,pwd FROM users WHERE uid=? AND pwd=?";
    private String change = "UPDATE users SET lastname=?, firstname=?, email=?, pwd=? WHERE uid=?";
    private String row = "SELECT uid,lastname,firstname,email,pwd FROM users WHERE uid=?";

    /*
     * private String changeLast = "UPDATE users SET lastname=? WHERE uid=?";
     * private String changeFirst = "UPDATE users SET firstname=? WHERE uid=?";
     * private String changeEmail = "UPDATE users SET email=? WHERE uid=?";
     * private String changePwd = "UPDATE users SET pwd=? WHERE uid=?";
     */
    /**
     * Costruttore della classe. Carica i driver da utilizzare per la
     * connessione alla base di dati.
     *
     * @throws ClassNotFoundException Eccezione generata nel caso in cui i
     * driver per la connessione non siano trovati nel CLASSPATH.
     */
    public DBMS() throws ClassNotFoundException {
        Class.forName(driver);
    }

    // Metodo per il recupero delle principali informazioni di tutti i corsi di studi
    public Persona getUser(String userid, String password) {
        // Dichiarazione delle variabili
        Connection con = null;
        PreparedStatement pstmt;
        ResultSet rs;
        //Vector result = new Vector();
        try {
            // Tentativo di connessione al database
            con = DriverManager.getConnection(url, user, passwd);

            // Connessione riuscita, ottengo l'oggetto per l'esecuzione
            // dell'interrogazione.
            pstmt = con.prepareStatement(login);
            pstmt.clearParameters();
            //Imposto i parametri della query
            pstmt.setString(1, userid);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            // Memorizzo il risultato dell'interrogazione nel Vector
            // while(rs.next())
            //	result.add(
            if (rs.next()) {
                return makePersonaBean(rs);//);
            }
        } catch (SQLException sqle) {                /*
             * Catturo le eventuali eccezioni!
             */
            sqle.printStackTrace();
        } finally {                                 /*
             * Alla fine chiudo la connessione.
             */
            try {
                con.close();
            } catch (SQLException sqle1) {
                sqle1.printStackTrace();
            }
        }
        return null;
    }

    private Persona makePersonaBean(ResultSet rs) throws SQLException {
        Persona bean = null;
            try {
                bean = new Persona();

                bean.setUid(rs.getString("uid"));
                bean.setLastname(rs.getString("lastname"));
                bean.setFirstname(rs.getString("firstname"));
                bean.setEmail(rs.getString("email"));
                bean.setPwd(rs.getString("pwd"));
            } catch (SQLException e) {
                throw new SQLException(e.getMessage());
            }
        return bean;

    }

    public Persona change(Persona pb) {
        
        
        // Dichiarazione delle variabili
        Connection con = null;
        PreparedStatement pstmt;
        ResultSet rs;
        int result;
        //Vector result = new Vector();
        try {
            // Tentativo di connessione al database
            con = DriverManager.getConnection(url, user, passwd);

            // Connessione riuscita, ottengo l'oggetto per l'esecuzione
            // dell'interrogazione.
            pstmt = con.prepareStatement(change);
            pstmt.clearParameters();
            //Imposto i parametri della query
            pstmt.setString(1, pb.getLastname());
            pstmt.setString(2, pb.getFirstname());
            pstmt.setString(3, pb.getEmail());
            pstmt.setString(4, pb.getPwd());
            pstmt.setString(5, pb.getUid());

            IMoviesLogger log = new IMoviesLogger("main.DBMS");
            log.info(false, "stampa email= "+pb.getEmail(), "stampa email= "+pb.getEmail(), "stampa email= "+pb.getEmail());
            result = pstmt.executeUpdate();

            pstmt = con.prepareStatement(row);
            pstmt.clearParameters();
            pstmt.setString(1, pb.getUid());
            rs = pstmt.executeQuery();

            // Memorizzo il risultato dell'interrogazione nel Vector
            // while(rs.next())
            //	result.add(
            if (rs.next()) {
                return makePersonaBean(rs);//);
            }
        } catch (SQLException sqle) {                /*
             * Catturo le eventuali eccezioni!
             */
            sqle.printStackTrace();
        } finally {                                 /*
             * Alla fine chiudo la connessione.
             */
            try {
                con.close();
            } catch (SQLException sqle1) {
                sqle1.printStackTrace();
            }
        }
        return null;
    }
}
