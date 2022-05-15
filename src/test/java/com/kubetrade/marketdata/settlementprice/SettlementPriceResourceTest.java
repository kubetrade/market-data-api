package com.kubetrade.marketdata.settlementprice;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class SettlementPriceResourceTest {

    @Test
    public void getByDateAndExecutionVenueCodeAndSymbol() {
        given()
                .when()
                .get("/settlement-prices/{date}/{executionVenueCode}/{symbol}", new Date(), "test", "test")
                .then()
                .statusCode(200);
    }

}

