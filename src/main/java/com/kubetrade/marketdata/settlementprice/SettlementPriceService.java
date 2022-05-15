package com.kubetrade.marketdata.settlementprice;

import com.kubetrade.marketdata.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class SettlementPriceService {

    private static final Logger log = LoggerFactory.getLogger(SettlementPriceService.class);
    private final SettlementPriceRepository settlementPriceRepository;
    private final SettlementPriceMapper settlementPriceMapper;

    public SettlementPriceService(SettlementPriceRepository settlementPriceRepository, SettlementPriceMapper settlementPriceMapper) {
        this.settlementPriceRepository = settlementPriceRepository;
        this.settlementPriceMapper = settlementPriceMapper;
    }

    public Optional<SettlementPrice> findByDateAndExecutionVenueCodeAndSymbol(
            Date date, String executionVenueCode, String symbol) {
        Objects.requireNonNull(date);
        SettlementPriceId settlementPriceId = new SettlementPriceId(date, executionVenueCode, symbol);
        return this.settlementPriceRepository.findByIdOptional(settlementPriceId).map(settlementPriceMapper::toDomain);
    }

    @Transactional
    public void save(@Valid SettlementPrice settlementPrice) {
        log.debug("Saving SettlementPrice: {}", settlementPrice);
        SettlementPriceEntity entity = settlementPriceMapper.toEntity(settlementPrice);
        settlementPriceRepository.persist(entity);
        settlementPriceMapper.updateDomainFromEntity(entity, settlementPrice);
    }

    @Transactional
    public void update(@Valid SettlementPrice settlementPrice) {
        log.debug("Updating SettlementPrice: {}", settlementPrice);
        SettlementPriceId settlementPriceId = new SettlementPriceId(settlementPrice.getDate(), settlementPrice.getExecutionVenueCode(), settlementPrice.getSymbol());
        SettlementPriceEntity entity = settlementPriceRepository.findByIdOptional(settlementPriceId)
                .orElseThrow(() -> new ServiceException("No SettlementPrice found for %s", settlementPriceId));
        settlementPriceMapper.updateEntityFromDomain(settlementPrice, entity);
        settlementPriceRepository.persist(entity);
        settlementPriceMapper.updateDomainFromEntity(entity, settlementPrice);
    }

}
