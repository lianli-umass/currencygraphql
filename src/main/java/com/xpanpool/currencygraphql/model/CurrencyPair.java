package com.xpanpool.currencygraphql.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrencyPair {
    private String base_code;
    private String target_code;
    private Float conversion_rate;
    private String result;
    private String error_type;
}
