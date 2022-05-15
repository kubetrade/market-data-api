package com.kubetrade.marketdata.settlementprice;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class SettlementPriceId implements Serializable {

    @NotNull
    private Date date;
    @NotEmpty
    private String executionVenueCode;
    @NotEmpty
    private String symbol;

    public SettlementPriceId(Date date, String executionVenueCode, String symbol) {
        this.date = date;
        this.executionVenueCode = executionVenueCode;
        this.symbol = symbol;
    }

    public Date getDate() {
        return date;
    }

    public String getExecutionVenueCode() {
        return executionVenueCode;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SettlementPriceId that = (SettlementPriceId) o;
        return Objects.equals(date, that.date) && Objects.equals(executionVenueCode, that.executionVenueCode) && Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, executionVenueCode, symbol);
    }

    @Override
    public String toString() {
        return "SettlementPriceId{" +
                "date=" + date +
                ", executionVenueCode='" + executionVenueCode + '\'' +
                ", symbol='" + symbol + '\'' +
                '}';
    }

}
