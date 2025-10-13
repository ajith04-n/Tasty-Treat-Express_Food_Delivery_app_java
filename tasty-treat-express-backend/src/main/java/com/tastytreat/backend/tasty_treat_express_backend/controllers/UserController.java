package com.tastytreat.backend.tasty_treat_express_backend.controllers;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.tastyTreatExpress.DTO.*;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.*;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.*;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;
import com.tastytreat.backend.tasty_treat_express_backend.services.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RestaurantServiceImpl restaurantService;

    // ✅ Register a new user + send welcome email
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> registerUser(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new InvalidInputException("Email is required");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new InvalidInputException("Name is required");
        }
        if (restaurantService.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered.");
        }

        // ✅ Save the user
        User newUser = userService.saveUser(user);
        UserDTO userDto = UserMapper.toUserDTO(newUser);

        // ✅ Send registration success email
        try {
            emailService.sendRegistrationSuccessEmail(user.getEmail(), user.getName());
        } catch (Exception e) {
            System.err.println("❌ Failed to send registration email: " + e.getMessage());
        }

        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    // 🔐 Login
    @PostMapping("/login")
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

        SuccessResponse successResponse = new SuccessResponse("User authenticated successfully!", LocalDateTime.now());
        successResponse.setData(userDTO);
        return ResponseEntity.ok(successResponse);
    }

    // ✅ Logout
    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse> logout(HttpSession session) {
        if (session.getAttribute("authenticatedUser") == null) {
            throw new NoActiveSessionException("No active session found!");
        }
        session.invalidate();
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new SuccessResponse("Logged out successfully!", LocalDateTime.now()));
    }

    // ✅ Get all users
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserDTO> userDTOs = users.stream().map(UserMapper::toUserDTO).collect(Collectors.toList());
        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }

    // ✅ Get user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null)
            throw new UserNotFoundException("User " + userId + " not found in database");
        return new ResponseEntity<>(UserMapper.toUserDTO(user), HttpStatus.OK);
    }

    // ✅ Forgot Password (trigger email with temporary password)
    @PostMapping("/forgotPassword/{email}")
    public ResponseEntity<String> forgotPassword(@PathVariable("email") String email) {
        try {
            if (email == null || email.isEmpty()) {
                return new ResponseEntity<>("Email must be provided!", HttpStatus.BAD_REQUEST);
            }
            if (!userService.existsByEmail(email)) {
                return new ResponseEntity<>("User with this email does not exist!", HttpStatus.NOT_FOUND);
            }

            String tempPassword = userService.forgotPassword(email);

            // ✅ Send temporary password email
            String htmlBody = """
                    <html>
                    <body style='font-family: Arial, sans-serif; line-height: 1.6;'>
                        <h2 style='color:#ff6600;'>Password Reset Request 🔒</h2>
                        <p>Hello,</p>
                        <p>Your temporary password is: <b>%s</b></p>
                        <p>Please log in using this password and change it immediately for security reasons.</p>
                        <hr/>
                        <p style='font-size: 12px; color: gray;'>If you didn’t request this, please ignore this email.</p>
                        <p style='color: gray;'>– Tasty Treat Express Team</p>
                    </body>
                    </html>
                    """.formatted(tempPassword);

            emailService.sendHtmlMessage(email, "🔒 Password Reset - Tasty Treat Express", htmlBody);

            return new ResponseEntity<>("A temporary password has been sent to your email.", HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("Error while sending email: " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ Fixed: Update Password (compatible with UserServiceImpl)
    @PutMapping("/updatePassword/{id}")
    public ResponseEntity<?> updatePassword(
            @PathVariable("id") long userId,
            @Valid @RequestBody PasswordUpdateRequest request) {

        try {
            boolean updated = userService.updateUserPassword2(userId, request);

            if (updated) {
                return ResponseEntity.ok(Map.of("message", "Password updated successfully!"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Password update failed."));
            }
        } catch (InvalidPasswordException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (DatabaseOperationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Database operation failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }
}
