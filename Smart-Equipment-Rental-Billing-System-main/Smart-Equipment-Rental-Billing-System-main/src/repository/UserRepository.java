package repository;

import model.User;
import model.UserType;
import java.io.*;
import java.util.*;

public class UserRepository {
    private static final String DATA_FILE = "data/users.txt";

    private Map<String, User> userMap;

    public UserRepository() {
        this.userMap = new HashMap<>();
    }

    public void add(User user) {
        userMap.put(user.getUserId(), user);
        saveToFile();
    }

    public User getById(String id) {
        return userMap.get(id);
    }

    public List<User> getAll() {
        return new ArrayList<>(userMap.values());
    }

    public boolean exists(String id) {
        return userMap.containsKey(id);
    }

    public void update(User user) {
        saveToFile();
    }

    public void remove(String id) {
        userMap.remove(id);
        saveToFile();
    }

    public void saveToFile() {
        File file = new File(DATA_FILE);
        file.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (User user : userMap.values()) {
                pw.println(toCsv(user));
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        userMap.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                User user = fromCsv(line);
                if (user != null) {
                    userMap.put(user.getUserId(), user);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    private String toCsv(User user) {
        return user.getUserId() + "," + user.getName() + ","
                + user.getUserType().name() + "," + user.isFinalYear();
    }

    private User fromCsv(String line) {
        String[] parts = line.split(",");
        if (parts.length < 4) return null;
        try {
            String id = parts[0];
            String name = parts[1];
            UserType userType = UserType.valueOf(parts[2]);
            boolean finalYear = Boolean.parseBoolean(parts[3]);
            return new User(id, name, userType, finalYear);
        } catch (Exception e) {
            System.err.println("Error parsing user line: " + line);
            return null;
        }
    }
}
