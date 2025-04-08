// package com.tastytreat.frontend.tasty_treat_express_frontend.controller;


// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;

// import com.tastytreat.frontend.tasty_treat_express_frontend.models.Order;



// @Controller
// @RequestMapping("/orders")
// public class OrderController {


//     @GetMapping("/payment")
//     public String paymentMethod() {
//         return "payment"; // Corresponds to payment.html
//     }

//     @GetMapping("/restaurant_orders")
//     public String restaurantOrders(Model model) {
//        // model.addAttribute("orders", orderService.getAllOrders());
//         return "restaurant_orders"; // Corresponds to restaurant_orders.html
//     }
    
//     @GetMapping("/confirmation")
//     public String orderConfirmation(Model model) {
//         model.addAttribute("order", new Order()); // Dummy order object
//         return "confirmation"; // Show confirmation page
//     }
    
// //  @GetMapping("/confirmation")
// //  public String orderConfirmation(@RequestParam(required = false) Long orderId, Model model) {
// //      System.out.println("Received orderId: " + orderId); // Debugging
// //
// //      if (orderId == null) {
// //          System.out.println("No orderId provided, redirecting...");
// //          return "redirect:/orders/restaurant_orders"; // Redirect to avoid error
// //      }
// //
// //      Order order = orderService.getOrderById(orderId);
// //
// //      if (order == null) {
// //          System.out.println("Order not found, redirecting...");
// //          return "redirect:/orders/restaurant_orders";
// //      }
// //
// //      model.addAttribute("order", order);
// //      return "confirmation"; // Show confirmation page
// //  }


// }
