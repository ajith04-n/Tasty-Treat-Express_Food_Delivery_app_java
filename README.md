# Tasty Treat Express - Online Food Ordering Platform

## Overview
**Tasty Treat Express** is a dynamic and user-friendly online food ordering platform designed to provide a seamless experience for both customers and restaurant owners. The system allows customers to browse menus, place orders, rate restaurants, and track deliveries, while restaurant owners can manage menus, handle orders, and view reports. This project was developed using an Agile methodology and focuses on creating an engaging and efficient platform for all users.

[**View Project Presentation**](https://prezi.com/view/cIiSn0JAZuczMYCRpCTK/)

## Features

### 1. **User Management**
   - **User Registration**: New users can register by providing basic details like name, contact information, and delivery address.
   - **Account Updates**: Users can update their personal details, such as contact information and delivery address.
   - **Account Deactivation**: Users can deactivate their accounts, preventing them from placing orders or accessing the platform.
   - **Order History**: Users can view and manage their order history and preferences.

### 2. **Restaurant Menu Management**
   - **Menu Creation**: Restaurants can create and manage their menus, adding details like dish names, descriptions, prices, and availability.
   - **Real-time Menu Updates**: Restaurants can update their menu in real-time, including adding, removing, or editing items based on availability.
   - **Menu Item Deletion**: Restaurants can delete items that are no longer offered or are out of stock.
   - **Detailed Item Information**: Restaurants can view detailed information about each menu item, including description, price, and availability.

### 3. **Order Management**
   - **Order Placement**: Customers can browse the menu, select items, and place orders. A unique order ID is generated for each order.
   - **Order Modification**: Customers can modify their orders before final confirmation (e.g., adding or removing items).
   - **Order Management for Restaurants**: Restaurant owners can view and manage incoming orders, including accepting, preparing, and dispatching them.
   - **Order Cancellation**: Orders can be canceled or modified based on certain conditions (e.g., if the order hasn’t been prepared).

### 4. **Feedback Management**
   - **Customer Reviews**: Customers can leave written reviews and ratings, providing valuable feedback to restaurants and platform administrators.
   - **Restaurant Responses**: Restaurants can respond to customer reviews, addressing concerns or thanking for positive feedback.
   - **Editable Feedback**: Customers can edit their reviews and ratings after submitting them.
   - **Average Rating**: Each restaurant has an average rating displayed, helping customers make informed decisions.

### 5. **Reports Management**
   - **Menu Reports**: Restaurants can view their entire menu in a list format, showing all items, prices, and availability.
   - **Order Reports**: The system generates daily, weekly, and monthly reports showing total orders, order status (completed, pending, canceled), and order value.
   - **Customer Activity Reports**: Administrators can view reports on customer activity, including the total number of customers and their frequency of orders.
   - **Data Management**: Restaurants can delete outdated or irrelevant reports related to customer preferences or other data analysis.

### 6. **Additional Features**
   - **Email Triggers**: Automated email notifications are sent for order confirmation, status updates, and account activities such as password recovery.
   - **Live Location Tracking**: Integrated with **Google Maps API**, customers can track the live location of the delivery for accurate ETA.
   - **Multiple Payment Methods**: Supports various payment methods, including credit/debit cards, net banking, and wallets, for seamless transactions.
   - **User and Restaurant Dashboards**: Both users and restaurant owners have personalized dashboards to manage their activities and monitor performance.
   - **CSV and PDF Reports**: Downloadable reports in **CSV** and **PDF** formats with customizable date ranges for easy data analysis.

### 7. **User Engagement Features**
   - **SweetAlert Library**: **SweetAlert** is integrated for displaying elegant success and error dialogs, making the user experience more engaging and interactive.
   - **Error Handling and Validations**: The frontend provides clear validation error messages for form inputs, ensuring users are guided with proper feedback. Error messages are shown using **SweetAlert** for a smoother experience.
   - **STS (Spring Tool Suite)**: Development was done using **Spring Tool Suite (STS)**, providing an efficient and effective environment for building the Spring Boot-based backend services.

     
future scopes !!


###8. Discount and Coupon Management
Promotional Coupons: Customers can apply promotional codes during checkout to receive discounts on their orders.

Restaurant-Specific Offers: Restaurants can create custom deals and discounts to attract more customers.

Usage Limits and Validity: Coupons can have configurable expiry dates, usage limits, and minimum order requirements for better control.

###9. Delivery Partner Module
Partner Registration and Login: Delivery personnel can register and log in to view and manage their delivery assignments.

Order Assignment and Tracking: Orders are auto-assigned or manually assigned to available delivery partners, who can update the order status in real time.

Earnings Dashboard: Delivery partners can track their daily deliveries, earnings, and performance metrics through a personalized dashboard.

## Technology Stack

- **Backend**:
  - **Java**
  - **Spring Boot** (Data JPA, REST Services)
  - **MySQL** (Database)
  - **Email Service** (For notifications and password recovery)
  - **Spring Tool Suite (STS)** (For backend development)

- **Frontend**:
  - **Thymeleaf** (For rendering dynamic web pages)
  - **HTML/CSS** (For responsive web design)
  - **JavaScript** (For frontend interactivity)
  - **SweetAlert** (For user-friendly error and success messages)
  - **Google Maps API** (For location tracking)

## Getting Started

### Prerequisites
To run this project locally, you'll need to have the following installed:

- **Java 11 or higher**
- **Maven**
- **MySQL**
- **IDE** (e.g., IntelliJ IDEA, Eclipse, Spring Tool Suite)
- **Google Maps API Key** for location tracking
  

