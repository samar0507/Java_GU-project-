package com.eiffelbikecorp.service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/bank")
public class BankService {

    @POST
    @Path("/payment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static String processPayment(@QueryParam("bikeId") String bikeId, @QueryParam("cardNumber") String cardNumber, @QueryParam("amount") double amount, @QueryParam("currency") String currency) {
        // Simple credit card verification
        if (cardNumber.length() < 12 || cardNumber.length() > 16) {
            return "{ \"success\": false, \"message\": \"Invalid credit card number.\" }";
        }

        // Simulate payment success
        return "{ \"success\": true, \"message\": \"Payment successful! Bike ID: " + bikeId + ", Amount: " + amount + " " + currency + "\" }";
    }
}
