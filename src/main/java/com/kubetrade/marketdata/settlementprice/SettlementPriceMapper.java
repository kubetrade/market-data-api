package com.kubetrade.marketdata.settlementprice;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface SettlementPriceMapper {

    List<SettlementPrice> toDomainList(List<SettlementPriceEntity> entities);

    SettlementPrice toDomain(SettlementPriceEntity entity);

    @InheritInverseConfiguration(name = "toDomain")
    SettlementPriceEntity toEntity(SettlementPrice domain);

    void updateEntityFromDomain(SettlementPrice domain, @MappingTarget SettlementPriceEntity entity);

    void updateDomainFromEntity(SettlementPriceEntity entity, @MappingTarget SettlementPrice domain);

}