package com.tunelar.backend.controller;

import com.tunelar.backend.repository.UserRepository;
import org.springframework.stereotype.beans.factory.annotation.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.Controller;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> userList = new ArrayList<>();
            userRepository.findByUsername().forEach(userList::add);

            if(userList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } exception e {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public void getUserById() {

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
