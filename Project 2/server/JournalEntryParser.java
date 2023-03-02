package server;

import java.io.BufferedWriter;
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

    public boolean deleteJournalEntry(JournalEntry deleteEntry, List<JournalEntry> journalEntries) {
        try {
            boolean returnValue = false;
            int currentIndex = 0;
            int deleteLineIndex = 0;
            Scanner scanFile = new Scanner(fileName);
            String line;
            List<String> lines = new ArrayList<>();

            while(scanFile.hasNextLine()) {
                line = scanFile.nextLine();
                lines.add(line);
                String[] splitLine = line.split(";");
                if (splitLine[0].equals(deleteEntry.getPatientID()) && 
                    splitLine[1].equals(deleteEntry.getDoctor()) && 
                    splitLine[2].equals(deleteEntry.getNurse()) &&
                    splitLine[3].equals(deleteEntry.getDivision()) &&
                    splitLine[4].equals(deleteEntry.getDate())) {
                        deleteLineIndex = currentIndex;
                        journalEntries.remove(deleteLineIndex);
                        returnValue = true;
                }
                currentIndex++;
            }
            scanFile.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter("journalEntries.txt"));

            for (int i = 0; i <= currentIndex; i++) {
                if(i != deleteLineIndex)
                    writer.write(lines.get(i) + System.getProperty("line.separator"));
            }
            return returnValue;
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            JournalEntryParser parser = new JournalEntryParser("./database/journalEntries.txt");
            JournalEntry entry = new JournalEntry("Arvid", "Sofia", "Niklas", "division1", "2023:02:23", "Arvid är en bra patient");
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
            JournalEntry journalEntry = new JournalEntry(entry[0], entry[1], entry[2], entry[3], entry[4], entry[5]);
            entries.add(journalEntry);
        }
        myReader.close();

        for (JournalEntry entry : entries) {
            if (patientEntries.containsKey(entry.getPatientID())) {
                patientEntries.get(entry.getPatientID()).add(entry);
            } else {
                List<JournalEntry> entryList = new ArrayList<JournalEntry>();
                entryList.add(entry);
                patientEntries.put(entry.getPatientID(), entryList);
            }
        }
        return patientEntries;
    }
  

    public void write(JournalEntry entry) { //Writes a JournalEntry to the file
        try {

            PrintWriter writer = new PrintWriter(new FileWriter(fileName, true));
            writer.append(entry.getPatientID() + ";" + entry.getDoctor() + ";" + entry.getNurse() + ";" + entry.getDivision() + ";" + entry.getDate() + ";" + entry.getInfo() + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}