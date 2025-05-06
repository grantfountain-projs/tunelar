package com.tunelar.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import com.tunelar.backend.repository.UserRepository;
import org.springframework.stereotype.beans.factory.annotation.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.Controller;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> userList = new ArrayList<>();
            userRepository.findByUsername().forEach(userList::add);

            if(userList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(userList, HttpStatus.OK);
        } exception e {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getUserByUsername/{username}")
    public ResponseEntity<User> getUserById(@PathVariable Long username) {
        Optional<User> userData = userRepository.findByUsername(username);
        
        if(userData.isPresent()) {
            return new ResponseEntity<>(userData.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }
    @PostMapping
    public void createUser() {

    }
    @PostMapping
    public void updateUserById() {

    }
    @DeleteMapping
    public void deleteUserById() {

    }
}
