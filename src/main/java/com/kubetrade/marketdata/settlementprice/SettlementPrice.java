package com.kubetrade.marketdata.settlementprice;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class SettlementPrice {

    @NotNull
    private Date date;
    @NotEmpty
    private String executionVenueCode;
    @NotEmpty
    private String symbol;
    @NotNull
    private BigDecimal settlementPrice;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getExecutionVenueCode() {
        return executionVenueCode;
    }

    public void setExecutionVenueCode(String executionVenueCode) {
        this.executionVenueCode = executionVenueCode;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getSettlementPrice() {
        return settlementPrice;
    }

    public void setSettlementPrice(BigDecimal settlementPrice) {
        this.settlementPrice = settlementPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SettlementPrice that = (SettlementPrice) o;
        return Objects.equals(date, that.date) && Objects.equals(executionVenueCode, that.executionVenueCode) && Objects.equals(symbol, that.symbol) && Objects.equals(settlementPrice, that.settlementPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, executionVenueCode, symbol, settlementPrice);
    }

    @Override
    public String toString() {
        return "SettlementPrice{" +
                "date=" + date +
                ", executionVenueCode='" + executionVenueCode + '\'' +
                ", symbol='" + symbol + '\'' +
                ", settlementPrice=" + settlementPrice +
                '}';
    }

}
