package com.kubetrade.marketdata.settlementprice;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class SettlementPriceResourceTest {

    @Test
    public void getByDateAndExecutionVenueCodeAndSymbol() {
        SettlementPrice settlementPrice = random();
        SettlementPrice saved = given()
                .contentType(ContentType.JSON)
                .body(settlementPrice)
                .post("/settlement-prices")
                .then()
                .statusCode(201)
                .extract().as(SettlementPrice.class);
        assertThat(saved.getSettlementPrice()).isNotNull();
        given()
                .when()
                .get("/settlement-prices/{date}/{executionVenueCode}/{symbol}", saved.getDate().format(DateTimeFormatter.ISO_DATE), saved.getExecutionVenueCode(), saved.getSymbol())
                .then()
                .statusCode(200);
    }

    public SettlementPrice random() {
        double randomPrice = ThreadLocalRandom.current().nextDouble(50.0, 100.0);
        return new SettlementPrice(LocalDate.now(), "TST",
                RandomStringUtils.randomAlphabetic(6), BigDecimal.valueOf(randomPrice));
    }

}

