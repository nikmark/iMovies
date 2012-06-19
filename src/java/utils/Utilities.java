/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.*;
import java.math.BigInteger;
import java.security.cert.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.context.FacesContext;
import main.CertificateBean;
import main.LoginBean;
import main.Persona;
import utils.IMoviesLogger;

/**
 * Classe di utilità
 * @author Gottoli, Marchi, Peretti
 */
public class Utilities {

    private static IMoviesLogger log = new IMoviesLogger("main.Utilities");
    private static final String priv = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//certs//private//") + "/";
    private static final String cert = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//certs//cert//") + "/";
    private static final String scripts = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//scripts//") + "/";
    private static final String directory = "/etc/ssl/CA_iMovies";

    /**
     * Crea il certificato(chiave privata + certificate signing request)
     * @param pb L'utente per il quale il certificato va creato
     * @param password La password della chiave privata
     * @param startDate La data di inizio di validità del certificato selezionata dall'utente
     * @param endDate La data di termine validità del certificato selezionata dall'utente
     * @return true se la creazione è andata a buon fine, false altrimenti
     */
    public static boolean createCertificate(Persona pb, String password, String startDate, String endDate) {
//                log.info(false, "Creazione certificato", "Entrata nel costruttore di Utilities", "Entrata nel costruttore di Utilities");
        String line;
        String subject = "\"/C=IT/ST=Italy/L=Verona/O=iMovies Certificate Authority"
                + "/OU=iMovies Security Department/CN=" + pb.getUid() + "/emailAddress=" + pb.getEmail() + "/SN=" + pb.getLastname() + ""
                + "/GN=" + pb.getFirstname() + "\"";
//        subject = subject.replace(" ", "\\ ");
//        File psw = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("//scripts//")+"/"+"psw");
        try {
//            psw.createNewFile();

            FileOutputStream file = new FileOutputStream(FacesContext.getCurrentInstance().getExternalContext().getRealPath("//scripts//") + "/psw");
            PrintStream Output = new PrintStream(file);

            Output.println(password);
            Output.println(password);
//            Output.println("sh " + scripts + "CA.sh -sign " + cert + pb.getUid() + ".csr " + pb.getUid() + "");
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

            log.info(false, "Creazione certificato", "Comando firma", "comando: sh " + scripts + "CA.sh -sign " + pb.getUid() + " " + startDate + " " + endDate);

            process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "sh " + scripts + "CA.sh -sign " + pb.getUid() + " " + startDate + " " + endDate});
            process.waitFor();
        } catch (IOException ex) {
            log.err(false, "Errore di IO", ex.toString(), ex.toString());
        } catch (InterruptedException ex) {
            log.err(false, "Errore nel waitFor", ex.toString(), ex.toString());
        }

        return true;
    }

    /**
     * Restituisce la lista dei certificati dell'utente
     * @param username lo username dell'utente
     * @param admin true se questo utente è un amministratore, false altrimenti
     * @return un oggetto di tipo List contenente tutti i certificati appartenenti all'utente. 
     * Nel caso l'utente sia anche un amministratore verranno restituiti tutti i certificati rilasciati
     * @throws CertificateException 
     */
    public static List<UserCert> getCertificateUser(String username, boolean admin) throws CertificateException {
//        CertificateBean certBean = new CertificateBean();

//                CertificateBean certBean = new CertificateBean();
//FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("CertificateBean",certBean);  


        List<UserCert> list = new ArrayList<UserCert>();
        File cartella = new File(directory + "/newcerts");
        File[] files = null;
        StringTokenizer tmp, tmp1;
        String mom;
        UserCert ue = null;
        //System.out.println(cartella.getAbsolutePath());
        if (cartella.isDirectory()) {
            files = cartella.listFiles();
//            for (int i=0;i<files.length;i++){
//                        System.out.println(files[i].toString());
//            }
        }
        for (int i = 0; i < files.length; i++) {
            try {
                //System.out.println(files[i].getAbsoluteFile().toString());
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
                //System.out.println("stringa trimmata: "+mom);
                boolean guardia = true;
                while (tmp.hasMoreTokens() && guardia) {
                    //System.out.println("qeusto è il token delle virgole: "+tmp);

                    tmp1 = new StringTokenizer(tmp.nextToken(), "=");
                    //System.out.println("qeusto è il token tokenizzato di nuovo: "+tmp1.toString());

                    if (tmp1.nextToken().equals("CN")) {
                        /**
                         * Aggiunto controllo su amministratore: se il
                         * certificato appartiene all'utente oppure questo è un
                         * amministratore, tale certificato viene visualizzato.
                         */
                        String user_cert = tmp1.nextToken();
                        if ((user_cert.equals(username) || admin) && !user_cert.equals("iSD")) {
                            //System.out.println("questo è il certificato di "+username+": "+files[i].getName());
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
        //System.out.println("fine del certificateUser");

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
     * Restituisce il certificato dell'utente
     * @param ue 
     * @return 
     */
    public static UserCert getIndexInfo(UserCert ue) {

//            HashMap<String,ArrayList<String>> map=new HashMap<String, ArrayList<String>>();
//            
        File index = new File(directory + "/index.txt");
        int anno;
        StringBuilder date;
//            
        try {
            FileReader fin = new FileReader(index);
            BufferedReader br = new BufferedReader(fin);

            String line;
            boolean guardia = true;
            while ((line = br.readLine()) != null && guardia) {
                StringTokenizer tok = new StringTokenizer(line, "\t");
//               
//                ArrayList<String> arr = new ArrayList<String>();
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
//                StringBuilder string1 = new StringBuilder();
//                StringBuilder string2 = new StringBuilder();
//                //string1.append(Integer.parseInt(, 16));
//                string2.append(Integer.parseInt(ue.getSerial(),16));
////                String hex = String.format("%x", ue.getSerial());
//                System.out.println("Stringa1= "+tok.nextToken()+"\nStringa2 ="+string2);
                String str = tok.nextElement().toString();
                if (str.startsWith("0")) {
                    str = str.replace("0", "");
                }
//                log.info(true, "str = " + str, "str = " + str, "str = " + str);
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
        //System.out.println("fine del getIndexInfo");

        return ue;
    }

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

    public static UserCert revokeCertificate(UserCert ue) {
        System.out.println("dentro revokeCertificate");

        Process process = null;
//        Process process2=null;
        try {
//            log.info(false, "Creazione certificato", "Generazione file con chiavi", "Generazione csr in cartella, questa è la cartella scripts: " + scripts);
            log.info(false, "Creazione certificato", "Generazione file con chiavi", "comando: sh " + scripts + "CA.sh -revoke " + directory + "/newcerts/" + ue.getNameFile());

            process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "sh " + scripts + "CA.sh -revoke " + directory + "/newcerts/" + ue.getNameFile()});
//            process2 = Runtime.getRuntime().exec(new String[]{"bash", "-c", "echo sh " + scripts + "CA.sh -revoke "+directory+"/newcerts/"+nomeFile+" >> "+scripts+"error"});
            process.waitFor();
//            process2.waitFor();
        } catch (IOException ex) {
            log.err(false, "Errore di IO", ex.toString(), ex.toString());
        } catch (InterruptedException ex) {
            log.err(false, "Errore nel waitFor", ex.toString(), ex.toString());
        }

        return getIndexInfo(ue);

    }

    public static void deleteCertificate(UserCert selectedUserCert) {
        Process process = null;
//        Process process2=null;
        try {
//            log.info(false, "Creazione certificato", "Generazione file con chiavi", "Generazione csr in cartella, questa è la cartella scripts: " + scripts);
            log.info(false, "Creazione certificato", "Generazione file con chiavi", "comando: rm " + directory + "/newcerts/" + selectedUserCert.getNameFile());

            process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "sh " + scripts + "CA.sh -revcanc " + directory + "/newcerts/" + selectedUserCert.getNameFile()});
//            process2 = Runtime.getRuntime().exec(new String[]{"bash", "-c", "echo sh " + scripts + "CA.sh -revoke "+directory+"/newcerts/"+nomeFile+" >> "+scripts+"error"});
            process.waitFor();
//            process2.waitFor();
        } catch (IOException ex) {
            log.err(false, "Errore di IO", ex.toString(), ex.toString());
        } catch (InterruptedException ex) {
            log.err(false, "Errore nel waitFor", ex.toString(), ex.toString());
        }
    }

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
                //log.info(true, "\n\n\nver= " + ver, "ver= " + ver, "ver= " + ver);
                if (ver.equals("V")) {
                    tok.nextToken();
//                    tok.nextToken();

                    String str = tok.nextElement().toString();
                    if (str.startsWith("0")) {
                        str = str.replace("0", "");
                    }
                    String serialeCert = str.toLowerCase();
                    //log.info(true, "\nseriale= " + serialeCert, "seriale= " + serialeCert, "seriale= " + serialeCert);
                    if (serialeCert.length() == 1)
                        serialeCert = "0" + serialeCert;
                    tok.nextToken();

                    String subj = tok.nextToken();
                    //log.info(true, "\nsubj= " + subj, "subj= " + subj, "subj= " + subj);
                    StringTokenizer cn = new StringTokenizer(subj, "/");
                    String temp;
                    boolean guardia2 = true;
                    String uidentifier;
                    while (cn.hasMoreTokens() && guardia2) {
                        temp = cn.nextToken();
                        //log.info(true, "\ntemp= " + temp, "temp= " + temp, "temp= " + temp);
                        if (temp.startsWith("CN=")) {
                            guardia2 = false;
                            uidentifier = temp.substring(3);
                            //log.info(true, "\nuid= " + uidentifier, "uid= " + uidentifier, "uid= " + uidentifier);

                            if (uidentifier.equals(username)) {
                                File file = new File(directory + "/newcerts/" + serialeCert.toUpperCase() + ".pem");
                                try {
                                    //System.out.println(files[i].getAbsoluteFile().toString());
                                    FileInputStream in = new FileInputStream(file.getAbsoluteFile());
                                    CertificateFactory cf = CertificateFactory.getInstance("X509");
                                    X509Certificate cer = (X509Certificate) cf.generateCertificate(in);
                                    in.close();
//                                    if (endDate.compareTo(cer.getNotBefore()) <= 0 || startDate.compareTo(cer.getNotAfter()) >= 0) {
//                                        //log.info(true, "\ndate OK", "date OK", "date OK");
//                                    } else {
//                                        //DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
//                                        //log.info(true, dateFormat.format(startDate)+ " " + dateFormat.format(endDate), null, null);
//                                        //log.info(true, "\ndate BAD " + endDate.compareTo(cer.getNotBefore()) + " e " + startDate.compareTo(cer.getNotAfter()), "date BAD", "date BAD");
//                                        return false;
//                                    }
                
                                    DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
                                    TimeZone tz = TimeZone.getTimeZone("GMT");
                                    dateFormat.setTimeZone(tz);
                                    //String startD = dateFormat.format(cer.getNotBefore());
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
        //System.out.println("fine del getIndexInfo");

        return true;
    }
}
