package com.xpanpool.currencygraphql.service;

import com.xpanpool.currencygraphql.model.CurrencyPair;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExchangeGenerateServiceTest {

    private final String USD_CODE = "USD";
    private final String SGD_CODE = "SGD";
    private final String EUR_CODE = "EUR";
    private final String HKD_CODE = "HKD";
    private final float USD_SGD_RATE = 1.353f;
    private final String SUCCESS = "success";
    private final String FAILED = "failed";
    private final String ERROR_MSG = "Please specify valid country code of base and target";
    private final String SERVER_URL = "https://v6.exchangerate-api.com/v6/696f1d2d3d460c3b7cd8a9ce/pair/";

    @Test
    void getCurrencyPairfromUSDtoSGD() {
        RestTemplateBuilder restTemplateBuilderMock = Mockito.mock(RestTemplateBuilder.class);
        RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);
        when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);

        ExchangeGenerateService exchangeGenerateService = new ExchangeGenerateService(restTemplateBuilderMock);

        CurrencyPair currencyPairMock = new CurrencyPair();
        currencyPairMock.setBase_code(USD_CODE);
        currencyPairMock.setTarget_code(SGD_CODE);
        currencyPairMock.setConversion_rate(USD_SGD_RATE);
        currencyPairMock.setResult(SUCCESS);

        ResponseEntity responseEntity = new ResponseEntity(currencyPairMock, HttpStatus.ACCEPTED);

        when(restTemplateMock
                .exchange(buildFullUrl(USD_CODE, SGD_CODE), HttpMethod.GET, null, CurrencyPair.class))
                .thenReturn(responseEntity);

        CurrencyPair result = exchangeGenerateService.getCurrencyPair(USD_CODE, SGD_CODE);
        assertEquals(result.getBase_code(), USD_CODE);
        assertEquals(result.getTarget_code(), SGD_CODE);
        assertEquals(result.getConversion_rate(), USD_SGD_RATE);
        assertEquals(result.getResult(), SUCCESS);
    }

    @Test
    void getCurrencyPairfromEmptytoEUR() {
        RestTemplateBuilder restTemplateBuilderMock = Mockito.mock(RestTemplateBuilder.class);
        RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);
        Mockito.when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);

        ExchangeGenerateService exchangeGenerateService = new ExchangeGenerateService(restTemplateBuilderMock);

        CurrencyPair result = exchangeGenerateService.getCurrencyPair("", EUR_CODE);
        assertEquals(result.getResult(), FAILED);
        assertEquals(result.getError_type(), ERROR_MSG);
        assertNull(result.getBase_code());

    }

    @Test
    void updateCacheTest() {
        RestTemplateBuilder restTemplateBuilderMock = Mockito.mock(RestTemplateBuilder.class);
        RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);
        Mockito.when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);

        ExchangeGenerateService exchangeGenerateService = new ExchangeGenerateService(restTemplateBuilderMock);
        CurrencyPair currencyPairMock = new CurrencyPair();

        ResponseEntity responseEntity = new ResponseEntity(currencyPairMock, HttpStatus.ACCEPTED);

        when(restTemplateMock
                .exchange(buildFullUrl(USD_CODE, SGD_CODE), HttpMethod.GET, null, CurrencyPair.class))
                .thenReturn(responseEntity);
        when(restTemplateMock
                .exchange(buildFullUrl(SGD_CODE, USD_CODE), HttpMethod.GET, null, CurrencyPair.class))
                .thenReturn(responseEntity);
        when(restTemplateMock
                .exchange(buildFullUrl(USD_CODE, HKD_CODE), HttpMethod.GET, null, CurrencyPair.class))
                .thenReturn(responseEntity);
        when(restTemplateMock
                .exchange(buildFullUrl(HKD_CODE, USD_CODE), HttpMethod.GET, null, CurrencyPair.class))
                .thenReturn(responseEntity);

        exchangeGenerateService.updateCache();

        verify(restTemplateMock, times(4));
    }

    private String buildFullUrl(String from, String to) {
        StringBuilder sb = new StringBuilder();
        return sb.append(SERVER_URL).append(from).append("/").append(to).toString();
    }
}