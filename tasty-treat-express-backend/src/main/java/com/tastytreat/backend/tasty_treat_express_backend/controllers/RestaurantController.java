package com.tastytreat.backend.tasty_treat_express_backend.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.tastyTreatExpress.DTO.LoginRequest;
import com.tastyTreatExpress.DTO.MenuItemDTO;
import com.tastyTreatExpress.DTO.MenuItemMapper;
import com.tastyTreatExpress.DTO.PasswordUpdateRequest;
import com.tastyTreatExpress.DTO.RestaurantDTO;
import com.tastyTreatExpress.DTO.RestaurantMapper;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.DuplicateMenuItemException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.EmailAlreadyExistsException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.InvalidCredentialsException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.InvalidInputException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.InvalidPasswordException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.NoActiveSessionException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.RestaurantNotFoundException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.ReportNotFoundException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.SuccessResponse;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.UserNotFoundException;
import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.Report;
import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;
import com.tastytreat.backend.tasty_treat_express_backend.services.MenuItemServiceImpl;
import com.tastytreat.backend.tasty_treat_express_backend.services.RestaurantService;
import com.tastytreat.backend.tasty_treat_express_backend.services.RestaurantServiceImpl;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

	@Autowired
	private RestaurantServiceImpl restaurantService;
	@Autowired
	private MenuItemServiceImpl menuItemService;

	// Add a restaurant
	@PostMapping("/register")
	public ResponseEntity<RestaurantDTO> saveRestaurant(@Valid @RequestBody Restaurant restaurant) {

		if (restaurant.getEmail().isEmpty()) {
			throw new InvalidInputException("Email is required");
		}

		if (restaurant.getName().isEmpty()) {
			throw new InvalidInputException("Name is required");
		}
		if (restaurantService.existsByEmail(restaurant.getEmail())) {
			throw new EmailAlreadyExistsException("Email is already registered.");
		}

		Restaurant savedRestaurant = restaurantService.saveRestaurant(restaurant);
		RestaurantDTO savedRestaurantDTO = RestaurantMapper.toRestaurantDTO(savedRestaurant);
		return new ResponseEntity<>(savedRestaurantDTO, HttpStatus.CREATED);
	}

	// // Authenticate a restaurant
	// @PostMapping("/authenticate")
	// public ResponseEntity<String> authenticateRestaurant(@RequestParam String
	// email, @RequestParam String password) {
	// boolean isAuthenticated = restaurantService.authenticateRestaurant(email,
	// password);
	// if (isAuthenticated) {
	// return new ResponseEntity<>("Authentication successful", HttpStatus.OK);
	// } else {
	// return new ResponseEntity<>("Invalid email or password",
	// HttpStatus.UNAUTHORIZED);
	// }
	// }

	// @PostMapping("/login")
	// public ResponseEntity<SuccessResponse> authenticateRestaurant(@Valid
	// @RequestBody LoginRequest loginRequest,
	// HttpSession session) {
	// if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty() ||
	// loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
	// throw new InvalidCredentialsException("Email and password must be
	// provided!");
	// }
	// if (!restaurantService.authenticateRestaurant(loginRequest.getEmail(),
	// loginRequest.getPassword())) {
	// throw new InvalidCredentialsException("Invalid email or password!");
	// }
	// Restaurant restaurant =
	// restaurantService.findByEmail(loginRequest.getEmail());
	// if (restaurant == null) {
	// throw new RestaurantNotFoundException("Restaurant not found!");
	// }
	// session.setAttribute("restaurant", restaurant);
	// session.setAttribute("authenticatedRest", true);

	// return ResponseEntity.ok(new SuccessResponse("Restaurant authenticated
	// successfully!", LocalDateTime.now()));
	// }

	// http://localhost:8080/api/restaurants/login
	@PostMapping("/login")
	public ResponseEntity<?> authenticateRestaurant(@RequestBody LoginRequest loginRequest, HttpSession session) {
		// Check if email and password are provided
		if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty() ||
				loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
			throw new InvalidCredentialsException("Email and password must be provided!");
		}

		Restaurant restaurant = restaurantService.findByEmail(loginRequest.getEmail());
		if (restaurant == null) {
			throw new RestaurantNotFoundException("Email not found.");
		}

		if (!restaurantService.authenticateRestaurant(loginRequest.getEmail(), loginRequest.getPassword())) {
			throw new InvalidCredentialsException("Invalid password.");
		}

		RestaurantDTO restaurantDTO = RestaurantMapper.toRestaurantDTO(restaurant);

		session.setAttribute("restaurant", restaurant);
		session.setAttribute("authenticatedRest", true);

		SuccessResponse successResponse = new SuccessResponse("Restaurant authenticated successfully!",
				LocalDateTime.now());
		successResponse.setResData(restaurantDTO);
		System.out.println("Session ID: " + session.getId());
		System.out.println("Session created for restaurant: " + restaurant.getName());
		System.out.println("***************** " + successResponse.getResData());
		return ResponseEntity.ok(successResponse);
	}

	@PostMapping("/logout")
	public ResponseEntity<SuccessResponse> logout(HttpSession session) {
		if (session.getAttribute("authenticatedRest") == null) {
			throw new NoActiveSessionException("No active session found!");
		}
		session.invalidate();
		return ResponseEntity.status(HttpStatus.NO_CONTENT)
				.body(new SuccessResponse("Logged out successfully!", LocalDateTime.now()));
	}

	// Check if a restaurant email exists
	@GetMapping("/exists/{email}")
	public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
		if (email == null || email.trim().isEmpty() || !email.contains("@")) {
			throw new InvalidInputException("Invalid email format.");
		}
		boolean exists = restaurantService.existsByEmail(email);
		return new ResponseEntity<>(exists, HttpStatus.OK);
	}

	// Get all restaurants
	@GetMapping("/all")
	public ResponseEntity<List<RestaurantDTO>> findAll() {
		List<Restaurant> restaurants = restaurantService.findAll();
		if (restaurants.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		List<RestaurantDTO> restaurantDTOs = restaurants.stream()
				.map(RestaurantMapper::toRestaurantDTO)
				.collect(Collectors.toList());
		return new ResponseEntity<>(restaurantDTOs, HttpStatus.OK);
	}

	// Get a restaurant by ID
	@GetMapping("/{restaurantId}")
	public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable String restaurantId) {
		if (restaurantId == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
		if (restaurant != null) {
			RestaurantDTO restaurantDTO = RestaurantMapper.toRestaurantDTO(restaurant);
			return new ResponseEntity<>(restaurantDTO, HttpStatus.OK);
		} else {
			throw new RestaurantNotFoundException("Restaurant doesnt exist with the given Id.");
		}
	}

	// Update restaurant details
	@PutMapping("/update/{restaurantId}")
	public ResponseEntity<RestaurantDTO> updateRestaurant(@PathVariable String restaurantId,
			@Valid @RequestBody Restaurant restaurant) {
		if (restaurantId == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if (!restaurantService.existsById(restaurantId)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		restaurant.setRestaurantId(restaurantId);
		Restaurant updatedRestaurant = restaurantService.updateRestaurant(restaurantId, restaurant);
		RestaurantDTO updatedRestaurantDTO = RestaurantMapper.toRestaurantDTO(updatedRestaurant);
		return new ResponseEntity<>(updatedRestaurantDTO, HttpStatus.OK);
	}

	// Delete a restaurant
	@DeleteMapping("/{restaurantId}")
	public ResponseEntity<String> deleteRestaurant(@PathVariable String restaurantId) {
		if (restaurantId == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if (!restaurantService.existsById(restaurantId)) {
			throw new RestaurantNotFoundException("Restaurant Not Found: " + restaurantId);
		}
		restaurantService.deleteRestaurant(restaurantId);
		return new ResponseEntity<>("Restaurant deleted successfully", HttpStatus.NO_CONTENT);
	}

	// Get a restaurant's menu
	@GetMapping("/{restaurantId}/menu")
	public ResponseEntity<List<MenuItem>> getRestaurantMenu(@PathVariable String restaurantId) {
		List<MenuItem> menuItems = restaurantService.getRestaurantMenu(restaurantId);
		return new ResponseEntity<>(menuItems, HttpStatus.OK);
	}

	// Add a menu item to a restaurant
	@PostMapping("/{restaurantId}/addmenu")
	public ResponseEntity<List<MenuItemDTO>> addMenuItem(@PathVariable String restaurantId,
			@RequestBody MenuItem menuItem) {

		Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
		if (restaurantId == null || restaurantId.isEmpty()) {
			throw new ReportNotFoundException("Restaurant does not exist with the given Id.");
		}
		if (menuItem.getPrice() < 0) {
			throw new IllegalArgumentException("Price of menu item cannot be negative.");
		}
		if (menuItem.getQuantity() < 0) {
			throw new IllegalArgumentException("Quantity of menu item cannot be negative.");
		}
		if (menuItem.getName() == null || menuItem.getName().isEmpty()) {
			throw new IllegalArgumentException("Menu item name cannot be empty.");
		}
		if (menuItemService.existsByNameAndRestaurantId(menuItem.getName(), restaurantId)) {
			throw new DuplicateMenuItemException("Menu item with this name already exists in this restaurant.");
		}

		menuItem.setRestaurant(restaurant);
		List<MenuItem> createdMenuItems = restaurantService.addMenuItem(restaurantId, menuItem);
		List<MenuItemDTO> createdMenuItemDTOs = MenuItemMapper.toMenuItemDTOList(createdMenuItems);
		return new ResponseEntity<>(createdMenuItemDTOs, HttpStatus.CREATED);
	}

	// Get a restaurant's feedbacks
	@GetMapping("/{restaurantId}/feedbacks")
	public ResponseEntity<List<Feedback>> getRestaurantFeedback(@PathVariable String restaurantId) {
		List<Feedback> feedbacks = restaurantService.getRestaurantFeedback(restaurantId);
		return new ResponseEntity<>(feedbacks, HttpStatus.OK);
	}

	// Add feedback to a restaurant
	@PostMapping("/{restaurantId}/feedbacks")
	public ResponseEntity<List<Feedback>> addFeedback(@PathVariable String restaurantId,
			@Valid @RequestBody Feedback feedback) {
		List<Feedback> updatedFeedbacks = restaurantService.addFeedback(restaurantId, feedback);
		return new ResponseEntity<>(updatedFeedbacks, HttpStatus.CREATED);
	}

	// Calculate the average rating of a restaurant
	@GetMapping("/{restaurantId}/rating")
	public ResponseEntity<Double> calculateAverageRating(@PathVariable String restaurantId) {
		double averageRating = restaurantService.calculateAverageRating(restaurantId);
		return new ResponseEntity<>(averageRating, HttpStatus.OK);
	}

	// // Find restaurants by location
	// @GetMapping("/search/location")
	// public ResponseEntity<List<RestaurantDTO>>
	// findRestaurantsByLocation(@RequestParam String location) {
	// List<Restaurant> restaurants =
	// restaurantService.findRestaurantsByLocation(location);
	// List<RestaurantDTO> restaurantDTOs = restaurants.stream()
	// .map(RestaurantMapper::toRestaurantDTO)
	// .collect(Collectors.toList());
	// return new ResponseEntity<>(restaurantDTOs, HttpStatus.OK);
	// }

	@GetMapping("/search/location/{location}")
	public ResponseEntity<List<RestaurantDTO>> findRestaurantsByLocation(@PathVariable String location) {
		List<Restaurant> restaurants = restaurantService.findRestaurantsByLocation(location);

		if (restaurants.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		List<RestaurantDTO> restaurantDTOs = restaurants.stream()
				.map(RestaurantMapper::toRestaurantDTO)
				.collect(Collectors.toList());

		return ResponseEntity.ok(restaurantDTOs);
	}

	// Find restaurants nearby
	@GetMapping("/search/nearby/{latitude}/{longitude}/{radiusKm}")
	public ResponseEntity<List<RestaurantDTO>> findRestaurantsNearby(
			@PathVariable double latitude,
			@PathVariable double longitude,
			@PathVariable double radiusKm) {

		List<Restaurant> nearbyRestaurants = restaurantService.findRestaurantsNearby(latitude, longitude, radiusKm);

		if (nearbyRestaurants.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		List<RestaurantDTO> nearbyRestaurantDTOs = nearbyRestaurants.stream()
				.map(RestaurantMapper::toRestaurantDTO)
				.collect(Collectors.toList());
		return ResponseEntity.ok(nearbyRestaurantDTOs);
	}

	// Get reports for a restaurant
	@GetMapping("/{restaurantId}/reports")
	public ResponseEntity<List<Report>> getReportsByRestaurant(@PathVariable String restaurantId) {
		List<Report> reports = restaurantService.getRestaurantReport(restaurantId);
		return new ResponseEntity<>(reports, HttpStatus.OK);
	}

	@PostMapping("/forgotPassword/{email}")
	public ResponseEntity<String> forgotPassword(@PathVariable("email") String email) {
		try {
			if (email == null || email.isEmpty()) {
				return new ResponseEntity<>("Email must be provided!", HttpStatus.BAD_REQUEST);
			}
			if (!restaurantService.existsByEmail(email) || !restaurantService.existsByEmail(email)) {
				return new ResponseEntity<>("Restaurant with this email does not exist!", HttpStatus.NOT_FOUND);
			}
			restaurantService.forgotPassword(email);
			return new ResponseEntity<>("A temporary password has been sent to your email.", HttpStatus.OK);
		} catch (UserNotFoundException ex) {
			return new ResponseEntity<>("Restaurant with the provided email was not found.", HttpStatus.NOT_FOUND);
		} catch (Exception ex) {
			return new ResponseEntity<>("An error occurred while processing your request. Please try again later.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/updatePassword/{restaurantId}") // for security purposes
	public ResponseEntity<Map<String, String>> updateUserPassword(
			@PathVariable String restaurantId,
			@Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest,
			HttpSession session) {

		if (passwordUpdateRequest.getOldPassword() == null || passwordUpdateRequest.getOldPassword().isEmpty() ||
				passwordUpdateRequest.getNewPassword() == null || passwordUpdateRequest.getNewPassword().isEmpty() ||
				passwordUpdateRequest.getConfirmPassword() == null
				|| passwordUpdateRequest.getConfirmPassword().isEmpty()) {
			throw new InvalidPasswordException("Old, new, and confirm passwords must be provided!");
		}

		if (!passwordUpdateRequest.getNewPassword().equals(passwordUpdateRequest.getConfirmPassword())) {
			throw new InvalidPasswordException("New password and confirm password must match!");
		}

		Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
		if (restaurant == null) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("status", "error");
			errorResponse.put("message", "User not found.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
		}

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		if (!passwordEncoder.matches(passwordUpdateRequest.getOldPassword(), restaurant.getPassword())) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("status", "error");
			errorResponse.put("message", "Old password is incorrect.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		}

		try {
			restaurantService.updateUserPassword(restaurantId, passwordUpdateRequest);
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

}
