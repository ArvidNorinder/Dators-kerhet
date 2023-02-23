package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class JournalEntryParser { 
    private File fileName; 
    //The format of the record filelines is: Date;Patient;Doctor;Nurse;Division
    //Note: Should the entries contain any information about the patient?


    public JournalEntryParser(String file) throws FileNotFoundException {
        fileName = new File(file);
    }

    public static void main(String[] args) {
        try {
            JournalEntryParser parser = new JournalEntryParser("./database/journalEntries.txt");
            JournalEntry entry = new JournalEntry("Arvid", "Sofia", "Niklas", "division1", "2023:02:23");
            parser.write(entry);
            Map<String, List<JournalEntry>> patientEntries = parser.read();
            System.out.println(patientEntries);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Returns a map with the patients as keys and a list of all journal entries as values
    public Map<String, List<JournalEntry>> read() throws FileNotFoundException { 
        Map<String, List<JournalEntry>> patientEntries = new HashMap<String, List<JournalEntry>>();
        List <JournalEntry> entries = new ArrayList<JournalEntry>();

        Scanner myReader = new Scanner(fileName);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            String[] entry = data.split(";");
            JournalEntry journalEntry = new JournalEntry(entry[0], entry[1], entry[2], entry[3], entry[4]);
            entries.add(journalEntry);
        }
        myReader.close();

        for (JournalEntry entry : entries) {
            if (patientEntries.containsKey(entry.getpatientID())) {
                patientEntries.get(entry.getpatientID()).add(entry);
            } else {
                List<JournalEntry> entryList = new ArrayList<JournalEntry>();
                entryList.add(entry);
                patientEntries.put(entry.getpatientID(), entryList);
            }
        }
        return patientEntries;

        }

    

    public void write(JournalEntry entry) { //Writes a JournalEntry to the file
        try {

            PrintWriter writer = new PrintWriter(new FileWriter(fileName, true));
            writer.append(entry.getpatientID() + ";" + entry.getDoctor() + ";" + entry.getNurse() + ";" + entry.getDiv() + ";" + entry.getDate() + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}