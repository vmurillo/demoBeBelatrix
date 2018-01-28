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
    public void noExchangeRate() throws Exception {

    }
}
