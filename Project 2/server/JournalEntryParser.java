package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

public class JournalEntryParser { 
    private File file; //The format of the record filelines is: recordID;date;Patient;Doctor;Nurse;Division
    private FileReader reader;

    public JournalEntryParser(String file) throws FileNotFoundException {
        this.reader = new FileReader(file);
        this.file = new File(file);
    }

    public static void main(String[] args) {
        try {
            JournalEntryParser parser = new JournalEntryParser("./database/journal.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<JournalEntry>> read() {
        return null;
    }

    public void write() {

    }

}