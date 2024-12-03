package com.eiffelbikecorp.service;

import com.eiffelbikecorp.model.Bike;
import com.eiffelbikecorp.model.Customer;
import com.eiffelbikecorp.model.WaitingList;
import com.eiffelbikecorp.model.Order;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("/bikes")
public class BikeService {

    private static List<Bike> bikeList = new ArrayList<>();
    private static WaitingList waitingList = new WaitingList();
    private static List<Order> orders = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(BikeService.class.getName());


    static {
        bikeList.add(new Bike("B1",1, true,10));
        bikeList.add(new Bike("B2",1, true,12));
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Bike> getAllBikes() {
        return bikeList.stream()
                       .filter(bike -> !bike.isSold())
                       .collect(Collectors.toList());
    }
    
    @GET
    @Path("/allForSale")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Bike> getAllBikesForSale() {
        return bikeList.stream()
                       .filter(bike -> !bike.isSold() && bike.getNumberOfGrades() > 1 && bike.isAvailable())
                       .collect(Collectors.toList());
    }

    
    @GET
    @Path("/orders")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Order> getAllOrdres() {
        return orders;
    }
    /*
    @GET
    @Path("/waiting")
    @Produces(MediaType.APPLICATION_JSON)
    public WaitingList getAlllist() {
        return waitingList;
    }*/

    @POST
    @Path("/{id}/rent")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response rentBike(@PathParam("id") String bikeId, @Context HttpServletRequest request) {
        System.out.println("DEBUG: Entered rentBike method with bikeId = " + bikeId);

        // Get the user's email from the request
        String userEmail = (String) request.getSession().getAttribute("User-Email");
        System.out.println("DEBUG: Retrieved User-Email from session = " + userEmail);
        
        if (userEmail == null || userEmail.isEmpty()) {
            System.out.println("ERROR: User email is null or empty. User not authenticated.");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("User not authenticated")
                    .build();
        }

        // Retrieve the customer from active sessions
        Customer customer = CustomerService.getCustomerFromSession(userEmail);
        System.out.println("DEBUG: Retrieved customer from session = " + customer);

        if (customer == null) {
            System.out.println("ERROR: No customer found for email = " + userEmail);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Customer not found")
                    .build();
        }

        // Extract customer ID
        int customerId = customer.getId();
        System.out.println("DEBUG: Retrieved customer ID = " + customerId);

        // Find the bike by its ID
        for (Bike bike : bikeList) {
            System.out.println("DEBUG: Checking bike with ID = " + bike.getId());

            if (bike.getId().equals(bikeId)) {
                System.out.println("DEBUG: Found bike with ID = " + bikeId);

                if (bike.isAvailable()) {
                    System.out.println("DEBUG: Bike is available. Proceeding to rent.");

                    // Mark bike as unavailable
                    bike.setAvailable(false);
                    System.out.println("DEBUG: Bike with ID = " + bikeId + " is now marked as unavailable.");

                    // Create a new order for the bike rental
                    Order order = new Order(customerId, bike);
                    order.setStatus("new");
                    orders.add(order);
                    System.out.println("DEBUG: Order created and added to list: " + order.getCustomer());

                    return Response.ok(order).build();
                } else {
                    System.out.println("ERROR: Bike with ID = " + bikeId + " is already rented.");
                    return Response.status(Response.Status.CONFLICT)
                            .entity("Bike is already rented")
                            .build();
                }
            }
        }

        System.out.println("ERROR: Bike with ID = " + bikeId + " not found.");
        // If bike not found
        return Response.status(Response.Status.NOT_FOUND)
                .entity("Bike not found")
                .build();
    }

    @POST
    @Path("/{id}/waiting-list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToWaitingList(@PathParam("id") String bikeId, @Context HttpServletRequest request) {
        System.out.println("DEBUG: Entered addToWaitingList method with bikeId = " + bikeId);

        // Get the user's email from the session
        String userEmail = (String) request.getSession().getAttribute("User-Email");
        System.out.println("DEBUG: Retrieved User-Email from session = " + userEmail);

        if (userEmail == null || userEmail.isEmpty()) {
            System.out.println("ERROR: User email is null or empty. User not authenticated.");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("User not authenticated")
                    .build();
        }

        // Retrieve the customer from active sessions
        Customer customer = CustomerService.getCustomerFromSession(userEmail);
        System.out.println("DEBUG: Retrieved customer from session = " + customer);

        if (customer == null) {
            System.out.println("ERROR: No customer found for email = " + userEmail);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Customer not found")
                    .build();
        }

        // Extract customer ID
        int customerId = customer.getId();
        System.out.println("DEBUG: Retrieved customer ID = " + customerId);

        // Find the bike by its ID
        for (Bike bike : bikeList) {
            System.out.println("DEBUG: Checking bike with ID = " + bike.getId());

            if (bike.getId().equals(bikeId)) {
                System.out.println("DEBUG: Found bike with ID = " + bikeId);

                if (bike.isAvailable()) {
                    System.out.println("ERROR: Bike is available. No need to join the waiting list.");
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Bike is available, no need to join waiting list")
                            .build();
                }

                // Check if the customer already has an active order for this bike
                for (Order order : orders) {
                    if (order.getCustomer() == customerId && order.getBike().getId().equals(bikeId) && "new".equals(order.getStatus())) {
                        System.out.println("ERROR: Customer already has an active order for this bike.");
                        return Response.status(Response.Status.CONFLICT)
                                .entity("You already have an active order for this bike")
                                .build();
                    }
                }

                // Add the customer to the bike's waiting list
                bike.getwaitingList().addCustomerToWaitList(customer);
                System.out.println("DEBUG: Customer added to waiting list for bike: " + bikeId);

                return Response.ok("{\"message\": \"Added to waiting list\"}").build();
            }
        }

        System.out.println("ERROR: Bike with ID = " + bikeId + " not found.");
        return Response.status(Response.Status.NOT_FOUND)
                .entity("Bike not found")
                .build();
    }


    @POST
    @Path("/{id}/release")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response releaseBike(@PathParam("id") String bikeId, @Context HttpServletRequest request, @QueryParam("comment") String comment, @QueryParam("grade") float grade) {
        System.out.println("DEBUG: Entered releaseBike method with bikeId = " + bikeId);

        // Get the user's email from the session
        String userEmail = (String) request.getSession().getAttribute("User-Email");
        System.out.println("DEBUG: Retrieved User-Email from session = " + userEmail);

        if (userEmail == null || userEmail.isEmpty()) {
            System.out.println("ERROR: User email is null or empty. User not authenticated.");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("User not authenticated")
                    .build();
        }

        // Retrieve the customer from active sessions
        Customer customer = CustomerService.getCustomerFromSession(userEmail);
        System.out.println("DEBUG: Retrieved customer from session = " + customer);

        if (customer == null) {
            System.out.println("ERROR: No customer found for email = " + userEmail);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Customer not found")
                    .build();
        }

        // Validate the grade
        if (grade < 1 || grade > 5) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Grade must be between 1 and 5.")
                    .build();
        }

        // Find the bike by its ID
        for (Bike bike : bikeList) {
            System.out.println("DEBUG: Checking bike with ID = " + bike.getId());

            if (bike.getId().equals(bikeId)) {
                System.out.println("DEBUG: Found bike with ID = " + bikeId);

                if (!bike.isAvailable()) { // Ensure the bike is rented
                    System.out.println("DEBUG: Bike is currently rented. Proceeding with release.");

                    // Check if the customer has an active order for this bike
                    for (Order order : orders) {
                        if (order.getCustomer() == customer.getId() && order.getBike() == bike && "new".equals(order.getStatus())) {
                            System.out.println("DEBUG: Active order found for customer and bike.");

                            // Update order status to "old"
                            order.setStatus("old");
                            System.out.println("DEBUG: Order status updated to 'old'.");

                            // Add comment and update grade
                            bike.getComments().add(comment);
                            System.out.println("DEBUG: Comment added: " + comment);

                            // Update the bike's grade
                            bike.updateGrade(grade);
                            System.out.println("DEBUG: Grade added: " + grade);

                            // Update the bike's condition based on the new average grade
                            bike.setCondition();
                            System.out.println("DEBUG: Bike condition updated.");

                            // Mark bike as available
                            bike.setAvailable(true);
                            System.out.println("DEBUG: Bike is now marked as available.");

                            // Notify the next customer in the waiting list, if any
                            if (!bike.getwaitingList().isEmpty()) {
                                Customer nextCustomer = bike.getwaitingList().getNextCustomer();
                                System.out.println("INFO: Notifying customer: " + nextCustomer.getName());
                                String message = "Bike with ID " + bikeId + " is now available.";
                                nextCustomer.addNotification(message);
                            }

                            return Response.ok("{\"message\": \"Bike released successfully.\"}").build();
                        }
                    }

                    System.out.println("ERROR: No active order found for this customer and bike.");
                    return Response.status(Response.Status.FORBIDDEN)
                            .entity("You don't have an active order for this bike.")
                            .build();
                } else {
                    System.out.println("ERROR: Bike is already available.");
                    return Response.status(Response.Status.CONFLICT)
                            .entity("Bike is already available.")
                            .build();
                }
            }
        }

        System.out.println("ERROR: Bike with ID = " + bikeId + " not found.");
        return Response.status(Response.Status.NOT_FOUND)
                .entity("Bike not found.")
                .build();
    }


    
    
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBike(Bike bike, @Context HttpServletRequest request) throws IOException {
    	//System.out.println("Raw JSON payload: " + request.getReader().lines().collect(Collectors.joining("\n")));
    	System.out.println("Received bike price: " + bike.getPrice());
        String userEmail = (String) request.getSession().getAttribute("User-Email");

        if (userEmail == null || userEmail.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("User not authenticated")
                    .build();
        }

        Customer customer = CustomerService.getCustomerFromSession(userEmail);

        if (customer == null || !"admin".equals(customer.getRole())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Access denied. Only admins can create bikes.")
                    .build();
        }

        
        bike.setCondition();
        bikeList.add(bike);
        
        return Response.ok("{\"message\": \"Bike created successfully.\"}").build();
    }

    
    
    @POST
    @Path("/{id}/purchase")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response purchaseBikeWithCard(@PathParam("id") String bikeId, @Context HttpServletRequest request) {
        System.out.println("DEBUG: Entered purchaseBikeWithCard method.");

       
        

        // Check authentication
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
               
        int customerId = customer.getId();
        
        
     // Find the bike by its ID
        for (Bike bike : bikeList) {
            System.out.println("DEBUG: Checking bike with ID = " + bike.getId());

            if (bike.getId().equals(bikeId)) {
                System.out.println("DEBUG: Found bike with ID = " + bikeId);

                if (bike.isAvailable()) {
                    System.out.println("DEBUG: Bike is available. Proceeding to rent.");

                    // Mark bike as unavailable
                    bike.setAvailable(false);
                    bike.setSold(true);
                    System.out.println("DEBUG: Bike with ID = " + bikeId + " is now marked as unavailable.");

                    // Create a new order for the bike rental
                    Order order = new Order(customerId, bike);
                    //
                    order.setStatus("sold");
                    orders.add(order);
                    System.out.println("DEBUG: Order created and added to list: " + order.getCustomer());

                    return Response.ok()
                            .entity("{\"message\": \"Purchase successful!\", \"bikeId\": \"" + bikeId + "\", \"amount\": " + "}")
                            .build();
                } else {
                    System.out.println("ERROR: Bike with ID = " + bikeId + " is already rented.");
                    return Response.status(Response.Status.CONFLICT)
                            .entity("Bike is already rented")
                            .build();
                }
            }
        }

        System.out.println("ERROR: Bike with ID = " + bikeId + " not found.");
        // If bike not found
        return Response.status(Response.Status.NOT_FOUND)
                .entity("Bike not found")
                .build();
        
        
        
        
        
        


        
    }

    
    
   
    
    private Response buildErrorResponse(String message, Response.Status status) {
        return Response.status(status).entity("{\"error\":\"" + message + "\"}").build();
    }
}
