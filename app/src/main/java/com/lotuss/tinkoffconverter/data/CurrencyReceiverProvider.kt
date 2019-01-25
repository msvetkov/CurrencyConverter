package com.lotuss.tinkoffconverter.data

object CurrencyReceiverProvider{

    fun provide(): CurrencyReceiver{
        return CurrencyReceiver(ConverterApi.create())
    }
}