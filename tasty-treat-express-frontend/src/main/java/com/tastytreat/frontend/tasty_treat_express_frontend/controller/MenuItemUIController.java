package com.tastytreat.frontend.tasty_treat_express_frontend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import com.tastytreat.frontend.tasty_treat_express_frontend.models.MenuItemDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MenuItemUIController {
    @Autowired
    RestTemplate restTemplate;

    // localhost:8080/api/menuItems/restaurant/fcfaf610-1ecf-40b8-8a7a-816e2e064507
    @GetMapping("/restaurant/menu")
    public String getMethodName(@RequestParam String restaurantId,Model model) {
        String url = "http://localhost:8080/api/menuItems/restaurant/" + restaurantId;
        List<MenuItemDTO> menuItems = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MenuItemDTO>>() {
                }).getBody();

        model.addAttribute("menuItems", menuItems);
        return new String();
    }
}
