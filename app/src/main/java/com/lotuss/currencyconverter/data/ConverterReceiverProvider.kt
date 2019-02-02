package com.lotuss.currencyconverter.data

object ConverterReceiverProvider{

    //To provide a single object of ConverterReceiver class
    fun provide(): ConverterReceiver {
        return ConverterReceiver(ConverterApi.create())
    }
}