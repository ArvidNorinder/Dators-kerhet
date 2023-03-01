package server;

import java.util.List;

public class permissionParser {
    private Log logger;
    
    public permissionParser(Log logger) {
        this.logger = logger;
    }

    public List<JournalEntry> permissionList (User user, List<JournalEntry> entries) {
        String id = user.getID();
        String role = user.getRole();
        String currentPatient = entries.get(0).getpatientID();

        if (user.) //TODO, add a function in user that return whether the user i a patient or not

    }

    public boolean canRead (User user1, User user2) {
        return null;
    }

    public boolean canEditAndCreate (User user1, User user2) {
        return null;
    }
}
    
