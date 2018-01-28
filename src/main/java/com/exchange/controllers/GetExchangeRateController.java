package com.exchange.controllers;

import com.exchange.dao.ExchangeRate;
import com.exchange.models.ExchangeRateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.exchange.repositories.ExchangeRateRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;

@RestController
@RequestMapping("/latest")
public class GetExchangeRateController {
    private final ExchangeRateRepository exchangeRateRepository;

    @Autowired
    public GetExchangeRateController(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ExchangeRateResponse greeting(@RequestParam(value="base") String base, @RequestParam(value="symbols") String symbols) {
        ExchangeRate exchangeRate = exchangeRateRepository.findByOriginAndDestination(base, symbols);
        final ExchangeRateResponse response;
        if (exchangeRate != null) {
            response = new ExchangeRateResponse("USD", LocalDate.now().toString(), Collections.emptyMap());

        } else {
            response = new ExchangeRateResponse(base, LocalDate.now().toString(), Collections.singletonMap(symbols, exchangeRate.getRate()));
        }
        return response;
    }
}
