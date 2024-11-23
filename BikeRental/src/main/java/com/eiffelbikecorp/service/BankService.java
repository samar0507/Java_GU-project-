package com.eiffelbikecorp.service;
import com.eiffelbikecorp.model.Order;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/bank")
public class BankService {

    @POST
    @Path("/payment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String processPayment(Order order) {
        // Simulate payment logic (e.g., via an API call to a bank)
        return "Payment for bike ID: " + order.getBike().getId() + " was successful.";
    }
}
