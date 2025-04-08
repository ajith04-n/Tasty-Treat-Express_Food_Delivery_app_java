package com.tastytreat.backend.tasty_treat_express_backend.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.tastyTreatExpress.DTO.LoginRequest;
import com.tastyTreatExpress.DTO.PasswordUpdateRequest;
import com.tastyTreatExpress.DTO.UserDTO;
import com.tastyTreatExpress.DTO.UserMapper;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.UserNotFoundException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.EmailAlreadyExistsException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.DatabaseConnectionException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.DuplicateResourceException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.InvalidCredentialsException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.InvalidInputException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.InvalidPasswordException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.NoActiveSessionException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.PasswordUpdateFailedException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.SuccessResponse;
import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;
import com.tastytreat.backend.tasty_treat_express_backend.services.EmailService;
import com.tastytreat.backend.tasty_treat_express_backend.services.RestaurantService;
import com.tastytreat.backend.tasty_treat_express_backend.services.RestaurantServiceImpl;
import com.tastytreat.backend.tasty_treat_express_backend.services.UserService;
import com.tastytreat.backend.tasty_treat_express_backend.services.UserServiceImpl;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private RestaurantServiceImpl restaurantService;
    @Autowired
    private EmailService emailService;

    // Register a user
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> registerUser(@RequestBody User user) {
        if (user.getEmail().isEmpty()) {
            throw new InvalidInputException("Email is required");
        }

        if (user.getName().isEmpty()) {
            throw new InvalidInputException("Name is required");
        }
        if (restaurantService.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered.");
        }

        User new_user = userService.saveUser(user);
        UserDTO userDto = UserMapper.toUserDTO(new_user);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);

        // try {
        // if (userService.existsByEmail(user.getEmail())) {
        // throw new DuplicateResourceException("User with this email already exists!");
        // }
        // User new_user = userService.saveUser(user);
        // UserDTO userDto = UserMapper.toUserDTO(new_user);
        // return new ResponseEntity<>(userDto, HttpStatus.CREATED);
        // } catch (Exception ex) {
        // return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        // }
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

    // http://localhost:8080/api/users/login
    @PostMapping("/login") // as we deal with secure login- request body is preferred
    public ResponseEntity<?> authenticateUser2(@RequestBody LoginRequest loginRequest, HttpSession session) {
        if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new InvalidCredentialsException("Email and password must be provided!");
        }

        User user = userService.findUserByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new UserNotFoundException("Email not found.");
        }

        if (!userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword())) {
            throw new InvalidCredentialsException("Invalid password.");
        }

        UserDTO userDTO = UserMapper.toUserDTO(user);
        session.setAttribute("user", userDTO);
        session.setAttribute("authenticatedUser", true);
        System.out.println("******************* login " + userDTO.getEmail());

        SuccessResponse successResponse = new SuccessResponse("User authenticated successfully!", LocalDateTime.now());
        successResponse.setData(userDTO);
        return ResponseEntity.ok(successResponse);
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
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User " + userId + " not found in database");
        }
        UserDTO userDTO = UserMapper.toUserDTO(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PostMapping("/user/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest,
            HttpSession session) {

        if (passwordUpdateRequest.getCurrentPassword() == null || passwordUpdateRequest.getCurrentPassword().isEmpty()
                ||
                passwordUpdateRequest.getNewPassword() == null || passwordUpdateRequest.getNewPassword().isEmpty() ||
                passwordUpdateRequest.getConfirmPassword() == null
                || passwordUpdateRequest.getConfirmPassword().isEmpty()) {
            throw new InvalidCredentialsException(
                    "Current password, new password, and confirm password must be provided!");
        }

        UserDTO user = (UserDTO) session.getAttribute("user");
        System.out.println(session.getAttribute("authenticatedUser"));
        // print all session attributes

        if (user == null) {
            throw new UserNotFoundException("User is not logged in.");
        }

        if (!userService.authenticateUser(user.getEmail(), passwordUpdateRequest.getCurrentPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect.");
        }

        if (!passwordUpdateRequest.getNewPassword().equals(passwordUpdateRequest.getConfirmPassword())) {
            throw new InvalidCredentialsException("New password and confirm password do not match.");
        }

        boolean isPasswordUpdated = userService.updateUserPassword2(user.getId(), passwordUpdateRequest);
        if (!isPasswordUpdated) {
            throw new PasswordUpdateFailedException("Failed to update password. Please try again.");
        }

        SuccessResponse successResponse = new SuccessResponse("Password updated successfully!", LocalDateTime.now());
        return ResponseEntity.ok(successResponse);
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

    @PutMapping("/updatePassword/{userId}") // for security purposes
    public ResponseEntity<Map<String, String>> updateUserPassword(
            @PathVariable long userId,
            @Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest,
            HttpSession session) {

        // Long sessionUserId = (Long) session.getAttribute("userId");

        // if (sessionUserId == null || sessionUserId != userId) {
        // Map<String, String> errorResponse = new HashMap<>();
        // errorResponse.put("status", "error");
        // errorResponse.put("message", "You are not authorized to update this
        // password.");
        // return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        // }

        if (passwordUpdateRequest.getOldPassword() == null || passwordUpdateRequest.getOldPassword().isEmpty() ||
                passwordUpdateRequest.getNewPassword() == null || passwordUpdateRequest.getNewPassword().isEmpty() ||
                passwordUpdateRequest.getConfirmPassword() == null
                || passwordUpdateRequest.getConfirmPassword().isEmpty()) {
            throw new InvalidPasswordException("Old, new, and confirm passwords must be provided!");
        }

        if (!passwordUpdateRequest.getNewPassword().equals(passwordUpdateRequest.getConfirmPassword())) {
            throw new InvalidPasswordException("New password and confirm password must match!");
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "User not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(passwordUpdateRequest.getOldPassword(), user.getPassword())) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Old password is incorrect.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            userService.updateUserPassword(userId, passwordUpdateRequest);
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("status", "success");
            successResponse.put("message", "Password updated successfully.");
            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An unexpected error occurred while updating the password.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
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

    // http://localhost:8080/api/users/delete-account/
    @DeleteMapping("/delete-account/{userId}/{password}")
    public ResponseEntity<Map<String, String>> deleteAccount(
            @PathVariable long userId,
            @PathVariable String password,
            HttpSession session) {

        User user = userService.getUserById(userId);
        if (user == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        boolean isPasswordCorrect = userService.authenticateUser(user.getEmail(), password);
        System.out.println(isPasswordCorrect+" ****************");
        if (!isPasswordCorrect) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Incorrect password.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        try {
            userService.deleteUser(userId);
            session.invalidate();
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("status", "success");
            successResponse.put("message", "Account deleted successfully.");
            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while deleting the account.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
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

    @PostMapping("/forgotPassword/{email}")
    public ResponseEntity<String> forgotPassword(@PathVariable("email") String email) {
        try {
            if (email == null || email.isEmpty()) {
                return new ResponseEntity<>("Email must be provided!", HttpStatus.BAD_REQUEST);
            }
            if (!userService.existsByEmail(email)) {
                return new ResponseEntity<>("User with this email does not exist!", HttpStatus.NOT_FOUND);
            }
            userService.forgotPassword(email);
            return new ResponseEntity<>("A temporary password has been sent to your email.", HttpStatus.OK);
        } catch (UserNotFoundException ex) {
            return new ResponseEntity<>("User with the provided email was not found.", HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>("An error occurred while processing your request. Please try again later.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
