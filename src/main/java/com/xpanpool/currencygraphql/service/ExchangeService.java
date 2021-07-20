package com.xpanpool.currencygraphql.service;

import com.xpanpool.currencygraphql.model.CurrencyPair;

public interface ExchangeService {
    public CurrencyPair getCurrencyPair(String base, String target);
}
