package com.kubetrade.marketdata.settlementprice;

import com.kubetrade.marketdata.Roles;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;

@Path("/settlement-prices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "settlement-prices", description = "Settlement Prices Operations")
public class SettlementPriceResource {

    private static final Logger log = LoggerFactory.getLogger(SettlementPriceResource.class);
    private final SettlementPriceService settlementPriceService;

    @Inject
    public SettlementPriceResource(SettlementPriceService settlementPriceService) {
        this.settlementPriceService = settlementPriceService;
    }

    @GET
    @Path("/{date}/{executionVenueCode}/{symbol}")
    @RolesAllowed({ Roles.MARKET_DATA_SETTLEMENT_PRICE_ADMIN, Roles.MARKET_DATA_SETTLEMENT_PRICE_READ })
    @APIResponse(
            responseCode = "200",
            description = "Get settlement price by date, execution venue code and symbol",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(type = SchemaType.OBJECT, implementation = SettlementPrice.class)
            )
    )
    @APIResponse(
            responseCode = "404",
            description = "SettlementPrice does not exist for date, execution venue code and symbol",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response getByDateAndExecutionVenueCodeAndSymbol(
            @Parameter(name = "date", required = true) @PathParam("date") LocalDate date,
            @Parameter(name = "executionVenueCode", required = true) @PathParam("executionVenueCode") String executionVenueCode,
            @Parameter(name = "symbol", required = true) @PathParam("symbol") String symbol
    ) {
        return settlementPriceService.findByDateAndExecutionVenueCodeAndSymbol(date, executionVenueCode, symbol)
                .map(settlementPrice -> Response.ok(settlementPrice).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @RolesAllowed({ Roles.MARKET_DATA_SETTLEMENT_PRICE_ADMIN, Roles.MARKET_DATA_SETTLEMENT_PRICE_WRITE })
    @APIResponse(
            responseCode = "201",
            description = "SettlementPrice Created",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(type = SchemaType.OBJECT, implementation = SettlementPrice.class)
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid SettlementPrice",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @APIResponse(
            responseCode = "400",
            description = "SettlementPrice already exists for settlementPriceId",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response post(@NotNull @Valid SettlementPrice settlementPrice, @Context UriInfo uriInfo) {
        settlementPriceService.save(settlementPrice);
        return Response.status(Response.Status.CREATED).entity(settlementPrice).build();
    }

}