package com.xpanpool.currencygraphql.service;

import com.xpanpool.currencygraphql.model.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;


/**
 * The service to get currency rate from external Rest service endpoint:
 * https://v6.exchangerate-api.com/v6/696f1d2d3d460c3b7cd8a9ce/pair/
 * This service maps the result to corresponding resolver methods
 */
@Service
public class ExchangeGenerateService implements ExchangeService {
    private final String SERVER_URL = "https://v6.exchangerate-api.com/v6/696f1d2d3d460c3b7cd8a9ce/pair/";
    private final String USD_CODE = "USD";
    private final String SGD_CODE = "SGD";
    private final String HKD_CODE = "HKD";

    private RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ExchangeGenerateService.class);

    @Autowired
    public ExchangeGenerateService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * To initialize the internal cache and fetch the hot data to store in the cache
     */
    @PostConstruct
    public void initCache() {
        logger.info("Starting initiating the cache.");
        updateCache();
        logger.info("Cache is initialized successfully.");
    }

    /**
     * The method to get currency rate from cache first.
     * And if no corresponding result in cache, then fetch data from external service and also store in cache.
     * @param base: base code of a country
     * @param target: target code of a country
     * @return: CurrencyPair Objects specifying "base_code", "target_code", conversion_rate, "result", "error_type"
     */
    @Cacheable(cacheNames = {"currencyRatesGraphql"})
    public CurrencyPair getCurrencyPair(String base, String target) {
        if (base == null || base.length() == 0 || target == null || target.length() == 0) {
            CurrencyPair currencyPair = new CurrencyPair();
            currencyPair.setResult("failed");
            currencyPair.setError_type("Please specify valid country code of base and target");
            return currencyPair;
        }

        logger.info("No matching data is stored in the cache. Calling external api");

        StringBuilder fullUrl = new StringBuilder();
        fullUrl.append(SERVER_URL).append(base).append("/").append(target);

        return restTemplate.exchange(fullUrl.toString(), HttpMethod.GET, null, CurrencyPair.class).getBody();
    }

    // To empty the cache
    @CacheEvict(cacheNames = {"currencyRatesGraphql"}, allEntries = true)
    public void emptyCache() {
        logger.info("To empty the cache");
    }

    /** The scheduled task to update the cache hourly.
     *  It runs at "05" min every hour, because the external service updates at "00" min every hour
     */
    @Scheduled(cron="0 5 * * * ?")
    public void scheduledUpdateCache() {
        logger.info("Scheduled task is calling method: updateCache.");
        updateCache();
        logger.info("Scheduled task is done with method updateCache");
    }

    // To update the cache with emptying the cache first and fetching hot data again.
    public void updateCache() {
        logger.info("starting updating cache");
        emptyCache();
        getCurrencyPair(USD_CODE, SGD_CODE);
        getCurrencyPair(SGD_CODE, USD_CODE);
        getCurrencyPair(USD_CODE, HKD_CODE);
        getCurrencyPair(HKD_CODE, USD_CODE);
        logger.info("Cache is updated successfully!");
    }
}
