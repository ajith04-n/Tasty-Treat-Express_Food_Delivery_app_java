package com.tastytreat.backend.tasty_treat_express_backend.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.tastyTreatExpress.DTO.LoginRequest;
import com.tastyTreatExpress.DTO.PasswordUpdateRequest;
import com.tastyTreatExpress.DTO.UserDTO;
import com.tastyTreatExpress.DTO.UserMapper;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.UserNotFoundException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.DatabaseConnectionException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.DuplicateResourceException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.InvalidCredentialsException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.InvalidPasswordException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.NoActiveSessionException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.SuccessResponse;
import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;
import com.tastytreat.backend.tasty_treat_express_backend.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Register a user
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> registerUser(@RequestBody User user) {
        try {
            if (userService.existsByEmail(user.getEmail())) {
                throw new DuplicateResourceException("User with this email already exists!");
            }
            User new_user = userService.saveUser(user);
            UserDTO userDto = UserMapper.toUserDTO(new_user);
            return new ResponseEntity<>(userDto, HttpStatus.CREATED);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Login endpoint
    @PostMapping("/login/{email}/{password}")
    public ResponseEntity<String> authenticateUser(
            @PathVariable String email,
            @PathVariable String password,
            HttpSession session) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return new ResponseEntity<>("Email and password must be provided!", HttpStatus.BAD_REQUEST);
        }
        try {
            if (userService.authenticateUser(email, password)) {
                User user = userService.findUserByEmail(email);
                if (user == null) {
                    return new ResponseEntity<>("User not found!", HttpStatus.NOT_FOUND);
                }
                session.setAttribute("user", user);
                session.setAttribute("authenticatedUser", true);
                return new ResponseEntity<>("User authenticated successfully!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid email or password!", HttpStatus.UNAUTHORIZED);
            }
        } catch (UserNotFoundException ex) {
            return new ResponseEntity<>("User not found: " + ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (DatabaseConnectionException ex) {
            return new ResponseEntity<>("Database connection error: " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login") // as we deal with secure login- request body is preferred
    public ResponseEntity<?> authenticateUser2(@RequestBody LoginRequest loginRequest, HttpSession session) {
        if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new InvalidCredentialsException("Email and password must be provided!");
        }
        if (!userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password!");
        }
        User user = userService.findUserByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new UserNotFoundException("User not found!");
        }
        session.setAttribute("user", user);
        session.setAttribute("authenticatedUser", true);

        return ResponseEntity.ok(new SuccessResponse("User authenticated successfully!", LocalDateTime.now()));
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse> logout(HttpSession session) {
        if (session.getAttribute("authenticatedUser") == null) {
            throw new NoActiveSessionException("No active session found!");
        }
        session.invalidate();
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new SuccessResponse("Logged out successfully!", LocalDateTime.now()));
    }

    // Fetch all users and return as DTOs
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            if (users.isEmpty()) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }
            List<UserDTO> userDTOs = users.stream().map(UserMapper::toUserDTO).collect(Collectors.toList());
            return new ResponseEntity<>(userDTOs, HttpStatus.OK);

        } catch (Exception ex) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Fetch user by ID and return as DTO
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User " + userId + " not found in database");
        }
        UserDTO userDTO = UserMapper.toUserDTO(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable long userId,
            @RequestBody User user) {
        try {
            User existingUser = userService.getUserById(userId);
            if (existingUser == null) {
                throw new UserNotFoundException("User " + userId + " not found in database");
            }
            User updatedUser = userService.updateUser(userId, user);
            UserDTO updatedUserDTO = UserMapper.toUserDTO(updatedUser);
            return new ResponseEntity<>(updatedUserDTO, HttpStatus.OK);
        } catch (UserNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * // Update user address
     * 
     * @PutMapping("/{userId}/address")
     * public ResponseEntity<String> updateUserAddress(
     * 
     * @PathVariable long userId,
     * 
     * @RequestParam String newAddress) {
     * try {
     * userService.updateUserAddress(userId, newAddress);
     * return new ResponseEntity<>("Address updated successfully!", HttpStatus.OK);
     * } catch (Exception e) {
     * return new ResponseEntity<>("Unable to update address: " + e.getMessage(),
     * HttpStatus.BAD_REQUEST);
     * }
     * }
     */

    @PutMapping("/{userId}/update-password") // for security purposes
    public ResponseEntity<SuccessResponse> updateUserPassword(
            @PathVariable long userId,
            @Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest) {
        if (passwordUpdateRequest.getOldPassword() == null || passwordUpdateRequest.getOldPassword().isEmpty() ||
                passwordUpdateRequest.getNewPassword() == null || passwordUpdateRequest.getNewPassword().isEmpty()) {
            throw new InvalidPasswordException("Old and new passwords must be provided!");
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User " + userId + " not found in database");
        }
        userService.updateUserPassword(userId, passwordUpdateRequest);

        return ResponseEntity.ok(new SuccessResponse("Password updated successfully!", LocalDateTime.now()));
    }

    /*
     * // Update user password
     * 
     * @PutMapping("/{userId}/password")
     * public ResponseEntity<String> updateUserPassword(
     * 
     * @PathVariable long userId,
     * 
     * @RequestParam String oldPassword,
     * 
     * @RequestParam String newPassword) {
     * try {
     * userService.updateUserPassword(userId, oldPassword, newPassword);
     * return new ResponseEntity<>("Password updated successfully!", HttpStatus.OK);
     * } catch (Exception e) {
     * return new ResponseEntity<>("Unable to update password: " + e.getMessage(),
     * HttpStatus.BAD_REQUEST);
     * }
     * }
     * 
     */

    // Delete a user
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable long userId) {
        try {
            userService.deleteUser(userId);
            return new ResponseEntity<>("User deleted successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable to delete user: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Generate user order summary report
    @GetMapping("/{userId}/report")
    public ResponseEntity<Map<String, Object>> generateUserOrderReport(@PathVariable long userId) {
        Map<String, Object> report = userService.generateUserOrderSummaryReport(userId);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    // Add feedback for a restaurant
    @PostMapping("/{userId}/feedback/{restaurantId}")
    public ResponseEntity<String> addFeedback(
            @PathVariable long userId,
            @PathVariable String restaurantId,
            @Valid @RequestBody Feedback feedback) {
        try {
            userService.addFeedback(userId, restaurantId, feedback);
            return new ResponseEntity<>("Feedback added successfully!", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable to add feedback: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Fetch all feedback for a user
    @GetMapping("/{userId}/feedback")
    public ResponseEntity<List<Feedback>> getUserFeedback(@PathVariable long userId) {
        List<Feedback> feedback = userService.getUserFeedback(userId);
        return new ResponseEntity<>(feedback, HttpStatus.OK);
    }
}
