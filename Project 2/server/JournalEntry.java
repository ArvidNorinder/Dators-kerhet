package server;

public class JournalEntry {
    private String date;
    private String doctor;
    private String nurse; 
    private String division;
    private String patientID;

    public JournalEntry(String date, String doc, String nurse, String div, String id, String patientID) {
        this.date = date;
        this.doctor = doc;
        this.nurse = nurse;
        this.division = div;
        this.patientID = patientID;
      }


    public String getDate() {
        return date;
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

    public String getpatientID() {
        return patientID; 
    }
}
