package com.tastyTreatExpress.DTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;

public class MenuItemMapper {

    public static MenuItemDTO toMenuItemDTO(MenuItem menuItem) {
        if (menuItem == null) return null;

        List<Long> orderIds = menuItem.getOrders() == null ? Collections.emptyList() : menuItem.getOrders().stream()
                .map(Order::getOrderId)
                .collect(Collectors.toList());

        List<Long> feedbackIds = menuItem.getFeedbacks() == null ? Collections.emptyList() : menuItem.getFeedbacks().stream()
                .map(Feedback::getFeedbackId)
                .collect(Collectors.toList());

        return new MenuItemDTO(
                menuItem.getMenuId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getCategory(),
                menuItem.getImageUrl(),
                menuItem.getQuantity(),
                menuItem.getIsAvailable(),
                menuItem.getStatus(),
                menuItem.getRestaurant() != null ? menuItem.getRestaurant().getRestaurantId() : null,
                orderIds,
                feedbackIds
        );
    }

    public static MenuItem toMenuItemEntity(MenuItemDTO menuItemDTO) {
        if (menuItemDTO == null) return null;

        MenuItem menuItem = new MenuItem();
        menuItem.setMenuId(menuItemDTO.getMenuId());
        menuItem.setName(menuItemDTO.getName());
        menuItem.setDescription(menuItemDTO.getDescription());
        menuItem.setPrice(menuItemDTO.getPrice());
        menuItem.setCategory(menuItemDTO.getCategory());
        menuItem.setImageUrl(menuItemDTO.getImageUrl());
        menuItem.setQuantity(menuItemDTO.getQuantity());
        menuItem.setIsAvailable(menuItemDTO.getIsAvailable());
        menuItem.setStatus(menuItemDTO.getStatus());

        return menuItem;
    }

    public static List<MenuItemDTO> toMenuItemDTOList(List<MenuItem> menuItems) {
        return menuItems.stream()
                .map(MenuItemMapper::toMenuItemDTO)
                .collect(Collectors.toList());
    }
}
