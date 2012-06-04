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
//            FileOutputStream file2  = new FileOutputStream(FacesContext.getCurrentInstance().getExternalContext().getRealPath("//scripts//") + "/cmd");
//            Output = new PrintStream(file2);
//            Output.println("sh " + scripts + "CA.sh -newreq " + subject + " " + pb.getUid() + " " + scripts + "psw");
////            FileOutputStream file3  = new FileOutputStream(FacesContext.getCurrentInstance().getExternalContext().getRealPath("//scripts//") + "/wmi");
////            Output = new PrintStream(filew);
////            Output.println("sh " + scripts + "CA.sh -newreq " + subject + " " + pb.getUid() + " " + scripts + "psw");
        } catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        log.info(false, "Creazione certificato", "Entrata nel costruttore di Utilities", "Entrata nel costruttore di Utilities");

//        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
//        keyGen.initialize(1024, new SecureRandom());
//        KeyPair keypair = keyGen.generateKeyPair();
//        PrivateKey privKey = keypair.getPrivate();
//        PublicKey pubKey = keypair.getPublic();
//
//        Calendar expiry = Calendar.getInstance();
//        expiry.add(Calendar.DAY_OF_YEAR, 365);



//        OutputStream stdin;
////                process.getOutputStream();
//        InputStream stderr;
////                process.getErrorStream();
//        InputStream stdout;
////                process.getInputStream();

        Scanner scan = new Scanner(System.in);

        //ProcessBuilder builder = new ProcessBuilder("openssl genrsa -out "+pb.getUid()+".key 1024");
        //builder.redirectErrorStream(true);
        Process process = null;
        Process process2 = null;
        Process process3 = null;

        try {
            log.info(false, "Creazione certificato", "Generazione file con chiavi", "Generazione csr in cartella, questa è la cartella scripts: " + scripts);
            log.info(false, "Creazione certificato", "Generazione file con chiavi", "comando: sh " + scripts + "CA.sh -newreq " + subject + " " + pb.getUid() + " " + scripts + "psw");

            //            process = Runtime.getRuntime().exec("openssl genrsa -out "+ priv + pb.getUid() + ".key 1024");
            process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "sh " + scripts + "CA.sh -newreq " + subject + " " + pb.getUid() + " " + scripts + "psw"});

            process.waitFor();
//            process2=Runtime.getRuntime().exec(new String[]{"bash", "-c","pwd >> "+scripts+"wmi"});
//            process2.waitFor();
//            
//process = Runtime.getRuntime().exec(new String[]{"bash","-c", "mv "+pb.getUid()+".key "+priv});
        } catch (IOException ex) {
            log.err(false, "Errore di IO", ex.toString(), ex.toString());
        } catch (InterruptedException ex) {
            log.err(false, "Errore nel waitFor", ex.toString(), ex.toString());
        }


        try {
//            log.info(false, "Creazione certificato", "Generazione file con chiavi", "Generazione csr in cartella, questa è la cartella scripts: " + scripts);
            log.info(false, "Creazione certificato", "Gcomando firma", "comando: sh " + scripts + "CA.sh -sign " + cert + pb.getUid() + ".csr " + pb.getUid() + "");

            //            process = Runtime.getRuntime().exec("openssl genrsa -out "+ priv + pb.getUid() + ".key 1024");
            process2 = Runtime.getRuntime().exec(new String[]{"bash", "-c", "sh " + scripts + "CA.sh -sign " + cert + pb.getUid() + ".csr " + pb.getUid() + ""});

            process2.waitFor();
//            process2=Runtime.getRuntime().exec(new String[]{"bash", "-c","pwd >> "+scripts+"wmi"});
//            process2.waitFor();
//            
//process = Runtime.getRuntime().exec(new String[]{"bash","-c", "mv "+pb.getUid()+".key "+priv});
        } catch (IOException ex) {
            log.err(false, "Errore di IO", ex.toString(), ex.toString());
        } catch (InterruptedException ex) {
            log.err(false, "Errore nel waitFor", ex.toString(), ex.toString());
        }

//        try {
////            log.info(false, "Creazione certificato", "Generazione file con chiavi", "Generazione csr in cartella, questa è la cartella scripts: " + scripts);
//            log.info(false, "Creazione certificato", "verifica ","comando: sh " + scripts + "CA.sh -verify "+cert+pb.getUid()+"_.pem >> "+scripts+"verify");
//
//            //            process = Runtime.getRuntime().exec("openssl genrsa -out "+ priv + pb.getUid() + ".key 1024");
//            process3 = Runtime.getRuntime().exec(new String[]{"bash", "-c", "sh " + scripts + "CA.sh -sign "+cert+pb.getUid()+".csr "+pb.getUid()+""});
//            
//            process3.waitFor();
////            process2=Runtime.getRuntime().exec(new String[]{"bash", "-c","pwd >> "+scripts+"wmi"});
////            process2.waitFor();
////            
////process = Runtime.getRuntime().exec(new String[]{"bash","-c", "mv "+pb.getUid()+".key "+priv});
//        } catch (IOException ex) {
//            log.err(false, "Errore di IO", ex.toString(), ex.toString());
//        } catch (InterruptedException ex) {
//            log.err(false, "Errore nel waitFor", ex.toString(), ex.toString());
//        }




//        builder = new ProcessBuilder("openssl req -new -key "+pb.getUid()+".key -out "+pb.getUid()+".csr -subj "+subject);
//        builder.redirectErrorStream(true);
//        Process process1 = null;
//        
//        try {
//            log.info(false, "Creazione certificato", "Generazione file csr", "Generazione file csr su directory 'private': "+priv+" e directory 'cert': "+cert+". Lancio comando "
//                    + "openssl req -new -key "+ priv + pb.getUid() + ".key -out "+ cert + pb.getUid() + ".csr -subj " + subject);
//            process1 = Runtime.getRuntime().exec(new String[]{"bash","-c","openssl req -new -key "+ priv + pb.getUid() + ".key -out "+ cert + pb.getUid() + ".csr -subj " + subject + ""});
//            process1.waitFor();
//        } catch (IOException ex) {
//            log.err(false, "Errore di IO", ex.toString(), ex.toString());
//        } catch (InterruptedException e) {
//            log.err(false, "Errore nel waitFor", e.toString(), e.toString());
//        }
//        
//                Process process2 = null;
//
//        try {
//            log.info(false, "Firma Certificato", "Generazione file pem", "Generazione file pem su directory 'private': "+priv+" e directory 'cert': "+cert+". Lancio comando "
//                    + "openssl ca -in "+ cert + pb.getUid() + ".csr -config /etc/ssl/openssl.cnf");
//            process2 = Runtime.getRuntime().exec(new String[]{"bash","-c","pwd >> percorso.txt"});
//                        process2.waitFor();
//
//            process2 = Runtime.getRuntime().exec(new String[]{"bash","-c","openssl ca -in "+ cert + pb.getUid() + ".csr -config //etc//ssl//openssl.cnf"});
//            process2.waitFor();
//        } catch (IOException ex) {
//            log.err(false, "Errore di IO", ex.toString(), ex.toString());
//        } catch (InterruptedException e) {
//            log.err(false, "Errore nel waitFor", e.toString(), e.toString());
//        }
//        
        //stdout = process.getInputStream();
        //stdout = process.getInputStream();

        //builder = new ProcessBuilder("openssl x509 -req -days 365 -in "+pb.getUid()+".csr -signkey "+pb.getUid()+".key -out "+pb.getUid()+".crt");
        //builder.redirectErrorStream(true);
        //process = builder.start();
/*
         * try { //process = Runtime.getRuntime().exec("openssl x509 -req -days
         * 365 -in " + pb.getUid() + ".csr -signkey " + pb.getUid() + ".key -out
         * " + pb.getUid() + ".crt"); } catch (IOException ex) { log.err(false,
         * "Errore di IO", ex.toString(), ex.toString()); } try {
         * process.waitFor();
         *
         * //stdout = process.getInputStream(); } catch (InterruptedException
         * ex) { log.err(false, "Errore nel waitFor", ex.toString(),
         * ex.toString()); }
         */
        //stdout = process.getInputStream();


//        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
//
//        String input = scan.nextLine();
//        input += "\n";
//        writer.write(input);
//        writer.flush();
//
//        input = scan.nextLine();
//        input += "\n";
//        writer.write(input);
//        writer.flush();
//        while (scan.hasNext()) {
//            String in = scan.nextLine();
//            if (in.trim().equals("exit")) {
//                // Putting 'exit' amongst the echo --EOF--s below doesn't work.
//                writer.write("exit\n");
//            } else {
//                writer.write("((" + in + ") && echo --EOF--) || echo --EOF--\n");
//            }
//            writer.flush();
//
//            line = reader.readLine();
//            while (line != null && !line.trim().equals("--EOF--")) {
//                System.out.println("Stdout: " + line);
//                line = reader.readLine();
//            }
//            if (line == null) {
//                break;
//            }
//        }

//        while ((line = reader.readLine()) != null) {
//            System.out.println("Stdout: " + line);
//        }
//
//        input = scan.nextLine();
//        input += "\n";
//        writer.write(input);
//        writer.close();
//
//        while ((line = reader.readLine()) != null) {
//            System.out.println("Stdout: " + line);
//
//        }


//        CertificateFactory cf = CertificateFactory.getInstance("X509");
//			X509Certificate c = (X509Certificate) cf;
//                        
//                                CertificateGenerator n;
//                        n.

//        X509Name x509Name = new X509Name("CN=" + dn);
//
//        V3TBSCertificateGenerator certGen = new V3TBSCertificateGenerator();
//        certGen.setSerialNumber(new DERInteger(BigInteger.valueOf(System.currentTimeMillis())));
//        certGen.setIssuer(PrincipalUtil.getSubjectX509Principal(caCert));
//        certGen.setSubject(x509Name);
//        DERObjectIdentifier sigOID = X509Util.getAlgorithmOID("SHA1WithRSAEncryption");
//        AlgorithmIdentifier sigAlgId = new AlgorithmIdentifier(sigOID, new DERNull());
//        certGen.setSignature(sigAlgId);
//        certGen.setSubjectPublicKeyInfo(new SubjectPublicKeyInfo((ASN1Sequence)new ASN1InputStream(
//                new ByteArrayInputStream(pubKey.getEncoded())).readObject()));
//        certGen.setStartDate(new Time(new Date(System.currentTimeMillis())));
//        certGen.setEndDate(new Time(expiry.getTime()));
//        TBSCertificateStructure tbsCert = certGen.generateTBSCertificate();


        return true;
    }

    public static ArrayList<UserCert> getCertificateUser(String username) throws CertificateException {
        ArrayList<UserCert> list = new ArrayList<UserCert>();
        File cartella = new File("/etc/ssl/CA_iMovies/newcerts");
        File[] files = null;
        StringTokenizer tmp, tmp1;
        String mom;
        UserCert ue = null;
        System.out.println(cartella.getAbsolutePath());
        if (cartella.isDirectory()) {
            files = cartella.listFiles();
            for (int i=0;i<files.length;i++){
                        System.out.println(files[i].toString());
            }
        }
        for (int i = 0; i < files.length; i++) {
            try {
                System.out.println(files[i].getAbsoluteFile().toString());
                FileInputStream in = new FileInputStream(files[i].getAbsoluteFile());
                CertificateFactory cf =CertificateFactory.getInstance("X509");
                X509Certificate cer = (X509Certificate) cf.generateCertificate(in);
                mom = cer.getSubjectDN().getName();
                mom = mom.trim();
                mom=mom.replace(" ", "");
                tmp = new StringTokenizer(mom, ",");
                System.out.println("stringa trimmata: "+mom);
                while (tmp.hasMoreTokens()) {
                    System.out.println("qeusto è il token delle virgole: "+tmp);

                    tmp1 = new StringTokenizer(tmp.nextToken(), "=");
                    System.out.println("qeusto è il token tokenizzato di nuovo: "+tmp1.toString());
                    if (tmp1.nextToken().equals("CN")) {
                        if (tmp1.nextToken().equals(username)) {
                            System.out.println("questo è il certificato di "+username+": "+files[i].getName());
                            ue=new UserCert();
                            ue.setNameFile(files[i].getName());
                            ue.setSerial(cer.getSerialNumber().toString());
                            int j=cer.getNotBefore().compareTo(Calendar.getInstance().getTime());
                            int k=Calendar.getInstance().getTime().compareTo(cer.getNotAfter());
                            if(j<=0 && k<=0){
                                ue.setValidity("Valid");
                            }else{
                                ue.setValidity("Invalid");
                            }
                            list.add(ue);

                            break;
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

        return list;
    }
}
