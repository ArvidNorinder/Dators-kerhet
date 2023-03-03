package server;

import java.util.ArrayList;
import java.util.List;

public class PermissionHandler {
    private Log logger;
    
    public PermissionHandler(Log logger) {
        this.logger = logger;
    }

    public List<JournalEntry> readPatientJournal (User user, List<JournalEntry> entries) {
        String currentPatient = entries.get(0).getPatientID();

        System.out.println(user.isGovernment());
        if (user.isGovernment()) {
            logger.log("Accessed records of" + currentPatient , user);
            return entries;
        }

        if (user.isPatient()) {
            if (currentPatient.equals(user.getID())) {
                logger.log(currentPatient + "Accessed their records", user);
                return entries;
            } else {
                logger.log("Tried to access the journal of patient " + currentPatient + "but was denied", user);
                return new ArrayList<JournalEntry>();
            }
        }
        

        List<JournalEntry> allowedEntries = new ArrayList<JournalEntry>();

        for (JournalEntry entry: entries) {
            if (user.getDivision().equals(entry.getDivision())) {
                logger.log("Accessed record of " + entry.getPatientID(), user);
                allowedEntries.add(entry);
            }
        }

        return allowedEntries;

    }

    public boolean canRead (User user, JournalEntry entry) {
        if (user.isGovernment()) {
            logger.log("Accessed record of " + entry.getPatientID(), user);
            return true;
        } else if (user.isPatient()) {
            if (user.getID().equals(entry.getPatientID())) {
                logger.log("Accessed their own records", user);
            } else {
                logger.log("Tried to access patient " + entry.getPatientID() + " records but was denied", user);
            }
            return user.getID().equals(entry.getPatientID());
        } else {
            if (user.getDivision().equals(entry.getDivision())) {
                logger.log("Accessed record of " + entry.getPatientID(), user);
            } else {
                logger.log("Tried to access patient" + entry.getPatientID() + " records but was denied", user);
            }
            return user.getDivision().equals(entry.getDivision());
        }


    }

    public boolean canEdit (User user, JournalEntry entry) {
       if (user.isDoctor() || user.isNurse()) {
            if (user.getDivision().equals(entry.getDivision())) {
                logger.log("Edited record of " + entry.getPatientID(), user);
                return user.getDivision().equals(entry.getDivision());
            }
        } else {
            logger.log("Tried to edit patient" + entry.getPatientID() + "records but was denied", user);
            return false;
        }
        return false;
    }
    

    public boolean canCreate (User user1, User user2) {
        if (user1.isDoctor() && user1.hasPatient(user2.getID())) {
            logger.log("Created record of " + user2.getID(), user1);
            return true;
        }
        logger.log("Tried to create a record for patient" + user2.getID() + " but was denied", user1);
        return false;
    }

    public boolean canDelete (User user, JournalEntry entry) {
        if (user.isGovernment()) {
            logger.log("Deleted record for " + entry.getPatientID() , user);
            return true;
        }
        logger.log("Tried to delete patient " +entry.getPatientID() + "records but was denied", user);
        return false;
    }
}
    
