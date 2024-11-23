package com.eiffelbikecorp.model;

public class Order {
    private Customer customer;
    private Bike bike;
    private String status;

    // Constructor
    public Order(Customer customer, Bike bike) {
        this.customer = customer;
        this.bike = bike;
        this.status = "Pending";  // Default status
    }

    // Getters and Setters
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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
