package com.tastytreat.backend.tasty_treat_express_backend.repositories;

import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedbackRepo extends JpaRepository<Feedback, Long> {
    // // Feedback given by a specific user
    // List<Feedback> findByUser(User user);
   
    boolean existsByUserAndOrder(User user, Order order);
   
    List<Feedback> findByOrder(Order order);
   
    List<Feedback> findByRating(Float rating);
   
    List<Feedback> findByCommentsContaining(String keyword);
   
    List<Feedback> findByRestaurant(Restaurant restaurant);
    
    List<Feedback> findByMenuItem(MenuItem menuItem);
    
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.menuItem = :menuItem")
    Float getAverageRatingForMenuItem(@Param("menuItem") MenuItem menuItem);
    
    @Query("SELECT f FROM Feedback f WHERE f.menuItem = :menuItem AND f.rating <= :rating")
    List<Feedback> findNegativeFeedbackForMenuItem(@Param("menuItem") MenuItem menuItem, @Param("rating") Float rating);
    
    
    @Query("SELECT f FROM Feedback f WHERE f.menuItem = :menuItem AND f.rating >= :rating")
    List<Feedback> findPositiveFeedbackForMenuItem(@Param("menuItem") MenuItem menuItem, @Param("rating") Float rating);


    List<Feedback> findByOrderOrderId(Long orderId);

    List<Feedback> findByRestaurantRestaurantId(String restaurantId);

    List<Feedback> findByMenuItemId(Long menuItemId);

    List<Feedback> findByUser_Id(Long user_id);


    @Query("SELECT f FROM Feedback f WHERE f.restaurant.restaurantId = :restaurantId ORDER BY f.rating DESC, f.feedbackDate DESC")
    List<Feedback> findTopFeedbacksForRestaurant(@Param("restaurantId") String restaurantId, Pageable pageable);

   
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.restaurant.restaurantId = :restaurantId")
    Double calculateAverageRatingForRestaurant(@Param("restaurantId") String restaurantId);


    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.menuItem.id = :menuItemId")
    Double calculateAverageRatingForMenuItem(@Param("menuItemId") Long menuItemId);
}

