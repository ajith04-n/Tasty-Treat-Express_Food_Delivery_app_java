package com.tastyTreatExpress.DTO;

import java.util.List;
import java.util.stream.Collectors;

import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;

public class OrderMapper {

    public static OrderDTO toOrderDTO(Order order) {
        if (order == null)
            return null;

        List<Long> menuItemIds = order.getMenuItems().stream()
                .map(MenuItem::getMenuId)
                .collect(Collectors.toList());

        List<Long> feedbackIds = order.getFeedbacks().stream()
                .map(Feedback::getFeedbackId)
                .collect(Collectors.toList());

        return new OrderDTO(
                order.getOrderId(),
                order.getUser().getId(),
                order.getRestaurant().getRestaurantId(),
                menuItemIds,
                feedbackIds,
                order.getTotalAmount(),
                order.getStatus(),
                order.getDeliveryAddress(),
                order.getPaymentStatus(),
                order.getPaymentMethod(),
                order.getOrderDate(),
                order.getDeliveryTime(),
                order.getCurrentLatitude(),
                order.getCurrentLongitude(),
                order.getTransactionId());
    }

    public static Order toOrderEntity(OrderDTO orderDTO) {
        if (orderDTO == null)
            return null;

        Order order = new Order();
        order.setOrderId(orderDTO.getOrderId());
        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setStatus(orderDTO.getStatus());
        order.setDeliveryAddress(orderDTO.getDeliveryAddress());
        order.setPaymentStatus(orderDTO.getPaymentStatus());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setOrderDate(orderDTO.getOrderDate());
        order.setDeliveryTime(orderDTO.getDeliveryTime());
        order.setCurrentLatitude(orderDTO.getCurrentLatitude());
        order.setCurrentLongitude(orderDTO.getCurrentLongitude());
        order.setTransactionId(orderDTO.getTransactionId());

        return order;
    }
}
