package com.example.multitenancydemo.service;

import com.example.multitenancydemo.entity.User;
import com.example.multitenancydemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    public User create(User user){
        return this.userRepository.save(user);
    }
}
