package utils;

import java.io.*;
import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import main.Persona;

/**
 * Classe di utilità
 *
 * @author Alessandro Gottoli
 * @author Nicolò Marchi
 * @author Mattia Peretti
 * @version 1.0
 */
public class Utilities {

    private static IMoviesLogger log = new IMoviesLogger("main.Utilities");
    private static final String priv = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//certs//private//") + "/";
    private static final String cert = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//certs//cert//") + "/";
    private static final String scripts = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//scripts//") + "/";
    private static final String directory = "/etc/ssl/CA_iMovies";

    /**
     * Crea il certificato(chiave privata + certificate signing request)
     *
     * @param pb L'utente per il quale il certificato va creato
     * @param password La password della chiave privata
     * @param startDate La data di inizio di validità del certificato
     * selezionata dall'utente
     * @param endDate La data di termine validità del certificato selezionata
     * dall'utente
     * @return true se la creazione è andata a buon fine, false altrimenti
     */
    public static boolean createCertificate(Persona pb, String password, String startDate, String endDate) {

        String subject = "\"/C=IT/ST=Italy/L=Verona/O=iMovies Certificate Authority"
                + "/OU=iMovies Security Department/CN=" + pb.getUid() + "/emailAddress=" + pb.getEmail() + "/SN=" + pb.getLastname() + ""
                + "/GN=" + pb.getFirstname() + "\"";

        try {

            FileOutputStream file = new FileOutputStream(FacesContext.getCurrentInstance().getExternalContext().getRealPath("//scripts//") + "/psw");
            PrintStream Output = new PrintStream(file);

            Output.println(password);
            Output.println(password);
            Output.flush();
            Output.close();
            file.flush();
            file.close();
        } catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        log.info(true, "Creazione certificato", "Entrata nel costruttore di Utilities", "Entrata nel costruttore di Utilities");

        Process process;

        try {
            log.info(true, "Creazione certificato", "Generazione file con chiavi", "Generazione csr in cartella, questa è la cartella scripts: " + scripts);
            log.info(true, "Creazione certificato", "Generazione file con chiavi", "comando: sh " + scripts + "CA.sh -newreq " + subject + " " + pb.getUid() + " " + scripts + "psw");

            process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "sh " + scripts + "CA.sh -newreq " + subject + " " + pb.getUid() + " " + scripts + "psw"});

            process.waitFor();

            log.info(true, "Creazione certificato", "Comando firma", "comando: sh " + scripts + "CA.sh -sign " + pb.getUid() + " " + startDate + " " + endDate);

            process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "sh " + scripts + "CA.sh -sign " + pb.getUid() + " " + startDate + " " + endDate});
            process.waitFor();

            log.info(false, "Certificate Created", "Certificate Created", "Certificate Created");
        } catch (IOException ex) {
            log.err(false, "Errore di IO", ex.toString(), ex.toString());
        } catch (InterruptedException ex) {
            log.err(false, "Errore nel waitFor", ex.toString(), ex.toString());
        }

        return true;
    }

    /**
     * Restituisce la lista dei certificati dell'utente
     *
     * @param username lo username dell'utente
     * @param admin true se questo utente è un amministratore, false altrimenti
     * @return un oggetto di tipo List contenente tutti i certificati
     * appartenenti all'utente. Nel caso l'utente sia anche un amministratore
     * verranno restituiti tutti i certificati rilasciati
     * @throws CertificateException
     */
    public static List<UserCert> getCertificateUser(String username, boolean admin) throws CertificateException {

        List<UserCert> list = new ArrayList<UserCert>();
        File cartella = new File(directory + "/newcerts");
        File[] files = null;
        StringTokenizer tmp, tmp1;
        String mom;
        UserCert ue = null;
        if (cartella.isDirectory()) {
            files = cartella.listFiles();
        }
        for (int i = 0; i < files.length; i++) {
            try {

                FileInputStream in = new FileInputStream(files[i].getAbsoluteFile());
                CertificateFactory cf = CertificateFactory.getInstance("X509");
                X509Certificate cer = (X509Certificate) cf.generateCertificate(in);
                mom = cer.getSubjectDN().getName();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                TimeZone tz = TimeZone.getTimeZone("GMT");
                dateFormat.setTimeZone(tz);
                String startD = dateFormat.format(cer.getNotBefore());
                mom = mom.trim();
                mom = mom.replace(" ", "");
                tmp = new StringTokenizer(mom, ",");

                boolean guardia = true;
                while (tmp.hasMoreTokens() && guardia) {

                    tmp1 = new StringTokenizer(tmp.nextToken(), "=");

                    if (tmp1.nextToken().equals("CN")) {

                        /**
                         * Aggiunto controllo su amministratore: se il
                         * certificato appartiene all'utente oppure questo è un
                         * amministratore, tale certificato viene visualizzato.
                         */
                        String user_cert = tmp1.nextToken();
                        if ((user_cert.equals(username) || admin) && !user_cert.equals("iSD")) {

                            ue = new UserCert();
                            ue.setStartD(startD);
                            ue.setNameFile(files[i].getName());

                            BigInteger serial = cer.getSerialNumber();
                            ue.setSerial(serial.toString(16));

                            ue = getIndexInfo(ue);
                            ue.setPasswordKey("");
                            ue.setPasswordPkcs12("");

                            /**
                             * Aggiunto il nome utente dell'utente che possiede
                             * il certificato (funzione utilizzata da admin)
                             */
                            ue.setUser(user_cert);

                            list.add(ue);

                            guardia = false;
                        }
                    }

                }
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
            } catch (java.security.cert.CertificateException ex) {
                Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        Collections.sort(list, new Comparator<UserCert>() {

            @Override
            public int compare(UserCert o1, UserCert o2) {
                if (Integer.parseInt(o1.getSerial(), 16) < Integer.parseInt(o2.getSerial(), 16)) {
                    return -1;
                } else if (Integer.parseInt(o1.getSerial(), 16) > Integer.parseInt(o2.getSerial(), 16)) {
                    return 1;
                }
                return 0;
            }
        });

        return list;
    }

    /**
     * Restituisce un oggetto UserCert modificato, con in aggiunta le
     * informazioni estratte dal file
     * <code>index.txt</code>
     *
     * @param ue oggetto da modificare
     * @return oggetto modificato
     */
    public static UserCert getIndexInfo(UserCert ue) {

        File index = new File(directory + "/index.txt");
        int anno;
        StringBuilder date;

        try {
            FileReader fin = new FileReader(index);
            BufferedReader br = new BufferedReader(fin);

            String line;
            boolean guardia = true;
            while ((line = br.readLine()) != null && guardia) {
                StringTokenizer tok = new StringTokenizer(line, "\t");

                String ver = tok.nextToken();
                String dateE = tok.nextToken();
                date = new StringBuilder();
                anno = Integer.parseInt(dateE.substring(0, 2)) + 2000;
                date.append(anno).append("/").append(dateE.substring(2, 4)).append("/").append(dateE.substring(4, 6)).append(" ").append(dateE.substring(6, 8)).append(":").append(dateE.substring(8, 10)).append(":").append(dateE.substring(10, 12));
                dateE = date.toString();

                String dateR = "Not Revoked";
                if (ver.equals("R")) {
                    dateR = tok.nextToken();
                    date = new StringBuilder();
                    anno = Integer.parseInt(dateR.substring(0, 2)) + 2000;
                    date.append(anno).append("/").append(dateR.substring(2, 4)).append("/").append(dateR.substring(4, 6)).append(" ").append(dateR.substring(6, 8)).append(":").append(dateR.substring(8, 10)).append(":").append(dateR.substring(10, 12));
                    dateR = date.toString();
                }

                String str = tok.nextElement().toString();
                if (str.startsWith("0")) {
                    str = str.replace("0", "");
                }

                if (str.toLowerCase().equals(ue.getSerial())) {
                    ue.setVer(ver);
                    ue.setDateE(dateE);
                    ue.setDateR(dateR);
                    guardia = false;
                }
            }

            br.close();
            fin.close();

        } catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ue;
    }

    /**
     * Genera il file PCKS12 partendo dal certificato passato come parametro e
     * dalla sua chiave privata.
     *
     * @param userCert oggetto con i dati del certificato
     */
    public static void pkcs12Certificate(UserCert userCert) {
        System.out.println("sono in pkcsCertificate.");
        Process process = null;
        String[] cmd = new String[]{"bash", "-c", "sh " + scripts + "CA.sh -pkcs12 " + userCert.getNameFile() + " " + userCert.getNameFile().replace(".pem", "") + " " + userCert.getPasswordPkcs12() + " " + userCert.getPasswordKey()};

        try {
            log.info(true, "Creazione certificato", "verifica ", "comando: " + cmd[0] + " " + cmd[1] + " " + cmd[2]);

            process = Runtime.getRuntime().exec(cmd);
            process.waitFor();

        } catch (IOException ex) {
            log.err(false, "Errore di IO", ex.toString(), ex.toString());
        } catch (InterruptedException ex) {
            log.err(false, "Errore nel waitFor", ex.toString(), ex.toString());
        }
    }

    /**
     * Metodo che permette di revocare il certificato passato come parametro.
     *
     * @param ue certificato da revocare
     * @return l'oggetto revocato
     */
    public static UserCert revokeCertificate(UserCert ue) {
        System.out.println("dentro revokeCertificate");

        Process process = null;
        try {
            log.info(false, "Creazione certificato", "Generazione file con chiavi", "comando: sh " + scripts + "CA.sh -revoke " + directory + "/newcerts/" + ue.getNameFile());

            process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "sh " + scripts + "CA.sh -revoke " + directory + "/newcerts/" + ue.getNameFile()});
            process.waitFor();
        } catch (IOException ex) {
            log.err(false, "Errore di IO", ex.toString(), ex.toString());
        } catch (InterruptedException ex) {
            log.err(false, "Errore nel waitFor", ex.toString(), ex.toString());
        }

        return getIndexInfo(ue);

    }

    /**
     * Metodo che cancella il certificato corrispondente ai dati contenuti in
     * selectedUserCert
     *
     * @param selectedUserCert certificato da eliminare
     */
    public static void deleteCertificate(UserCert selectedUserCert) {
        Process process = null;
        try {
            log.info(false, "Creazione certificato", "Generazione file con chiavi", "comando: rm " + directory + "/newcerts/" + selectedUserCert.getNameFile());

            process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "sh " + scripts + "CA.sh -revcanc " + directory + "/newcerts/" + selectedUserCert.getNameFile()});
            process.waitFor();
        } catch (IOException ex) {
            log.err(false, "Errore di IO", ex.toString(), ex.toString());
        } catch (InterruptedException ex) {
            log.err(false, "Errore nel waitFor", ex.toString(), ex.toString());
        }
    }

    /**
     * Metodo che controlla la compatibilità delle date passate in input per la
     * creazione di un nuovo certificato con quelle dei certificati già presenti
     *
     * @param username nome utente
     * @param startD data di inizio
     * @param endD data di fine
     * @return true se è compatibile, false altrimenti
     */
    public static boolean checkIncompatibleDate(String username, String startD, String endD) {

        File index = new File(directory + "/index.txt");

        try {
            FileReader fin = new FileReader(index);
            BufferedReader br = new BufferedReader(fin);

            String line;
            boolean guardia = true;
            while ((line = br.readLine()) != null && guardia) {
                StringTokenizer tok = new StringTokenizer(line, "\t");

                String ver = tok.nextToken();
                if (ver.equals("V")) {
                    tok.nextToken();

                    String str = tok.nextElement().toString();
                    if (str.startsWith("0")) {
                        str = str.replace("0", "");
                    }
                    String serialeCert = str.toLowerCase();
                    if (serialeCert.length() == 1) {
                        serialeCert = "0" + serialeCert;
                    }
                    tok.nextToken();

                    String subj = tok.nextToken();
                    StringTokenizer cn = new StringTokenizer(subj, "/");
                    String temp;
                    boolean guardia2 = true;
                    String uidentifier;
                    while (cn.hasMoreTokens() && guardia2) {
                        temp = cn.nextToken();

                        if (temp.startsWith("CN=")) {
                            guardia2 = false;
                            uidentifier = temp.substring(3);

                            if (uidentifier.equals(username)) {
                                File file = new File(directory + "/newcerts/" + serialeCert.toUpperCase() + ".pem");
                                try {

                                    FileInputStream in = new FileInputStream(file.getAbsoluteFile());
                                    CertificateFactory cf = CertificateFactory.getInstance("X509");
                                    X509Certificate cer = (X509Certificate) cf.generateCertificate(in);
                                    in.close();

                                    DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
                                    TimeZone tz = TimeZone.getTimeZone("GMT");
                                    dateFormat.setTimeZone(tz);
                                    log.info(true, "end=" + endD + " < di notBefore=" + dateFormat.format(cer.getNotBefore()) + " POI start=" + startD + " > di notAfter=" + dateFormat.format(cer.getNotAfter()), null, null);
                                    if (!(endD.compareTo(dateFormat.format(cer.getNotBefore())) < 0 || startD.compareTo(dateFormat.format(cer.getNotAfter())) > 0)) {
                                        return false;
                                    }

                                } catch (IOException ex) {
                                    Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (java.security.cert.CertificateException ex) {
                                    Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }
                        }
                    }



                }
            }

            br.close();
            fin.close();

        } catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }
}
