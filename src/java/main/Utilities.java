/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import utils.IMoviesLogger;

/**
 *
 * @author nicolo
 */
public class Utilities {

    private static IMoviesLogger log = new IMoviesLogger("main.Utilities");
    private static final String priv = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//certs//private//")+"/";
    private static final String cert = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//certs//cert//")+"/";

    public static boolean createCertificate(Persona pb) throws InterruptedException {

        log.info(false, "Creazione certificato", "Entrata nel costruttore di Utilities", "Entrata nel costruttore di Utilities");

//        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
//        keyGen.initialize(1024, new SecureRandom());
//        KeyPair keypair = keyGen.generateKeyPair();
//        PrivateKey privKey = keypair.getPrivate();
//        PublicKey pubKey = keypair.getPublic();
//
//        Calendar expiry = Calendar.getInstance();
//        expiry.add(Calendar.DAY_OF_YEAR, 365);

        String line;
        String subject = "\"/C=IT/ST=Italy/L=Verona/O=iMovies Certificate Authority/"
                + "OU=iMovies Security Department/CN=iSD/"
                + "emailAddress=" + pb.getEmail() + "/SN=" + pb.getLastname() + ""
                + "/GN=" + pb.getFirstname() + "\"";
        //subject = subject.replace(" ", "\\ ");

        OutputStream stdin;
//                process.getOutputStream();
        InputStream stderr;
//                process.getErrorStream();
        InputStream stdout;
//                process.getInputStream();

        Scanner scan = new Scanner(System.in);

        //ProcessBuilder builder = new ProcessBuilder("openssl genrsa -out "+pb.getUid()+".key 1024");
        //builder.redirectErrorStream(true);
        Process process = null;
        try {
            log.info(false, "Creazione certificato", "Generazione file con chiavi", "Generazione file con chiavi su directory 'private': "+priv);
            process = Runtime.getRuntime().exec("openssl genrsa -out "+ priv + pb.getUid() + ".key 1024");
        } catch (IOException ex) {
            log.err(false, "Errore di IO", ex.toString(), ex.toString());
        }
//        try {
//            process.waitFor();
//
//            //stdout = process.getInputStream();
//        } catch (InterruptedException ex) {
//            log.err(false, "Errore nel waitFor", ex.toString(), ex.toString());
//        }


//        builder = new ProcessBuilder("openssl req -new -key "+pb.getUid()+".key -out "+pb.getUid()+".csr -subj "+subject);
//        builder.redirectErrorStream(true);
        Process process1 = null;
        
        try {
            log.info(false, "Creazione certificato", "Generazione file csr", "Generazione file csr su directory 'private': "+priv+" e directory 'cert': "+cert+". Lancio comando "
                    + "openssl req -new -key "+ priv + pb.getUid() + ".key -out "+ cert + pb.getUid() + ".csr -subj " + subject);
            process1 = Runtime.getRuntime().exec("openssl req -new -key "+ priv + pb.getUid() + ".key -out "+ cert + pb.getUid() + ".csr -subj " + subject + "");
            process1.waitFor();
        } catch (IOException ex) {
            log.err(false, "Errore di IO", ex.toString(), ex.toString());
        } catch (InterruptedException e) {
            log.err(false, "Errore nel waitFor", e.toString(), e.toString());
        }
        
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
}
