package com.eiffelbikecorp.service;

import com.eiffelbikecorp.model.Bike;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Path("/bikes")
public class BikeService {

    private static List<Bike> bikeList = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(BikeService.class.getName());

    static {
        bikeList.add(new Bike("B1", "Good", true));
        bikeList.add(new Bike("B2", "Fair", false));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Bike> getAvailableBikes() {
        List<Bike> availableBikes = new ArrayList<>();
        for (Bike bike : bikeList) {
            if (bike.isAvailable()) {
                availableBikes.add(bike);
            }
        }
        return availableBikes;
    }

    @POST
    @Path("/{id}/rent")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response rentBike(@PathParam("id") String bikeId) {
        logger.info("Rent request received for bike ID: " + bikeId);
        for (Bike bike : bikeList) {
            if (bike.getId().equals(bikeId)) {
                if (bike.isAvailable()) {
                    bike.setAvailable(false);
                    logger.info("Bike " + bikeId + " rented successfully");
                    return Response.ok(bike).build();
                } else {
                    logger.warning("Bike " + bikeId + " is already rented");
                    return buildErrorResponse("Bike is already rented", Response.Status.CONFLICT);
                }
            }
        }
        logger.warning("Bike " + bikeId + " not found");
        return buildErrorResponse("Bike not found", Response.Status.NOT_FOUND);
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Bike> getAllBikes() {
        return bikeList;
    }

    @POST
    @Path("/{id}/release")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response releaseBike(@PathParam("id") String bikeId) {
        for (Bike bike : bikeList) {
            if (bike.getId().equals(bikeId)) {
                if (!bike.isAvailable()) {
                    bike.setAvailable(true);
                    logger.info("Bike " + bikeId + " released successfully");
                    return Response.ok(bike).build();
                } else {
                    logger.warning("Bike " + bikeId + " is already available");
                    return buildErrorResponse("Bike is already available", Response.Status.CONFLICT);
                }
            }
        }
        logger.warning("Bike " + bikeId + " not found");
        return buildErrorResponse("Bike not found", Response.Status.NOT_FOUND);
    }

    private Response buildErrorResponse(String message, Response.Status status) {
        String errorResponse = String.format("{\"error\": \"%s\", \"status\": %d}", message, status.getStatusCode());
        return Response.status(status).entity(errorResponse).build();
    }
}
