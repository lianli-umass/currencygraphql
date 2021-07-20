package com.xpanpool.currencygraphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The GraphQL API Endpoint Service to provide currency rates.
 * The source data gets from "https://app.exchangerate-api.com/"
 * This service returns the rate between pairs.
 * This service caches constant request result in internal cache implemented with ConcurrentHashMap
 * The Cache always contains four pairs: USD-SGD, SGD-USD, UDK-HKD, HKD-USD
 * The Cache updates every 1 hour in the background.
 * Using "http://localhost:8080/graphiql" to test the query:
 * query {
 *   getCurrencyPair(base: "HKD", target: "USD") {
 *     conversion_rate
 *   }
 * }
 * The result should be:
 * {
 *   "data": {
 *     "getCurrencyPair": {
 *       "conversion_rate": 0.1286
 *     }
 *   }
 * }
 *
 * @author: Lian Li
 * @version: 1.0
 * @date: 2021-07-20
 *
 */

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class CurrencygraphqlApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencygraphqlApplication.class, args);
	}

}
