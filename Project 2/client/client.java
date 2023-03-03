package client;
import java.net.*;
import java.io.*;
import java.math.BigInteger;

import javax.net.ssl.*;
import javax.swing.text.html.parser.Entity;
import javax.xml.namespace.QName;

import java.security.cert.X509Certificate;
import java.security.KeyStore;
import java.security.cert.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

/*
 * This example shows how to set up a key manager to perform client
 * authentication.
 *
 * This program assumes that the client is not inside a firewall.
 * The application can be modified to connect to a server outside
 * the firewall by following SSLSocketClientWithTunneling.java.
 */

public class client {
  public static void main(String[] args) throws Exception {

    //TODO: Ask user for user name
    System.out.println("Enter username.");

    Scanner scan = new Scanner(System.in);
    String userName = scan.nextLine();
    
    String host = null;
    int port = 9876;
    /*for (int i = 0; i < args.length; i++) {
      System.out.println("args[" + i + "] = " + args[i]);
    }
    if (args.length < 2) {
      System.out.println("USAGE: java client host port");
      System.exit(-1);
    }*/
    try { /* get input parameters */
      host = "localhost";
      port = 9876;
    } catch (IllegalArgumentException e) {
      System.out.println("USAGE: java client host port");
      System.exit(-1);
    }

    try {
      SSLSocketFactory factory = null;
      try {
        char[] password = "password".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        KeyStore ts = KeyStore.getInstance("JKS");
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        // keystore password (storepass)

        //TODO: Load in keystore depending on user name instead.
        ks.load(new FileInputStream(userName + "keystore"), password);  
        // truststore password (storepass)
        ts.load(new FileInputStream("clienttruststore"), password); 
        kmf.init(ks, password); // user password (keypass)
        tmf.init(ts); // keystore can be used as truststore here
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        factory = ctx.getSocketFactory();
      } catch (Exception e) {
        throw new IOException(e.getMessage());
      }
      SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
      System.out.println("\nsocket before handshake:\n" + socket + "\n");

      /*
       * send http request
       *
       * See SSLSocketClient.java for more information about why
       * there is a forced handshake here when using PrintWriters.
       */

      socket.startHandshake();
      SSLSession session = socket.getSession();
      Certificate[] cert = session.getPeerCertificates();
      String subject = ((X509Certificate) cert[0]).getSubjectX500Principal().getName();
      System.out.println("certificate name (subject DN field) on certificate received from server:\n" + subject + "\n");
      
      String issuer = ((X509Certificate) cert[0]).getIssuerX500Principal().getName();
      System.out.println("Issuer name (issuer DN field) on certificate received from server:\n" + issuer + "\n");
      
      byte[] serialNumber = ((X509Certificate) cert[0]).getSerialNumber().toByteArray();
      String serialNumberString = new BigInteger(serialNumber).toString(16);
      System.out.println("Serial number (serial number DN field) on certificate received from server:\n" + serialNumberString + "\n");

      System.out.println("socket after handshake:\n" + socket + "\n");
      System.out.println("secure connection established\n\n");

      //TODO: Efter detta vill vi komma åt filer. Använd kod för att begränsa access frammöver

      //låt användaren  skriva in mer hit, i form av antingen namn eller division.


      //Se om strängen är en divison, annars se om strängen är ett namn

      //Kolla accessrättigheter och ev. printa

      BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String msg;
      for (;;) {
        System.out.println("Enter a patient name to list entries.");
        System.out.print(">");
        msg = read.readLine();
        if (msg.equalsIgnoreCase("quit")) {
          break;
        } 
        
        printEntriesSpecifyRecord(msg, in, out, read);
        
        System.out.println("done");
      }
      in.close();
      out.close();
      read.close();
      socket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void printEntriesSpecifyRecord(String msg, BufferedReader in, PrintWriter out, BufferedReader read) {
    try {
      //Tell server what we want
      out.println(msg);
      out.flush();

      System.out.println("Message we want to send: " + msg);
      //server will respond with filling the buffered reader.
      List<String> entries = new ArrayList<>();
      String line;
      //Here the server only replies with entries that our role has access to.
      
      do {
          line = in.readLine();
          if (line.equals("end"))
            break;
          else if(line != null)
            entries.add(line);
          
      } while(line != null);
      
      for (int i = 0; i < entries.size(); i++) {
        //TODO make sure server does not send text in each line
        System.out.println(entries.get(i));
      }

      String index;
      String answer;

      System.out.println("Select an entry out of the printed, numbered in asceding order going down. " + entries.size() + " entries were printed.");
      //Let user specify one entry in range of structure length
      index = read.readLine();
      //Ask if display or edit
      System.out.println("Read, edit, delete or create entry? (r/e/d/c)");
      //TODO: Let doctors create new entry
      answer = read.readLine();
      if(answer.equalsIgnoreCase("r")) {
        out.println("r," +  entries.get(Integer.parseInt(index) - 1) + ",");
        out.flush();
        System.out.println(in.readLine());
      } else if (answer.equalsIgnoreCase("e")) {
        System.out.println("Enter additional content: ");
        answer = read.readLine();
        out.println("e," + entries.get(Integer.parseInt(index) - 1) + "," + answer);
        out.flush();
      } else if (answer.equalsIgnoreCase("d")) {
        out.println("d," +  entries.get(Integer.parseInt(index) - 1) + ",");
        out.flush();
      } else if (answer.equalsIgnoreCase("c")) {
        System.out.println("Enter patient name: ");
        String patientName = read.readLine();

        System.out.println("Enter doctor name: ");
        String doctorName = read.readLine();

        System.out.println("Enter nurse name: ");
        String nurseName = read.readLine();

        System.out.println("Enter information: ");
        String information = read.readLine();

        out.println("c," + patientName + ";" + doctorName + ";" + nurseName + ";"  + ";" + information + ",");
        out.flush();
      }
    } catch (IOException e) {

    }
  }
}
