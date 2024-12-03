package com.eiffelbikecorp.model;

public class Order {
    private int customerId;
    private Bike bike;
    private String status;
 

    // Constructor
    public Order(int customerId, Bike bike) {
        this.customerId = customerId;
        this.bike = bike;
        this.status = "Pending";  // Default status
    }

    // Getters and Setters
    public int getCustomer() {
        return customerId;
    }

    public void setCustomer(int customer) {
        this.customerId = customer;
    }

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
