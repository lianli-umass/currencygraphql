package com.xpanpool.currencygraphql.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.xpanpool.currencygraphql.model.CurrencyPair;
import com.xpanpool.currencygraphql.service.ExchangeGenerateService;
import org.springframework.stereotype.Component;

/**
 * The Query Resolver specifies the endpoint for the currency rate service.
 * The resolver also maps method to the specified actions in the schema.
 */
@Component
public class QueryResolver implements GraphQLQueryResolver {

    private ExchangeGenerateService exchangeGenerateService;

    public QueryResolver(ExchangeGenerateService exchangeGenerateService) {
        this.exchangeGenerateService = exchangeGenerateService;
    }

    /**
     * To get the currency rate from base to target
     * @param base: the base code of a country
     * @param target: the target code of a country
     * @return: CurrencyPair Objects specifying "base_code", "target_code", conversion_rate, "result", "error_type"
     */
    public CurrencyPair getCurrencyPair(String base, String target) {
        return exchangeGenerateService.getCurrencyPair(base, target);
    }
}
