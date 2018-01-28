package com.exchange.exceptions;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class ExchangeRateException extends RuntimeException {
    private boolean isOne;
    private String reason;

    public ExchangeRateException(boolean isOne, String reason) {
        this.isOne = isOne;
        this.reason = reason;
    }

    public boolean isOne() {
        return isOne;
    }

    public Map<String, String> getReason() {
        return ImmutableMap.of("operation", isOne ? "specific exchange rate not found" : "list of exchanges rates", "reason" , reason);
    }
}
