package com.lotuss.tinkoffconverter.data

object ConverterReceiverProvider{

    fun provide(): ConverterReceiver{
        return ConverterReceiver(ConverterApi.create())
    }
}