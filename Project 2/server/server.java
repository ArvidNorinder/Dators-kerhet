package server;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import javax.net.*;
import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map;

public class server implements Runnable {
  private ServerSocket serverSocket = null;
  private static int numConnectedClients = 0;
  private List<User> users;
  private Map<String, List<JournalEntry>> journalEntries;
  private PermissionHandler permissionHandler = new PermissionHandler(new Log("database/log.txt"));

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
    UserParser parser = new UserParser("database/users.txt");
    users = parser.read();
  }

  public void updateFromDataBase() throws FileNotFoundException  {
    readJournals();
    readEmployees();
  }

  private void getRecordsForPatient (String patient) {
    List<JournalEntry> entries = journalEntries.get(patient);
    if (entries != null) {
      for (JournalEntry entry : entries) {
        System.out.println(entry.toString());
      }
    }
  }

  public String getCurrentDate () {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
    LocalDateTime timeNow = LocalDateTime.now();
    String formatDateTime = timeNow.format(formatter);
    String[] date = formatDateTime.split(" ");
    return date[0];
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

      System.out.println("test " + serialNumberString);
      User us = certificateToUserMap.get(serialNumberString);
    //TODO: Handle client requests below
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

      //Create file object to read from journal entries
      readJournals();
      readEmployees();
      

      do { //TODO: Remember to close on other end after done.
      clientMsg = in.readLine();
      List<JournalEntry> entriesToReturn = permissionHandler.readPatientJournal(us, journalEntries.get(clientMsg));
      System.out.println("hello");
      System.out.println(entriesToReturn.size());


      for(JournalEntry e: entriesToReturn) {
        out.println(e.toString()); 
      }
      out.println("end");
      out.flush();

        clientMsg = in.readLine();
        System.out.println("Message recieved form client: " + clientMsg);
        String[] msgParts = clientMsg.split(",");

        if(msgParts[0].equals("r")) {
          for (JournalEntry e : entriesToReturn) {
            if (e.toString().equals(msgParts[1])) {
              out.println(e.getInfo());
              //TODO: Add end?
            }
          }
        } else if(msgParts[0].equals("e")) {
          for (JournalEntry e : entriesToReturn) {
            if (e.toString().equals(msgParts[1])) {
              if (permissionHandler.canEdit(us, e)) {
                String oldInfo = e.getInfo();
                JournalEntry updatedJournal = new JournalEntry(e.getPatientID(), e.getDoctor(), e.getNurse(), e.getDivision(), e.getDate(), oldInfo + msgParts[2]);
                JournalEntryParser parser = new JournalEntryParser("database/journalEntries.txt");
                parser.write(updatedJournal);
                updateFromDataBase();
                System.out.println("successfully edited journal entry"); 
              } else {
                System.out.println("you do not have permission to edit this journal entry");
              }
            }
          }
        }else if(msgParts[0].equals("d")) {
          for (JournalEntry e: entriesToReturn) {
            if (e.toString().equals(msgParts[1])) {
              if (permissionHandler.canDelete(us, e)) {
                JournalEntryParser parser = new JournalEntryParser("database/journalEntries.txt");
                parser.deleteJournalEntryFromFile(e);
                updateFromDataBase();
                System.out.println("successfully deleted journal entry");
              } else {
                System.out.println("you do not have permission to delete this journal entry");
              }
            }
          }
        } else if(msgParts[0].equals("c")) {
          String[] tempName = msgParts[1].split(";");
          for (User u : users) {
            if (u.getID().equals(tempName[0])) {
              if (permissionHandler.canCreate(us, u)) {
                JournalEntryParser parser = new JournalEntryParser("database/journalEntries.txt");
                parser.write(new JournalEntry(tempName[0], tempName[1], tempName[2], tempName[3], getCurrentDate(), msgParts[2]));
                updateFromDataBase();
                System.out.println("successfully created journal entry");
              } else {
                System.out.println("you do not have permission to create a journal entry for this patient");
              }
            }
          }
        }
      } while(clientMsg != null);


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
    int port = 9876;
    if (args.length >= 1) {
      //port = Integer.parseInt(args[0]);
      port = 9876;
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
