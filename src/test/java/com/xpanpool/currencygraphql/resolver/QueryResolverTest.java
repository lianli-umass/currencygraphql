package com.xpanpool.currencygraphql.resolver;

import com.xpanpool.currencygraphql.model.CurrencyPair;
import com.xpanpool.currencygraphql.service.ExchangeGenerateService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class QueryResolverTest {

    private final String USD_CODE = "USD";
    private final String SGD_CODE = "SGD";
    private final float USD_SGD_RATE = 1.353f;
    private final String SUCCESS = "success";
    private final String FAILED = "failed";
    private final String ERROR_MSG = "Please specify valid country code of base and target";

    @Test
    void getCurrencyPair() {
        ExchangeGenerateService exchangeGenerateServiceMock = Mockito.mock(ExchangeGenerateService.class);
        QueryResolver queryResolver = new QueryResolver(exchangeGenerateServiceMock);

        CurrencyPair currencyPairMock = new CurrencyPair();
        currencyPairMock.setBase_code(USD_CODE);
        currencyPairMock.setTarget_code(SGD_CODE);
        currencyPairMock.setConversion_rate(USD_SGD_RATE);
        currencyPairMock.setResult(SUCCESS);
        when(exchangeGenerateServiceMock
                .getCurrencyPair(USD_CODE, SGD_CODE))
                .thenReturn(currencyPairMock);

        CurrencyPair result = queryResolver.getCurrencyPair(USD_CODE, SGD_CODE);
        assertEquals(result.getResult(), SUCCESS);
        assertEquals(result.getBase_code(), USD_CODE);
        assertEquals(result.getTarget_code(), SGD_CODE);
        assertEquals(result.getConversion_rate(), USD_SGD_RATE);
    }

    @Test
    void getCurrencyRateWithNullValue() {
        ExchangeGenerateService exchangeGenerateServiceMock = Mockito.mock(ExchangeGenerateService.class);
        QueryResolver queryResolver = new QueryResolver(exchangeGenerateServiceMock);

        CurrencyPair currencyPairMock = new CurrencyPair();
        currencyPairMock.setResult(FAILED);
        currencyPairMock.setError_type(ERROR_MSG);
        when(exchangeGenerateServiceMock
                .getCurrencyPair(USD_CODE, ""))
                .thenReturn(currencyPairMock);

        CurrencyPair result = queryResolver.getCurrencyPair(USD_CODE, "");
        assertNull(result.getBase_code());
        assertNull(result.getTarget_code());
        assertEquals(result.getResult(), FAILED);
        assertEquals(result.getError_type(), ERROR_MSG);
    }
}