The GraphQL API Endpoint Service to provide currency rates.

The source data gets from "https://app.exchangerate-api.com/"

This service returns the rate between pairs.

This service caches constant request result in internal cache implemented with ConcurrentHashMap

The Cache always contains four pairs: USD-SGD, SGD-USD, UDK-HKD, HKD-USD

The Cache updates every 1 hour in the background.

Using "http://localhost:8080/graphiql" to test the query:

query {
   getCurrencyPair(base: "HKD", target: "USD") {
      conversion_rate
   }
 }
 
 The result should be:
 {
   "data": {
     "getCurrencyPair": {
       "conversion_rate": 0.1286
     }
   }
 }
 
 query {
   getCurrencyPair(base: "HKD", target: "USD") {
      base_code
      target_code
      conversion_rate
      result
   }
 }
 
 The result should be:
 {
   "data": {
     "getCurrencyPair": {
       "base_code": "HKD"
       "target_code": "USD"
       "conversion_rate": 0.1286
       "result": "success"
     }
   }
 }
 

