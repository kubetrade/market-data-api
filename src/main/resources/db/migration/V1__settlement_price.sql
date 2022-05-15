CREATE TABLE settlement_price
(
    date                 DATE    NOT NULL,
    execution_venue_code TEXT    NOT NULL,
    symbol               TEXT    NOT NULL,
    settlement_price     DECIMAL NOT NULL,
    PRIMARY KEY (date, execution_venue_code, symbol)
);