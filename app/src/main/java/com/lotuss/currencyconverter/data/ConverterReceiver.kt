package com.lotuss.currencyconverter.data

import com.google.gson.JsonElement

class ConverterReceiver(private val converterApi: ConverterApi){

    // Implements api methods
    fun getCurrencyList():io.reactivex.Observable<JsonElement>{
        return converterApi.getCurrencyList()
    }

    fun getRates(currencies: String, compact: String):io.reactivex.Observable<JsonElement>{
        return converterApi.getRates(currencies, compact)
    }
}