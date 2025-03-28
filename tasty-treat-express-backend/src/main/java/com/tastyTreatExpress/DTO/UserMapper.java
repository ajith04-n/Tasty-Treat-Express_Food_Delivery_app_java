package com.tastyTreatExpress.DTO;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDTO toUserDTO(User user) {
        if (user == null) return null;

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setAddress(user.getAddress());
        userDTO.setPhoneNumber(user.getPhoneNumber());

        if (user.getOrders() != null) {
            List<Long> orderIds = user.getOrders().stream()
                    .map(Order::getOrderId)
                    .collect(Collectors.toList());
            userDTO.setOrderIds(orderIds);
        } else {
            userDTO.setOrderIds(List.of()); 
        }

        if (user.getFeedbacks() != null) {
            List<Long> feedbackIds = user.getFeedbacks().stream()
                    .map(Feedback::getFeedbackId)
                    .collect(Collectors.toList());
            userDTO.setFeedbackIds(feedbackIds);
        } else {
            userDTO.setFeedbackIds(List.of()); 
        }

        return userDTO;
    }

  
    public static User toUserEntity(UserDTO userDTO) {
        if (userDTO == null) return null;

        User user = new User();
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setAddress(userDTO.getAddress());
        user.setPhoneNumber(userDTO.getPhoneNumber());

        return user;
    }
}


/*
@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "orderIds", expression = "java(user.getOrders().stream().map(Order::getOrderId).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "feedbackIds", expression = "java(user.getFeedbacks().stream().map(Feedback::getFeedbackId).collect(java.util.stream.Collectors.toList()))")
    UserDTO toUserDTO(User user);
}


*/