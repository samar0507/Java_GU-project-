package com.eiffelbikecorp.service;
import com.eiffelbikecorp.model.Order;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/gustavebike")
public class GustaveBikeService {

    @GET
    @Path("/price/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPrice(@PathParam("id") String bikeId) {
        // Example static price return
        return "100 EUR";  // Price of bike
    }

    @POST
    @Path("/purchase")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String purchaseBike(Order order) {
        // Simulate purchase logic
        return "Purchase successful for bike ID: " + order.getBike().getId();
    }
}
