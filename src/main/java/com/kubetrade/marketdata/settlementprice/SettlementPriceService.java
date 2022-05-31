package com.kubetrade.marketdata.settlementprice;

import com.kubetrade.marketdata.event.SettlementPriceEventProducer;
import com.kubetrade.marketdata.exception.ServiceException;
import io.quarkus.narayana.jta.QuarkusTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class SettlementPriceService {

    private static final Logger log = LoggerFactory.getLogger(SettlementPriceService.class);
    private final SettlementPriceRepository settlementPriceRepository;
    private final SettlementPriceMapper settlementPriceMapper;

    private final SettlementPriceEventProducer settlementPriceEventProducer;

    @Inject
    public SettlementPriceService(SettlementPriceRepository settlementPriceRepository, SettlementPriceMapper settlementPriceMapper, SettlementPriceEventProducer settlementPriceEventProducer) {
        this.settlementPriceRepository = settlementPriceRepository;
        this.settlementPriceMapper = settlementPriceMapper;
        this.settlementPriceEventProducer = settlementPriceEventProducer;
    }

    public Optional<SettlementPrice> findByDateAndExecutionVenueCodeAndSymbol(
            LocalDate date, String executionVenueCode, String symbol) {
        Objects.requireNonNull(date);
        SettlementPriceId settlementPriceId = new SettlementPriceId(date, executionVenueCode, symbol);
        return this.settlementPriceRepository.findByIdOptional(settlementPriceId).map(settlementPriceMapper::toDomain);
    }

    public void save(@Valid SettlementPrice settlementPrice) {
        log.debug("Saving SettlementPrice: {}", settlementPrice);
        QuarkusTransaction.run(() -> {
            SettlementPriceEntity entity = settlementPriceMapper.toEntity(settlementPrice);
            settlementPriceRepository.persist(entity);
            settlementPriceMapper.updateDomainFromEntity(entity, settlementPrice);
        });
        settlementPriceEventProducer.generateSettlementPriceCreateEvent(settlementPrice);
    }

    public void update(@Valid SettlementPrice settlementPrice) {
        log.debug("Updating SettlementPrice: {}", settlementPrice);
        SettlementPriceId settlementPriceId = new SettlementPriceId(settlementPrice.getDate(), settlementPrice.getExecutionVenueCode(), settlementPrice.getSymbol());
        SettlementPriceEntity entity = settlementPriceRepository.findByIdOptional(settlementPriceId)
                .orElseThrow(() -> new ServiceException("No SettlementPrice found for %s", settlementPriceId));
        settlementPriceMapper.updateEntityFromDomain(settlementPrice, entity);
        settlementPriceRepository.persist(entity);
        settlementPriceMapper.updateDomainFromEntity(entity, settlementPrice);

        settlementPriceEventProducer.generateSettlementPriceUpdateEvent(settlementPrice);
    }

}
