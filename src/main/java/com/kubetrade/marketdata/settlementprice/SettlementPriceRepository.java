package com.kubetrade.marketdata.settlementprice;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SettlementPriceRepository implements PanacheRepositoryBase<SettlementPriceEntity, SettlementPriceId> {

}