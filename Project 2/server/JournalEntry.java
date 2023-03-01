package server;

public class JournalEntry {
    private String date;
    private String doctor;
    private String nurse; 
    private String division;
    private String patientID;
    private String info;

    public JournalEntry( String patientID, String doc, String nurse, String div, String date, String info) {
        this.patientID = patientID;
        this.doctor = doc;
        this.nurse = nurse;
        this.division = div;
        this.date = date;
        this.info = info;
      }

    public String getPatientID() {
        return patientID; 
    }

    public String getDoctor() {
        return doctor;
    }

    public String getNurse() {
        return nurse;
    }

    public String getDivision() {
        return division;
    }

    public String getDate() {
        return date;
    }
    
    public String getInfo() {
        return info;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Patient ID: " + patientID + "\n");
        sb.append(" Doctor: " + doctor + "\n");
        sb.append(" Nurse: " + nurse + "\n");
        sb.append(" Division: " + division + "\n");
        sb.append(" Date: " + date + "\n");
        return sb.toString();
    }
}
