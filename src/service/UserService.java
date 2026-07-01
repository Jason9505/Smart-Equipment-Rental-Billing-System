package service;

import model.User;
import model.UserType;
import repository.UserRepository;
import java.util.List;

public class UserService {
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public String addUser(String id, String name, UserType userType, boolean finalYear) {
        if (id == null || id.trim().isEmpty()) return "User ID is required.";
        if (name == null || name.trim().isEmpty()) return "Name is required.";
        if (userRepo.exists(id.trim())) return "User ID already exists.";

        userRepo.add(new User(id.trim(), name.trim(), userType, finalYear));
        return null;
    }

    public String updateUser(String id, String name, UserType userType, boolean finalYear) {
        if (id == null || !userRepo.exists(id)) return "User not found.";
        User user = userRepo.getById(id);
        user.setName(name);
        user.setUserType(userType);
        user.setFinalYear(finalYear);
        userRepo.update(user);
        return null;
    }

    public String deleteUser(String id) {
        if (id == null || !userRepo.exists(id)) return "User not found.";
        userRepo.remove(id);
        return null;
    }

    public List<User> getAll() { return userRepo.getAll(); }
    public User getById(String id) { return userRepo.getById(id); }
}
