package com.tastytreat.backend.tasty_treat_express_backend.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import com.tastytreat.backend.tasty_treat_express_backend.models.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
	List<Order> findByRestaurantRestaurantId(String restaurantId);

	List<Order> findByUser_Id(Long userId);// refer to the customer field (since it's the name of our User field in Order), and use the "id" property of the User entity. 

    List<Order> findByStatus(String status);

   
    
    @Query("SELECT o FROM Order o WHERE DATE(o.orderDate) = DATE(:orderDate)")
    List<Order> findByOrderDate(LocalDate orderDate);
    List<Order> findByPaymentMethod(String paymentMethod);
    List<Order> findByRestaurant_RestaurantIdAndStatus(String restaurantId, String status);
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
    Long countByStatusAndOrderDateBetween(String status, LocalDateTime start, LocalDateTime end);
//    List<Order> findByUserId(Long userId);

    List<Order> findByRestaurantRestaurantIdAndOrderDateBetween(String restaurantId, LocalDateTime startDate,
            LocalDateTime endDate);

    List<Order> findByStatusAndOrderDateLessThan(String string, LocalDateTime minusHours);
}
