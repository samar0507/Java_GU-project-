package com.eiffelbikecorp.model;

import java.util.LinkedList;
import java.util.Queue;

public class WaitingList {
    private Queue<Customer> list;

    // Constructor
    public WaitingList() {
        this.list = new LinkedList<>();
    }

    // Add customer to the waiting list
    public void addCustomerToWaitList(Customer customer) {
        list.offer(customer);
    }

    // Notify the first customer in the queue
    public Customer getNextCustomer() {
        return list.poll();
    }
    
    // Check if the list is empty
    public boolean isEmpty() {
        return list.isEmpty();
    }
}
