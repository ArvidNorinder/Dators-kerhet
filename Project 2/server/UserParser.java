package server;

import java.io.*;
import java.util.*;

public class UserParser {
    //Format for users.txt: ID;Role;Division;patient1,patient2,patient3
    private File fileName;

    public UserParser (String file) {
        fileName = new File(file);
    }

    public static void main(String[] args) {    
        
        ArrayList<User> users = new ArrayList<User>();
        UserParser parser = new UserParser("./database/users.txt");
        users.add(new User("1", "doctor", "division1"));
        users.add(new User("2", "nurse", "division1"));
        users.add(new User("3", "patient"));
        users.add(new User("4", "patient"));
        for (User user: users) {
            parser.addUser(user);
        }

        System.out.println(parser.read());
            
                        

    }

    public ArrayList<User> read() {
        ArrayList<User> users = new ArrayList<User>();
        try {
            Scanner myReader = new Scanner(fileName);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] splitData = data.split(";");
                if (splitData.length == 4) {
                    String[] patients = splitData[3].split(",");
                    User user = new User(splitData[0], splitData[1], splitData[2]);
                    for (String patient: patients) {
                        user.addPatient(patient);
                    } 
                    users.add(user);
                } else {
                    User user = new User(splitData[0], splitData[1]);
                    users.add(user);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void addUser(User user) {
        try {
            FileWriter fileWriter = new FileWriter(fileName, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            if (user.getPatients() != null){
                printWriter.println(user.getID() + ";" + user.getRole() + ";" + user.getDivision() + ";" + user.getPatients()); 
            } else {
                printWriter.println(user.getID() + ";" + user.getRole() + ";" + user.getDivision());
            }
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
