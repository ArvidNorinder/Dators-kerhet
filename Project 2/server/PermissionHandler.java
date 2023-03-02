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
        System.out.println("entries size: " + entries.size());

        if (user.isPatient()) {
            if (currentPatient.equals(user.getID())) {
                logger.log("Accessed their records", user);
                return entries;
            } else {
                logger.log("Tried to access another patient's records but was denied", user);
                return new ArrayList<JournalEntry>();
            }
        }

        if (user.isGovernment()) {
            logger.log("Accessed records", user);
            return entries;
        }

        List<JournalEntry> allowedEntries = new ArrayList<JournalEntry>();

        for (JournalEntry entry: entries) {
            if (user.getDivision().equals(entry.getDivision())) {
                logger.log("Accessed record", user);
                allowedEntries.add(entry);
            }
        }

        return allowedEntries;

    }

    public boolean canRead (User user, JournalEntry entry) {
        if (user.isGovernment()) {
            return true;
        } else if (user.isPatient()) {
            return user.getID().equals(entry.getPatientID());
        } else {
            return user.getDivision().equals(entry.getDivision());
        }


    }

    public boolean canEdit (User user, JournalEntry entry) {
       if (user.isDoctor() || user.isNurse()) {
              return user.getDivision().equals(entry.getDivision());
        } else {
            return false;
       }
    }

    public boolean canCreate (User user1, User user2) {
        if (user1.isDoctor() && user1.hasPatient(user2.getID())) {
            return true;
        }

        return false;
    }

    public boolean canDelete (User user, JournalEntry entry) {
        if (user.isGovernment()) {
            return true;
        }
        return false;
    }
}
    
