package server;

import java.util.HashSet;
import java.util.Set;

public class Employee {
    private String id;
    private String role; 
    private String division;
    Set<String> patients;

    public Employee(String id, String role, String div) {
        this.id = id;
        this.role = role;
        division = div;
        patients = new HashSet<String>();
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

}
