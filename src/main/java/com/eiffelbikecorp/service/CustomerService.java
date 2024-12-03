package com.eiffelbikecorp.service;

import com.eiffelbikecorp.model.Customer;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/customers")
public class CustomerService {

    private static List<Customer> customerList = new ArrayList<>();
    private static Map<String, Customer> activeSessions = new HashMap<>();

    
    
    static int maxCustomerId=1;
    public static  Integer getNextCustomerId() {
        
		return ++maxCustomerId;
    }
    
    
    static {
    	customerList.add(new Customer(1, "Admin", "admin@admin.com", "admin", "admin"));
    }
    

    

 // Sign-Up Service
    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signUp(Customer newCustomer) {
        // Check if a customer with the same email already exists
        for (Customer customer : customerList) {
            if (customer.getEmail().equals(newCustomer.getEmail())) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("A customer with this email already exists.")
                        .build();
            }
        }

        // Assign the next available customer ID
        newCustomer.setId(getNextCustomerId());

        // Add the new customer to the list
        customerList.add(newCustomer);

        // Return a successful response with the newly created customer
        return Response.status(Response.Status.CREATED).entity(newCustomer).build();
    }

    // Login Service
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Customer loginRequest,@Context HttpServletRequest request) {
        for (Customer customer : customerList) {
            if (customer.getEmail().equals(loginRequest.getEmail()) &&
                customer.getPassword().equals(loginRequest.getPassword())) {
                String token = UUID.randomUUID().toString();
                activeSessions.put(token, customer);
                activeSessions.put(customer.getEmail(), customer); 
                request.getSession().setAttribute("User-Email", customer.getEmail());// Store in activeSessions
         
                return Response.ok("{\"token\": \"" + token + "\"}").build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("Invalid email or password.").build();
    }

    // Logout Service
    @POST
    @Path("/logout")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(String token) {
        if (activeSessions.containsKey(token)) {
            activeSessions.remove(token);
            return Response.ok("{\"message\": \"Logged out successfully.\"}").build();
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("Invalid session token.").build();
    }

    // Get All Customers (for testing purposes)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Customer> getAllCustomers() {
        return customerList;
    }
    
    
    public static Customer getCustomerFromSession(String email) {
        return activeSessions.get(email);
    }

    // Method to find a customer by ID
    public static Customer findCustomerById(String customerId) {
        try {
            int id = Integer.parseInt(customerId);  // Convert the string to an integer
            for (Customer customer : customerList) {
                if (customer.getId() == id) {  // Compare with integer ID
                    return customer;
                }
            }
        } catch (NumberFormatException e) {
            // Handle the case where the ID cannot be parsed to an integer
            System.out.println("Invalid customer ID: " + customerId);
        }
        return null;  // Return null if customer not found
    }


    
    public static Map<String, Customer> getActiveSessions() {
        return activeSessions;
    }

    
    
    
    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser(@Context HttpServletRequest request) {
        String userEmail = (String) request.getSession().getAttribute("User-Email");

        if (userEmail == null || userEmail.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("User not authenticated")
                    .build();
        }

        Customer customer = CustomerService.getCustomerFromSession(userEmail);

        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Customer not found")
                    .build();
        }

        return Response.ok(customer).build();
    }
    
    
    @GET
    @Path("/notifications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchNotifications(@Context HttpServletRequest request) {
        String userEmail = (String) request.getSession().getAttribute("User-Email");

        if (userEmail == null || userEmail.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("User not authenticated")
                    .build();
        }

        Customer customer = getCustomerFromSession(userEmail);

        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Customer not found")
                    .build();
        }

        // Fetch notifications if they exist
        List<String> notifications = customer.getNotifications();
        if (notifications.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT)
                    .entity("{\"message\":\"No new notifications\"}")
                    .build();
        }

        // Return notifications and clear them afterward
        Response response = Response.ok(notifications).build();
        customer.clearNotifications(); // Clear notifications after fetching
        return response;
    }


    // Utility Methods
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}
