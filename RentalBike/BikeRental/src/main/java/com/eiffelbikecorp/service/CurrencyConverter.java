package com.eiffelbikecorp.service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/currency")
public class CurrencyConverter {

    @GET
    @Path("/convert")
    @Produces(MediaType.APPLICATION_JSON)
    public String convertCurrency(@QueryParam("amount") double amount, @QueryParam("from") String fromCurrency, @QueryParam("to") String toCurrency) {
        // Example static conversion rate
        double conversionRate = 1.1;  // Example conversion rate
        double convertedAmount = amount * conversionRate;
        return "{ \"convertedAmount\": " + convertedAmount + " }";
    }
}
