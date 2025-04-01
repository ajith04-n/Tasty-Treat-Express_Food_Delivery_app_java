package com.tastytreat.backend.tasty_treat_express_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant,String>{
	Restaurant findByEmail(String email); 
    boolean existsByEmail(String email);
    
    List<Restaurant> findByLocation(String location);
    boolean existsByNameAndRestaurantId(String name, String restaurantId);
  
    
    
}
