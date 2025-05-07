package com.tunelar.backend.service;

import java.util.List;

import com.tunelar.backend.dto.JwtAuthResponse;
import com.tunelar.backend.dto.LoginDto;
import com.tunelar.backend.dto.RegisterDto;
import com.tunelar.backend.model.User;
import com.tunelar.backend.exception.ResourceNotFoundException;

/**
 * Authorization service
 */
public interface AuthService {
    /**
     * Registers the given user
     *
     * @param registerDto new user information
     *            
     * @return message for success or failure
     */
    String register ( RegisterDto registerDto );

    /**
     * Logins in the given user
     *
     * @param loginDto username/email and password
     *            
     * @return response with authenticated user
     */
    JwtAuthResponse login ( LoginDto loginDto );

    /**
     * Deletes the given user by id
     *
     * @param id id of user to delete
     *            
     */
    void deleteUserById ( Long id );

    /**
     * gets the given user by id
     *
     * @param id id of user to get
     *            
     * @throws ResourceNotFoundException if user cannot be found
     *            
     * @return the user id
     */
    public User getUserById (final Long id);

    /**
     * Updates a user's role
     *
     * @param id id of user to update
     *            
     * @param roleName new role name
     *
     * @return message for success or failure
     */
    public String updateUserRole ( Long id, String roleName );

    /**
     * gets the given user by username
     *
     * @param username username of user to get
     *            
     * @throws ResourceNotFoundException if user cannot be found
     *             
     * @return the username
     */
    public User getUserByUsername ( final String username );

    /**
     * gets all the users
     *
     *
     * @return a list of all the users
     */
    public List<User> getAllUsers ();

    /**
     * Updates a user's username
     *
     * @param id id of user to update
     *           
     * @param roleName new role name
     *            
     * @param username the username of the user
     *           
     * @return message for success or failure
     */
    public String updateUserUsername ( final Long id, final String username );

}