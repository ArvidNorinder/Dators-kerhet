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

        if (user.isPatient()) {
            if (currentPatient == user.getID()) {
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
            if (user.getDivision() == entry.getDivision()) {
                logger.log("Accessed record", user);
                allowedEntries.add(entry);
            }
        }

        return allowedEntries;

    }

    public boolean canRead (User user, JournalEntry entry) {
        if (user.isGovernment()) {
            return true;
        } 
    }

    public boolean canEdit (User user, User user2) {
        return false;
    }

    public boolean canCreate (User user, User user2) {
        return false;
    }

    public boolean canDelete (User user, User user2) {
        if (user1)
        return false;
    }
}
    
