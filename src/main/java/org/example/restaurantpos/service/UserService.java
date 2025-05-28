package org.example.restaurantpos.service;
import java.util.List;
import org.example.restaurantpos.entity.User;

public interface UserService {
    List<User> getAllUsers();
    User createUser(User user);
    User updateUser(Long id, User user);
    User getUserById(Long id);
    void deleteUser(Long id);
}
