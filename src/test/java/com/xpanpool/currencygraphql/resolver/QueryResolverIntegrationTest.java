package com.xpanpool.currencygraphql.resolver;

import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTest;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.xpanpool.currencygraphql.model.CurrencyPair;
import com.xpanpool.currencygraphql.service.ExchangeGenerateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@GraphQLTest
public class QueryResolverIntegrationTest {
    private final String USD_CODE = "USD";
    private final String HKD_CODE = "HKD";
    private final Float RATE = 7.7671f;
    private final String SUCCESS = "success";
    private final String FAILED = "failed";
    private final String TEST_FILE_USD_HKD = "get-currency-usd-hkd.graphql";
    private final String TEST_FILE_USD_EMPTY = "get-currency-usd-empty.graphql";

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @MockBean
    private ExchangeGenerateService exchangeGenerateServiceMock;

    @Test
    void getCurrencyRateFromUSDToHKD() throws Exception {

        CurrencyPair currencyPairMock = new CurrencyPair();
        currencyPairMock.setBase_code(USD_CODE);
        currencyPairMock.setTarget_code(HKD_CODE);
        currencyPairMock.setConversion_rate(RATE);
        currencyPairMock.setResult(SUCCESS);

        when(exchangeGenerateServiceMock.getCurrencyPair(USD_CODE, HKD_CODE))
                .thenReturn(currencyPairMock);

        GraphQLResponse response =
                graphQLTestTemplate.postForResource(TEST_FILE_USD_HKD);
        assertTrue(response.isOk());
        assertEquals(response.get("$.data.getCurrencyPair.base_code"), USD_CODE);
        assertEquals(response.get("$.data.getCurrencyPair.target_code"), HKD_CODE);
        assertEquals(response.get("$.data.getCurrencyPair.result"), SUCCESS);

        //TODO: Here GraphqlResponse.class exceptions: Only support String type.
        // Need to update after GraphqlResponse fix the bug.
        //assertEquals(response.get("$.data.getCurrencyPair.conversion_rate"), RATE);
    }

    @Test
    void getCurrencyRateFromUSDToEmpty() throws Exception {

        CurrencyPair currencyPairMock = new CurrencyPair();
        currencyPairMock.setResult(FAILED);

        when(exchangeGenerateServiceMock.getCurrencyPair(USD_CODE, ""))
                .thenReturn(currencyPairMock);

        GraphQLResponse response =
                graphQLTestTemplate.postForResource(TEST_FILE_USD_EMPTY);
        assertTrue(response.isOk());
        assertEquals(response.get("$.data.getCurrencyPair.result"), FAILED);
    }
}
