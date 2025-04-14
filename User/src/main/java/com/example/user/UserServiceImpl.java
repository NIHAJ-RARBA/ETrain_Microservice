package com.example.user;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    public UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserByEmail(String email) {

        User user = userRepository.findByEmail(email);

        if (user != null) {
            return user;
        }

        return null;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void createUser(String name, String email, String password) {

        if (userRepository.findByEmail(email) != null) {

            throw new RuntimeException("Email already exists");
        }

        User user = new User(name, email, password);

        userRepository.save(user);

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean loginUser(String email, String password) {

        User user = userRepository.findByEmail(email);

        if (user.getPassword().equals(password)) {
            return true;
        }

        return false;
    }

}
