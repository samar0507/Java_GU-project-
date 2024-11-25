package com.eiffelbikecorp.service;

import com.eiffelbikecorp.model.Customer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/customers")
public class CustomerService {

    private static List<Customer> customerList = new ArrayList<>();
    private static Map<String, Customer> activeSessions = new HashMap<>();

    // Initial dummy data
    static {
        customerList.add(new Customer("C1", "Alice", "alice@example.com", "password123"));
        customerList.add(new Customer("C2", "Bob", "bob@example.com", "securePass"));
    }

    // Sign-Up Service
    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signUp(Customer newCustomer) {
        for (Customer customer : customerList) {
            if (customer.getEmail().equals(newCustomer.getEmail())) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("A customer with this email already exists.")
                        .build();
            }
        }
        if (!isValidEmail(newCustomer.getEmail()) || !isValidPassword(newCustomer.getPassword())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid email or password format.").build();
        }
        customerList.add(newCustomer);
        return Response.status(Response.Status.CREATED).entity(newCustomer).build();
    }

    // Login Service
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Customer loginRequest) {
        for (Customer customer : customerList) {
            if (customer.getEmail().equals(loginRequest.getEmail()) &&
                customer.getPassword().equals(loginRequest.getPassword())) {
                String token = UUID.randomUUID().toString();
                activeSessions.put(token, customer);
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

    // Utility Methods
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}
