package server;

public class JournalEntry {
    private String date;
    private String doctor;
    private String nurse; 
    private String division;
    private String patientID;

    public JournalEntry( String patientID, String doc, String nurse, String div, String date) {
        this.patientID = patientID;
        this.doctor = doc;
        this.nurse = nurse;
        this.division = div;
        this.date = date;
      }

    public String getpatientID() {
        return patientID; 
    }

    public String getDoctor() {
        return doctor;
    }

    public String getNurse() {
        return nurse;
    }

    public String getDiv() {
        return division;
    }

    public String getDate() {
        return date;
    }
}
