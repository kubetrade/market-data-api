package com.kubetrade.marketdata.settlementprice;

import com.kubetrade.avro.marketdata.SettlementPriceEvent;
import com.kubetrade.avro.marketdata.SettlementPriceEventKey;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@QuarkusTest
public class SettlementPriceResourceTest {

    @Inject
    @Channel("settlement-price-event-in")
    Multi<Record<SettlementPriceEventKey, SettlementPriceEvent>> settlementPriceEventMulti;

    @Test
    public void getByDateAndExecutionVenueCodeAndSymbol() {
        AtomicReference<SettlementPriceEvent> eventAtomicReference = new AtomicReference<>();
        settlementPriceEventMulti.onItem().invoke(message -> {
            System.out.println("Received message...");
            eventAtomicReference.set(message.value());
        });
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
        await().atMost(5, SECONDS).until(() -> eventAtomicReference.get() != null);
        assertThat(eventAtomicReference.get()).isNotNull();
    }

    public SettlementPrice random() {
        return new SettlementPrice(LocalDate.now(), "TST",
                RandomStringUtils.randomAlphabetic(6), new BigDecimal("100.00"));
    }

}

