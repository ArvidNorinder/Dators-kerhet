package server;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import javax.net.*;
import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map;

public class server implements Runnable {
  private ServerSocket serverSocket = null;
  private static int numConnectedClients = 0;
  private List<User> users;
  private Map<String , List<JournalEntry>> patients;
  private Map<String, List<JournalEntry>> journalEntries;

  private Map<String, User> certificateToUserMap = new HashMap<>();

  //TODO: A function that returns a list of all the patients that a user is allowed to see
  //TODO: A function that returns a list of all the patients that a user is allowed to edit
  //TODO: A function that handles messages from the client (i.e. what to do when a client sends a message to the server)
  //TODO: main function that starts the server


  public server(ServerSocket ss) throws IOException {
    serverSocket = ss;
    newListener();
    certificateToUserMap.put("76ce524f180f52b2e9fe13e1e06b935aee0aa522", new User("Government", "government"));
    certificateToUserMap.put("76ce524f180f52b2e9fe13e1e06b935aee0aa523", new User("Sofia", "doctor", "division1"));
    certificateToUserMap.put("76ce524f180f52b2e9fe13e1e06b935aee0aa524", new User("Oscar", "nurse", "division1"));
    certificateToUserMap.put("76ce524f180f52b2e9fe13e1e06b935aee0aa525", new User("Arvid", "patient"));
  }

  public void readJournals() throws FileNotFoundException {
    JournalEntryParser parser = new JournalEntryParser("database/journalEntries.txt");
    journalEntries = parser.read();
  }
  
  public void readEmployees() {
    UserParser parser = new UserParser("database/employees.txt");
    users = parser.read();
  }

  private void getRecordsForPatient (String patient) {
    List<JournalEntry> entries = journalEntries.get(patient);
    for (JournalEntry entry : entries) {
      System.out.println(entry.toString());
    }
  }

  private void getAllowedRecords () {

  }

  public void run() {
    try {
      SSLSocket socket=(SSLSocket)serverSocket.accept();
      newListener();
      SSLSession session = socket.getSession();
      Certificate[] cert = session.getPeerCertificates();
      String subject = ((X509Certificate) cert[0]).getSubjectX500Principal().getName();
      numConnectedClients++;
      System.out.println("client connected");
      System.out.println("client name (cert subject DN field): " + subject);
      
      String issuer = ((X509Certificate) cert[0]).getIssuerX500Principal().getName();
      System.out.println("Issuer name (issuer DN field) on certificate received from server:\n" + issuer + "\n");

      byte[] serialNumber = ((X509Certificate) cert[0]).getSerialNumber().toByteArray();
      String serialNumberString = new BigInteger(serialNumber).toString(16);

      //TODO: Use this serial number to map to a user. --> First create a map between users and their serial number.
      System.out.println("Serial number (serial number DN field) on certificate received from server:\n" + serialNumberString + "\n");
      
      System.out.println(numConnectedClients + " concurrent connection(s)\n");

      PrintWriter out = null;
      BufferedReader in = null;
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      String clientMsg = null;

      //TODO: Handle client requests below
      while ((clientMsg = in.readLine()) != null) {
        //first thing we get from the client is either a patient name or 
        //a division name
        /*
          String rev = new StringBuilder(clientMsg).reverse().toString();
          System.out.println("received '" + clientMsg + "' from client");
          System.out.print("sending '" + rev + "' to client...");
          out.println(rev);
          out.flush();
          System.out.println("done\n"); 
         */
        // 
      }
      in.close();
      out.close();
      socket.close();
      numConnectedClients--;
      System.out.println("client disconnected");
      System.out.println(numConnectedClients + " concurrent connection(s)\n");
    } catch (IOException e) {
      System.out.println("Client died: " + e.getMessage());
      e.printStackTrace();
      return;
    }
  }
  
  private void newListener() { (new Thread(this)).start(); } // calls run()

  public static void main(String args[]) {
    System.out.println("\nServer Started\n");
    int port = -1;
    if (args.length >= 1) {
      port = Integer.parseInt(args[0]);
    }
    String type = "TLSv1.2";
    try {
      ServerSocketFactory ssf = getServerSocketFactory(type);
      ServerSocket ss = ssf.createServerSocket(port);
      ((SSLServerSocket)ss).setNeedClientAuth(true); // enables client authentication
      new server(ss);
    } catch (IOException e) {
      System.out.println("Unable to start Server: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static ServerSocketFactory getServerSocketFactory(String type) {
    if (type.equals("TLSv1.2")) {
      SSLServerSocketFactory ssf = null;
      try { // set up key manager to perform server authentication
        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        KeyStore ks = KeyStore.getInstance("JKS");
        KeyStore ts = KeyStore.getInstance("JKS");
        char[] password = "password".toCharArray();
        // keystore password (storepass)
        ks.load(new FileInputStream("serverkeystore"), password);  
        // truststore password (storepass)
        ts.load(new FileInputStream("servertruststore"), password); 
        kmf.init(ks, password); // certificate password (keypass)
        tmf.init(ts);  // possible to use keystore as truststore here
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        ssf = ctx.getServerSocketFactory();
        return ssf;
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      return ServerSocketFactory.getDefault();
    }
    return null;
  }
}
