package com.tastytreat.backend.tasty_treat_express_backend.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    
    List<MenuItem> findByCategory(String category);
    List<MenuItem> findByRestaurantRestaurantId(String restaurantId);
    
    boolean existsByNameAndRestaurant_RestaurantId(String name, String restaurantId);
    
    @Query("SELECT mi FROM MenuItem mi JOIN mi.orders o GROUP BY mi.menuId ORDER BY COUNT(o.orderId) DESC")
    List<MenuItem> findPopularMenuItems();
    
    @Query("SELECT mi FROM MenuItem mi JOIN mi.orders o WHERE mi.restaurant.restaurantId = :restaurantId GROUP BY mi.menuId ORDER BY COUNT(o.orderId) DESC")
    List<MenuItem> findPopularMenuItemsByRestaurant(@Param("restaurantId") String restaurantId);
    
    @Query("SELECT mi FROM MenuItem mi JOIN mi.orders o GROUP BY mi.menuId ORDER BY COUNT(o.orderId) DESC")
    List<MenuItem> findTopPopularMenuItems(org.springframework.data.domain.Pageable pageable);
    
    // Feedback analysis methods
    @Query("SELECT mi FROM MenuItem mi JOIN mi.feedbacks f GROUP BY mi.menuId HAVING AVG(f.rating) >= :rating")
    List<MenuItem> findMenuItemsWithMinimumAverageRating(@Param("rating") Float rating);
    
    @Query("SELECT mi FROM MenuItem mi JOIN mi.feedbacks f GROUP BY mi.menuId HAVING AVG(f.rating) < :rating")
    List<MenuItem> findMenuItemsWithBelowAverageRating(@Param("rating") Float rating);
    
    @Query("SELECT mi FROM MenuItem mi JOIN mi.feedbacks f GROUP BY mi.menuId ORDER BY COUNT(f.feedbackId) DESC")
    List<MenuItem> findMenuItemsWithMostFeedback();


}