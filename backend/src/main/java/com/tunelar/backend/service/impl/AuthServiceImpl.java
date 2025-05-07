package com.tunelar.backend.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tunelar.backend.dto.JwtAuthResponse;
import com.tunelar.backend.dto.LoginDto;
import com.tunelar.backend.dto.RegisterDto;
import com.tunelar.backend.model.Role;
import com.tunelar.backend.model.User;
import com.tunelar.backend.exception.ResourceNotFoundException;
import com.tunelar.backend.exception.TunelarAPIException;
import com.tunelar.backend.repository.RoleRepository;
import com.tunelar.backend.repository.UserRepository;
import com.tunelar.backend.security.JwtTokenProvider;
import com.tunelar.backend.service.AuthService;
import lombok.AllArgsConstructor;


/**
 * Implemented AuthService
 */
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    /**
     * Link to userRepository
     */
    private final UserRepository        userRepository;
    /**
     * Link to roleRepository
     */
    private final RoleRepository        roleRepository;
    /**
     * Link to passwordEncoder
     */
    private final PasswordEncoder       passwordEncoder;
    /**
     * Link to authenticationManager
     */
    private final AuthenticationManager authenticationManager;
    /**
     * Link to jwtTokenProvider
     */
    private final JwtTokenProvider      jwtTokenProvider;

    /**
     * Registers the given user
     *
     * @param registerDto
     *            new user information
     * @return message for success or failure
     */
    @Override
    public String register (final RegisterDto registerDto) {
        // Check for duplicates - username
        if ( userRepository.existsByUsername( registerDto.getUsername() ) ) {
            throw new TunelarAPIException( HttpStatus.BAD_REQUEST, "Username already exists." );
        }
        // Check for duplicates - email
        if ( userRepository.existsByEmail( registerDto.getEmail() ) ) {
            throw new TunelarAPIException( HttpStatus.BAD_REQUEST, "Email already exists." );
        }

        final User user = new User();
        user.setUsername( registerDto.getUsername() );
        user.setEmail( registerDto.getEmail() );
        user.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );

        final Set<Role> roles = new HashSet<>();
        final Role userRole = roleRepository.findByName( "ROLE_PROD" );
        roles.add( userRole );

        user.setRoles( roles );

        userRepository.save( user );

        return "User registered successfully.";
    }

    /**
     * Logins in the given user
     *
     * @param loginDto username/email and password
     *           
     * @return response with authenticated user
     */
    @Override
    public JwtAuthResponse login ( final LoginDto loginDto ) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken( loginDto.getUsernameOrEmail(), loginDto.getPassword() ) );

        SecurityContextHolder.getContext().setAuthentication( authentication );

        final String token = jwtTokenProvider.generateToken( authentication );

        final Optional<User> userOptional = userRepository.findByUsernameOrEmail( loginDto.getUsernameOrEmail(),
                loginDto.getUsernameOrEmail() );

        String role = null;
        if ( userOptional.isPresent() ) {
            final User loggedInUser = userOptional.get();
            final Optional<Role> optionalRole = loggedInUser.getRoles().stream().findFirst();

            if ( optionalRole.isPresent() ) {
                final Role userRole = optionalRole.get();
                role = userRole.getName();
            }
        }

        final JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setRole( role );
        jwtAuthResponse.setAccessToken( token );

        return jwtAuthResponse;
    }

    /**
     * Deletes the given user by id
     *
     * @param id id of user to delete
     *            
     */

    @Override
    public void deleteUserById ( final Long id ) {
        if ( id == 1L ) {
            throw new TunelarAPIException( HttpStatus.BAD_REQUEST, "Cannot Delete Admin" );
        }

        final User user = userRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "User not found with id " + id ) );

        user.getRoles().clear(); // detach roles – makes intent obvious
        userRepository.delete( user ); // delete user only
    }

    /**
     * Updates a user's role
     *
     * @param id
     *            id of user to update
     * @param roleName
     *            new role name
     * @return message for success or failure
     */
    @Override
    public String updateUserRole ( final Long id, final String roleName ) {
        // cannot update admin
        if ( id == 1L ) {
            throw new TunelarAPIException( HttpStatus.BAD_REQUEST, "Cannot Update Admin Role" );
        }
        // cannot change role to admin
        if ( roleName.equals( "ROLE_ADMIN" ) ) {
            throw new TunelarAPIException( HttpStatus.BAD_REQUEST, "Cannot Change Role to Admin" );
        }
        final User user = userRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "User not found with id " + id ) );

        // Validate role name
        final Role role = roleRepository.findByName( roleName );
        if ( role == null ) {
            throw new TunelarAPIException( HttpStatus.BAD_REQUEST, "Invalid role name: " + roleName );
        }

        // Update user's roles
        final Set<Role> roles = new HashSet<>();
        roles.add( role );
        user.setRoles( roles );

        userRepository.save( user );

        return "User role updated successfully.";
    }

    /**
     * GENERATIVE AI WAS USED
     *
     * link: https://chatgpt.com/share/67ef29a8-0188-8010-86b0-415cadfefa0d
     *
     * Updates a user's username
     *
     * @param id
     *            id of user to update
     * @param roleName
     *            new role name
     * @return message for success or failure
     */
    @Override
    public String updateUserUsername ( final Long id, final String username ) {
        // cannot update admin
        if ( id == 1L ) {
            throw new TunelarAPIException( HttpStatus.BAD_REQUEST, "Cannot Update Admin Username" );
        }
        // Check for duplicates - username
        if ( userRepository.existsByUsername( username ) ) {
            throw new TunelarAPIException( HttpStatus.BAD_REQUEST, "Username already exists." );
        }

        final User user = userRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "User not found with id " + id ) );

        user.setUsername( username );
        userRepository.save( user );

        return "User username updated successfully.";
    }

    /**
     * gets the given user by id
     *
     * @param id
     *            id of user to get
     * @throws ResourceNotFoundException
     *             if user cannot be found
     * @return the user id
     */
    @Override
    public User getUserById ( final Long id ) {
        return userRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "User not found with id " + id ) );

    }

    /**
     * gets the given user by username
     *
     * @param username
     *            username of user to get
     * @return the userame
     */
    @Override
    public User getUserByUsername ( final String username ) {
        return userRepository.findByUsername( username )
                .orElseThrow( () -> new ResourceNotFoundException( "User not found with id " + username ) );

    }

    /**
     * gets all the users
     *
     *
     * @return a list of all the users
     */
    @Override
    public List<User> getAllUsers () {
        return userRepository.findAll();
    }

}
