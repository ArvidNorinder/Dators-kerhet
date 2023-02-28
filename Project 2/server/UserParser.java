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
                String[] user = data.split(";");
                if (user.length == 4) {
                    users.add(new User(user[0], user[1], user[2]));
                    for (String patient: user[3].split(",")) {
                        users.get(users.size()-1).addPatient(patient);
                    }
                } else {
                    users.add(new User(user[0], user[1]));
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
