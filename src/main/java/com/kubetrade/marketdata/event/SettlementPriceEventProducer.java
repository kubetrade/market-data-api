package com.kubetrade.marketdata.event;

import com.kubetrade.avro.marketdata.EventType;
import com.kubetrade.avro.marketdata.SettlementPriceEvent;
import com.kubetrade.avro.marketdata.SettlementPriceEventKey;
import com.kubetrade.marketdata.settlementprice.SettlementPrice;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class SettlementPriceEventProducer {

    private final Emitter<Record<SettlementPriceEventKey, SettlementPriceEvent>> emitter;

    @Inject
    public SettlementPriceEventProducer(@Channel("settlement-price-event-out") Emitter<Record<SettlementPriceEventKey, SettlementPriceEvent>> emitter) {
        this.emitter = emitter;
    }

    public void generateSettlementPriceCreateEvent(SettlementPrice settlementPrice) {
        emitter.send(this.createEvent(EventType.CREATE, settlementPrice));
    }

    @Transactional
    public void generateSettlementPriceUpdateEvent(SettlementPrice settlementPrice) {
        emitter.send(this.createEvent(EventType.UPDATE, settlementPrice));
    }

    @Transactional
    public void generateSettlementPriceDeleteEvent(SettlementPrice settlementPrice) {
        emitter.send(this.createEvent(EventType.DELETE, settlementPrice));
    }

    private SettlementPriceEventKey createKey(SettlementPrice settlementPrice) {
        return new SettlementPriceEventKey(settlementPrice.getExecutionVenueCode(), settlementPrice.getSymbol());
    }

    private Record<SettlementPriceEventKey, SettlementPriceEvent> createEvent(EventType eventType, SettlementPrice settlementPrice) {
        SettlementPriceEventKey settlementPriceEventKey = this.createKey(settlementPrice);
        SettlementPriceEvent settlementPriceEvent =  SettlementPriceEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType(eventType)
                .setSettlementPrice(com.kubetrade.avro.marketdata.SettlementPrice.newBuilder()
                        .setDate(settlementPrice.getDate())
                        .setExecutionVenueCode(settlementPrice.getExecutionVenueCode())
                        .setSymbol(settlementPrice.getSymbol())
                        .setPrice(settlementPrice.getSettlementPrice())
                        .build())
                .setEventTimestamp(Instant.now())
                .build();
        return Record.of(settlementPriceEventKey, settlementPriceEvent);
    }

}
