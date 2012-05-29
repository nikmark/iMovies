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

/**
 *
 * @author nicolo
 */
public class Utilities {

    public static boolean createCertificate(Persona pb) throws InterruptedException, IOException {

//        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
//        keyGen.initialize(1024, new SecureRandom());
//        KeyPair keypair = keyGen.generateKeyPair();
//        PrivateKey privKey = keypair.getPrivate();
//        PublicKey pubKey = keypair.getPublic();
//
//        Calendar expiry = Calendar.getInstance();
//        expiry.add(Calendar.DAY_OF_YEAR, 365);

        String line;
        String subject="/C=IT/ST=Italy/L=Verona/O=iMovies Certificate Authority/"
                + "OU=iMovies Security Department/CN=iSD/"
                + "emailAddress="+pb.getEmail()+"/SN="+pb.getLastname()+""
                        + "/GN="+pb.getFirstname()+"";
        subject = subject.replaceAll(" ", "\\ ");

        OutputStream stdin;
//                process.getOutputStream();
        InputStream stderr;
//                process.getErrorStream();
        InputStream stdout;
//                process.getInputStream();

        Scanner scan = new Scanner(System.in);

        //ProcessBuilder builder = new ProcessBuilder("openssl genrsa -out "+pb.getUid()+".key 1024");
        //builder.redirectErrorStream(true);
        Process process = Runtime.getRuntime().exec("openssl genrsa -out "+pb.getUid()+".key 1024");
        process.waitFor();
        
        //stdout = process.getInputStream();
        
        
//        builder = new ProcessBuilder("openssl req -new -key "+pb.getUid()+".key -out "+pb.getUid()+".csr -subj "+subject);
//        builder.redirectErrorStream(true);
        process = Runtime.getRuntime().exec("openssl req -new -key "+pb.getUid()+".key -out "+pb.getUid()+".csr -subj "+subject);
        process.waitFor();
        //stdout = process.getInputStream();
        
        //builder = new ProcessBuilder("openssl x509 -req -days 365 -in "+pb.getUid()+".csr -signkey "+pb.getUid()+".key -out "+pb.getUid()+".crt");
        //builder.redirectErrorStream(true);
        //process = builder.start();
process = Runtime.getRuntime().exec("openssl x509 -req -days 365 -in "+pb.getUid()+".csr -signkey "+pb.getUid()+".key -out "+pb.getUid()+".crt");
        process.waitFor();
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
