package com.exchange;

import com.exchange.dao.ExchangeRate;
import com.exchange.repositories.ExchangeRateRepository;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(ExchangeRateRepository exchangeRateRepository) {
        String dollarsCode = "USD";
        Map<String, Float> othersCurrencies = ImmutableMap.<String, Float>builder()
                .put("EUR", 0.8f)
                .put("GBP", .7f)
                .put("PEN", 3.4f)
                .put("PES", 300.3f)
                .put("YEN", 324.53f)
                .put("COR", 123.3f)
                .build();
        return (tmp) -> othersCurrencies.entrySet().stream().forEach(keyValue -> {
            String destinationCurrency = keyValue.getKey();
            float rate = keyValue.getValue();
            ExchangeRate exchangeRate = new ExchangeRate(dollarsCode, destinationCurrency, rate);
            exchangeRateRepository.save(exchangeRate);
        });
    }

}
