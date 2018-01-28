package com.exchange.models;

import java.time.LocalDate;
import java.util.Map;

public class ExchangeRateResponse {
    private final String base;
    private final String date;
    private final Map<String, Float> rates;

    public ExchangeRateResponse(String base, String date, Map<String, Float> rates) {
        this.base = base;
        this.date = date;
        this.rates = rates;
    }

    public String getBase() {
        return base;
    }

    public String getDate() {
        return date;
    }

    public Map<String, Float> getRates() {
        return rates;
    }
}
