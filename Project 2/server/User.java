package server;

import java.util.HashSet;
import java.util.Set;

public class User {
    private String id; //Is a number combination???
    private String role; 
    private String division;
    Set<String> patients;

    public User(String id, String role, String div) { //For Nurse and Doctor
        this.id = id;
        this.role = role;
        division = div;
        patients = new HashSet<String>();
    }

    public User(String id, String role) { //For Government and Patient
        this.id = id; 
        this.role = role;
        division = null;
        patients = null;
    }

    public boolean isPatient() {
        if (patients == null)
            return true;
        else
            return false;
    }

    public boolean isDoctor() {
        return role.equals("doctor");
    }

    public boolean isNurse() {
        return role.equals("nurse");
    }

    public boolean isGovernment() {
        return role.equals("government");
    }

    public String getID() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getDivision() {
        return division;
    }

    public void addPatient(String patient) {
        patients.add(patient);
    }

    public boolean isDivision(String div) {
        return division.equals(div);
    }

    public boolean isRole(String role) {
        return role.equals(role);
    }

   public boolean hasPatient(String id) {
        return patients.contains(id);
   }

    public String getPatients() {
        String patientList = "";
        if (patients == null) {
            return null;
        }
        for (String patient : patients) {
            patientList += patient + ",";
        }
        return patientList;
    }

   public String getReport (String info) {
    return getRole() + " " + getID() + " in " + getDivision() +  " " + info + " ";
   }

   @Override
   public boolean equals(Object o){
    User p2 = (User) o;
    return id.equals(p2.getID());
    }
}
