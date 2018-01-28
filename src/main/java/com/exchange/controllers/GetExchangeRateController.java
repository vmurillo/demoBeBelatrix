package com.exchange.controllers;

import com.exchange.dao.ExchangeRate;
import com.exchange.exceptions.ExchangeRateException;
import com.exchange.models.ExchangeRateResponse;
import com.exchange.repositories.ExchangeRateRepository;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/latest")
public class GetExchangeRateController {
    private final ExchangeRateRepository exchangeRateRepository;

    @Autowired
    public GetExchangeRateController(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    private ExchangeRateResponse getAllExchangeRates(String base) {
        List<ExchangeRate> exchangeRates = exchangeRateRepository.findByOrigin(base);
        final ExchangeRateResponse response;
        if (exchangeRates == null || exchangeRates.isEmpty()) {
            throw new ExchangeRateException(false, String.format("Exchange rates for %s not found", base));
        } else {
            Map<String, Float> exchanges = exchangeRates.stream().
                    collect(Collectors.toMap(ExchangeRate::getDestination, ExchangeRate::getRate));
            response = new ExchangeRateResponse(base, LocalDate.now().toString(), exchanges);
        }
        return response;
    }

    private ExchangeRateResponse getOneExchangeRate(String base, String symbols) {
        ExchangeRate exchangeRate = exchangeRateRepository.findByOriginAndDestination(base, symbols);
        final ExchangeRateResponse response;
        if (exchangeRate == null) {
            throw new ExchangeRateException(true, String.format("Exchange rate from %s to %s not found", base, symbols));
        } else {
            response = new ExchangeRateResponse(base, LocalDate.now().toString(), Collections.singletonMap(symbols, exchangeRate.getRate()));
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ExchangeRateResponse getExchangeRate(@RequestParam(value="base") String base, @RequestParam(value = "symbols", required = false) String symbols) {
        if (Strings.isNullOrEmpty(symbols)) {
            return getAllExchangeRates(base);
        } else {
            return getOneExchangeRate(base, symbols);
        }
    }

    @ExceptionHandler(ExchangeRateException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleCustomException(ExchangeRateException ex) {
        return ex.getReason();
    }
}
