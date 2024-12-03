package com.eiffelbikecorp.model;
import java.util.ArrayList;
import java.util.List;

import com.eiffelbikecorp.service.CustomerService;
public class Customer {
    private int id;
    private String name;
    private String email;
    private String password; // Added password field
    private List<String> notifications = new ArrayList<>();
    private String role= "user";  // Default role = user ; // "user" or "admin"
    // Default Constructor
    public Customer() {}

    // Constructor with parameters
    public Customer( String name, String email, String password) {
    	this.id = CustomerService.getNextCustomerId(); // Automatically assign the next ID
        this.name = name;
        this.email = email;
        this.password = password;
        
    }
    
 // Constructor
    public Customer(int id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
    public List<String> getNotifications() {
        return notifications;
    }

    public void addNotification(String message) {
        notifications.add(message);
    }

    public void clearNotifications() {
        notifications.clear();
    }
}
