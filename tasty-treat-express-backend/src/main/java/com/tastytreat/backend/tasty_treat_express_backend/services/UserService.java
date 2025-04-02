package com.tastytreat.backend.tasty_treat_express_backend.services;

import java.net.PasswordAuthentication;
import java.util.List;
import java.util.Map;

import com.tastyTreatExpress.DTO.PasswordUpdateRequest;
import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;

import jakarta.validation.Valid;

public interface UserService {
	 public boolean authenticateUser(String email, String password);
	 public User findUserByEmail(String email);
	 public User saveUser(@Valid User user);
	 public boolean existsByEmail(String email);
	 public List<User> findAll();
	 public User getUserById(long userId);
	 public User updateUser(Long userId, @Valid User user);  

//- newAddional methods - 
	 public void updateUserAddress(long userId, String newAddress);
	 public void updateUserPassword(long userId,String oldPassword,String newPassword);
	 public void updateUserPassword(long userId, PasswordUpdateRequest request);
	 public void deleteUser(long userId);
	 public Map<String,Object> generateUserOrderSummaryReport(long userId);
	public Feedback addFeedback(long userId, String restaurantId, Feedback feedback);
    public List<Feedback> getUserFeedback(long userId);
	public boolean existsById(Long userId);
	 
}


