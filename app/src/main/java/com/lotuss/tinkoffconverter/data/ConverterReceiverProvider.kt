package com.lotuss.tinkoffconverter.data

object ConverterReceiverProvider{

    //To provide a single object of ConverterReceiver class
    fun provide(): ConverterReceiver{
        return ConverterReceiver(ConverterApi.create())
    }
}