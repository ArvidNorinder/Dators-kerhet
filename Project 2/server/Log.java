package server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    private File file; 

    
    public static void main(String[] args) {
        Log log = new Log("./database/log.txt");
        User user = new User("Niklas", "nurse", "division1" );

        user.setCurrentlyTreating("Tobias");
        log.log("treated", user);
    }

    public Log(String file) {
        this.file = new File(file);
    }

    public void log(String logString, User user) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime timeNow = LocalDateTime.now();
            String formatDateTime = timeNow.format(formatter);
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(file, true));
            writer.append(formatDateTime + "; " + "\n" + user.getReport(logString) + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
