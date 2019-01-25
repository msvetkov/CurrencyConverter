package com.lotuss.tinkoffconverter.data

class CurrencyReceiver(private val converterApi: ConverterApi){

    fun getCurrencyList():io.reactivex.Observable<Result>{
        return converterApi.getCurrencyList()
    }
}