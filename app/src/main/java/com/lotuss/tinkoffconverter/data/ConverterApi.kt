package com.lotuss.tinkoffconverter.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ConverterApi {

    companion object {
        fun create():ConverterApi{
            val gson: Gson = GsonBuilder().setLenient().create()
            val retrofit: Retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("https://free.currencyconverterapi.com/api/v6/")
                .build()
            return retrofit.create(ConverterApi::class.java)
        }
    }

    @GET("currencies")
    fun getCurrencyList():io.reactivex.Observable<Result>
}