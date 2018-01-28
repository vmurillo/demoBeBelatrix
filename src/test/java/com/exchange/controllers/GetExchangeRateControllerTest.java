package com.exchange.controllers;

import com.exchange.dao.ExchangeRate;
import com.exchange.models.ExchangeRateResponse;
import com.exchange.repositories.ExchangeRateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(GetExchangeRateController.class)
public class GetExchangeRateControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ExchangeRateRepository exchangeRateRepository;
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static String buildURL(String origin, String destination) {
        String url = String.format("/latest?base=%s", origin);
        if (!Strings.isNullOrEmpty(destination)) {
            url += "&symbols=" + destination;
        }
        return url;
    }

    @Test
    public void getOneExchangeRate() throws Exception {
        //Given
        String originCurr = "USD";
        String destinationCurr = "PEN";
        Float currentER = 1.0f;
        ExchangeRateResponse expectedResponse = new ExchangeRateResponse(originCurr, LocalDate.now().toString(), ImmutableMap.of(destinationCurr, currentER));

        //When
        when(exchangeRateRepository.findByOriginAndDestination(originCurr, destinationCurr))
            .thenReturn(new ExchangeRate(originCurr, destinationCurr, currentER));

        //Then
        mockMvc.perform(get(buildURL(originCurr, destinationCurr))).andExpect(status().isOk())
            .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedResponse)));
        verify(exchangeRateRepository).findByOriginAndDestination(eq(originCurr), eq(destinationCurr));
        verify(exchangeRateRepository, never()).findByOrigin(anyString());
    }

    @Test
    public void getAllExchangeRates() throws Exception {
        //Given
        String originCurr = "USD";
        Map<String, Float> rates = ImmutableMap.of("PEN", 1.0f, "GBP", 2.0f);
        ExchangeRateResponse expectedResponse = new ExchangeRateResponse(originCurr, LocalDate.now().toString(), rates);

        //When
        when(exchangeRateRepository.findByOrigin(originCurr))
                .thenReturn(rates.entrySet().stream()
                    .map(er -> new ExchangeRate(originCurr, er.getKey(), er.getValue()))
                    .collect(Collectors.toList()));
        //Then
        mockMvc.perform(get(buildURL(originCurr, ""))).andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedResponse)));
        verify(exchangeRateRepository, never()).findByOriginAndDestination(anyString(), anyString());
        verify(exchangeRateRepository).findByOrigin(eq(originCurr));
    }

    @Test
    public void noExchangeRate() throws Exception {
        //Given
        String originCurr = "PEN";
        String destinationCurr = "USD";

        //When
        when(exchangeRateRepository.findByOriginAndDestination(originCurr, destinationCurr))
                .thenReturn(null);

        //Then
        mockMvc.perform(get(buildURL(originCurr, destinationCurr))).andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Exchange rate from PEN to USD not found")));
        verify(exchangeRateRepository).findByOriginAndDestination(eq(originCurr), eq(destinationCurr));
        verify(exchangeRateRepository, never()).findByOrigin(anyString());
    }

    @Test
    public void noExchangeRates() throws Exception {
        //Given
        String originCurr = "PEN";

        //When
        when(exchangeRateRepository.findByOrigin(originCurr))
                .thenReturn(null);

        //Then
        mockMvc.perform(get(buildURL(originCurr, ""))).andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Exchange rates for PEN not found")));
        verify(exchangeRateRepository, never()).findByOriginAndDestination(anyString(), anyString());
        verify(exchangeRateRepository).findByOrigin(originCurr);
    }


}
