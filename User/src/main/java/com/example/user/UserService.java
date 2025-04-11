package com.example.user;

import java.util.List;

public interface UserService {
    
    public User getUserByEmail(String email);
    public void createUser(String name, String email, String password);
    public User getUserById(Long id);
    public List<User> getAllUsers();
    public boolean loginUser(String email,String password);
}
