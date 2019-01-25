package com.lotuss.tinkoffconverter.data

import com.google.gson.JsonElement

class CurrencyReceiver(private val converterApi: ConverterApi){

    fun getCurrencyList():io.reactivex.Observable<JsonElement>{
        return converterApi.getCurrencyList()
    }
}