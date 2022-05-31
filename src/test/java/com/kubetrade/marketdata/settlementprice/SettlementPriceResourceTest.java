package com.kubetrade.marketdata.settlementprice;

import com.kubetrade.avro.marketdata.EventType;
import com.kubetrade.avro.marketdata.SettlementPriceEvent;
import com.kubetrade.avro.marketdata.SettlementPriceEventKey;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.helpers.test.AssertSubscriber;
import io.smallrye.reactive.messaging.kafka.Record;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
        AssertSubscriber<Record<SettlementPriceEventKey, SettlementPriceEvent>> subscriber = settlementPriceEventMulti
                .subscribe()
                .withSubscriber(AssertSubscriber.create(1))
                .assertSubscribed();
        SettlementPrice settlementPrice = random();
        SettlementPrice saved = given()
                .contentType(ContentType.JSON)
                .body(settlementPrice)
                .post("/settlement-prices")
                .then()
                .statusCode(201)
                .extract().as(SettlementPrice.class);
        assertThat(saved).isEqualTo(saved);
        SettlementPrice found = given()
                .when()
                .get("/settlement-prices/{date}/{executionVenueCode}/{symbol}", saved.getDate().format(DateTimeFormatter.ISO_DATE), saved.getExecutionVenueCode(), saved.getSymbol())
                .then()
                .statusCode(200)
                .extract().as(SettlementPrice.class);
        assertThat(saved).isEqualTo(found);
        subscriber.awaitItems(1);
        assertThat(subscriber.getItems()).hasSize(1);
        Record<SettlementPriceEventKey, SettlementPriceEvent> record = subscriber.getItems().get(0);
        assertThat(record.key().getExecutionVenueCode()).isEqualTo(settlementPrice.getExecutionVenueCode());
        assertThat(record.key().getSymbol()).isEqualTo(settlementPrice.getSymbol());
        assertThat(record.value().getEventType()).isEqualTo(EventType.CREATE);
    }

    public SettlementPrice random() {
        return new SettlementPrice(LocalDate.now(), "TST",
                RandomStringUtils.randomAlphabetic(6), new BigDecimal("100.00"));
    }

}

