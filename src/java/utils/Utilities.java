/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.*;
import java.security.cert.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import main.LoginBean;
import main.Persona;
import utils.IMoviesLogger;

/**
 *
 * @author nicolo
 */
public class Utilities {

    private static IMoviesLogger log = new IMoviesLogger("main.Utilities");
    private static final String priv = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//certs//private//") + "/";
    private static final String cert = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//certs//cert//") + "/";
    private static final String scripts = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//scripts//") + "/";
    private static final String directory = "/etc/ssl/CA_iMovies";

    public static boolean createCertificate(Persona pb, String password) {
//                log.info(false, "Creazione certificato", "Entrata nel costruttore di Utilities", "Entrata nel costruttore di Utilities");
        String line;
        String subject = "\"/C=IT/ST=Italy/L=Verona/O=iMovies Certificate Authority"
                + "/OU=iMovies Security Department/CN=" + pb.getUid() + "/emailAddress=" + pb.getEmail() + "/SN=" + pb.getLastname() + ""
                + "/GN=" + pb.getFirstname() + "\"";
//        subject = subject.replace(" ", "\\");
//        File psw = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("//scripts//")+"/"+"psw");
        try {
//            psw.createNewFile();

            FileOutputStream file = new FileOutputStream(FacesContext.getCurrentInstance().getExternalContext().getRealPath("//scripts//") + "/psw");
            PrintStream Output = new PrintStream(file);

            Output.println(password);
            Output.println(password);
            Output.println("sh " + scripts + "CA.sh -sign " + cert + pb.getUid() + ".csr " + pb.getUid() + "");
            Output.flush();
            Output.close();
            file.flush();
            file.close();
        } catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        log.info(false, "Creazione certificato", "Entrata nel costruttore di Utilities", "Entrata nel costruttore di Utilities");


        Scanner scan = new Scanner(System.in);

        Process process = null;
        Process process2 = null;

        try {
            log.info(false, "Creazione certificato", "Generazione file con chiavi", "Generazione csr in cartella, questa è la cartella scripts: " + scripts);
            log.info(false, "Creazione certificato", "Generazione file con chiavi", "comando: sh " + scripts + "CA.sh -newreq " + subject + " " + pb.getUid() + " " + scripts + "psw");

            //            process = Runtime.getRuntime().exec("openssl genrsa -out "+ priv + pb.getUid() + ".key 1024");
            process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "sh " + scripts + "CA.sh -newreq " + subject + " " + pb.getUid() + " " + scripts + "psw"});

            process.waitFor();
        } catch (IOException ex) {
            log.err(false, "Errore di IO", ex.toString(), ex.toString());
        } catch (InterruptedException ex) {
            log.err(false, "Errore nel waitFor", ex.toString(), ex.toString());
        }


        try {
            log.info(false, "Creazione certificato", "Gcomando firma", "comando: sh " + scripts + "CA.sh -sign " + pb.getUid() + "");

            process2 = Runtime.getRuntime().exec(new String[]{"bash", "-c", "sh " + scripts + "CA.sh -sign " + pb.getUid() + ""});

            process2.waitFor();
      } catch (IOException ex) {
            log.err(false, "Errore di IO", ex.toString(), ex.toString());
        } catch (InterruptedException ex) {
            log.err(false, "Errore nel waitFor", ex.toString(), ex.toString());
        }

        return true;
    }

    public static ArrayList<UserCert> getCertificateUser(String username) throws CertificateException {
        ArrayList<UserCert> list = new ArrayList<UserCert>();
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
                        if (tmp1.nextToken().equals(username)) {
                            //System.out.println("questo è il certificato di "+username+": "+files[i].getName());
                            ue = new UserCert();
                            ue.setNameFile(files[i].getName());
                            ue.setSerial("" + cer.getSerialNumber());
//                            int j = cer.getNotBefore().compareTo(Calendar.getInstance().getTime());
//                            int k = Calendar.getInstance().getTime().compareTo(cer.getNotAfter());
//                            if (j <= 0 && k <= 0) {
//                                ue.setValidity("Valid");
//                            } else {
//                                ue.setValidity("Invalid");
//                            }

                            ue = getIndexInfo(ue);

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
        System.out.println("fine del cerificateUser");

        Collections.sort(list, new Comparator<UserCert>() {

            @Override
            public int compare(UserCert o1, UserCert o2) {
                if (Integer.parseInt(o1.getSerial()) < Integer.parseInt(o2.getSerial())) {
                    return -1;
                } else if (Integer.parseInt(o1.getSerial()) > Integer.parseInt(o2.getSerial())) {
                    return 1;
                }
                return 0;
            }
        });

        return list;
    }

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
                dateE=date.toString();
                
                String dateR = "Not Revoked";
                if (ver.equals("R")) {
                    dateR = tok.nextToken();
                    date = new StringBuilder();
                    anno = Integer.parseInt(dateR.substring(0, 2)) + 2000;
                    date.append(anno).append("/").append(dateR.substring(2, 4)).append("/").append(dateR.substring(4, 6)).append(" ").append(dateR.substring(6, 8)).append(":").append(dateR.substring(8, 10)).append(":").append(dateR.substring(10, 12));
                    dateR=date.toString();
                }

                if (tok.nextToken().equals(Integer.toHexString(Integer.parseInt(ue.getSerial())))){
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
        System.out.println("fine del getIndexInfo");

        return ue;
    }
    
    public static void pkcs12Certificate(UserCert userCert){
        
        Process process = null;
        
         try {
            log.info(false, "Creazione certificato", "verifica ","comando: sh " + scripts + "CA.sh -pkcs12 "+userCert.getNameFile()+" "+userCert.getNameFile().replace(".pem", ""));

            process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "sh " + scripts + "CA.sh -pkcs12 "+userCert.getNameFile() +" "+userCert.getNameFile().replace(".pem", "")+" "+userCert.getPasswordPkcs12()+" "+userCert.getPasswordKey()});
            
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

            process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "rm " + directory + "/newcerts/" + selectedUserCert.getNameFile()});
//            process2 = Runtime.getRuntime().exec(new String[]{"bash", "-c", "echo sh " + scripts + "CA.sh -revoke "+directory+"/newcerts/"+nomeFile+" >> "+scripts+"error"});
            process.waitFor();
//            process2.waitFor();
        } catch (IOException ex) {
            log.err(false, "Errore di IO", ex.toString(), ex.toString());
        } catch (InterruptedException ex) {
            log.err(false, "Errore nel waitFor", ex.toString(), ex.toString());
        }
    }
}
