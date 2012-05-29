/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.sql.*;
import java.util.*;

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
    private String url = "jdbc:mysql://localhost:3306/iMoviesDB";
    /**
     * Driver da utilizzare per la connessione e l'esecuzione delle query.
     */
    private String driver = "com.mysql.jdbc.Driver";
    //Recupera tutte le informazioni di un particolare corso di studi
    private String login = "SELECT uid,lastname,firstname,email,pwd FROM users WHERE uid=? AND pwd=?";
    private PersonaBean bean;

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
	public PersonaBean getUser(String userid, String password) {
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
			rs=pstmt.executeQuery(); 
     
			// Memorizzo il risultato dell'interrogazione nel Vector
			// while(rs.next())
			//	result.add(
                        if (rs.next())
                            return makePersonaBean(rs);//);
		} catch(SQLException sqle) {                /* Catturo le eventuali eccezioni! */
			sqle.printStackTrace();
		} finally {                                 /* Alla fine chiudo la connessione. */
			try {
				con.close();
			} catch(SQLException sqle1) {
				sqle1.printStackTrace();
			}
		}
		return null;
    }

    private PersonaBean makePersonaBean(ResultSet rs) throws SQLException {
        bean = new PersonaBean();
        try {
            bean.setUid(rs.getString("uid"));
            bean.setLastname(rs.getString("lastname"));
            bean.setFirstname(rs.getString("firstname"));
            bean.setEmail(rs.getString("email"));
            bean.setPwd(rs.getString("pwd"));
            return bean;
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }
}
