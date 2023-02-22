package server;

import java.util.HashSet;
import java.util.Set;

public class User {
    private String id; //Is a number combination
    private String role; 
    private String division;
    Set<String> patients;

    public User(String id, String role, String div) {
        this.id = id;
        this.role = role;
        division = div;
        patients = new HashSet<String>();
    }

    public User(String id, String role) {
        this.id = id; 
        this.role = role;
        division = null;
        patients = null;
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

   @Override
   public boolean equals(Object o){
    User p2 = (User) o;
    return id.equals(p2.getID());
    }
}
