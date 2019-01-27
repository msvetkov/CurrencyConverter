package com.lotuss.tinkoffconverter.data

import com.google.gson.JsonElement

class ConverterReceiver(private val converterApi: ConverterApi){

    fun getCurrencyList():io.reactivex.Observable<JsonElement>{
        return converterApi.getCurrencyList()
    }

    fun getRates(currencies: String, compact: String):io.reactivex.Observable<JsonElement>{
        return converterApi.getRates(currencies, compact)
    }
}