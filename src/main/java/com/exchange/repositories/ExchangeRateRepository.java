package com.exchange.repositories;

import com.exchange.dao.ExchangeRate;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExchangeRateRepository extends CrudRepository<ExchangeRate, Long> {
    List<ExchangeRate> findByOrigin(String originCurrency);
    ExchangeRate findByOriginAndDestination(String originCurrency, String destinationCurrency);
}
