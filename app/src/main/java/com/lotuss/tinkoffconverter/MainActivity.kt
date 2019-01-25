package com.lotuss.tinkoffconverter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lotuss.tinkoffconverter.data.Currency
import com.lotuss.tinkoffconverter.data.CurrencyReceiver
import com.lotuss.tinkoffconverter.data.CurrencyReceiverProvider
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val currencyReceiver: CurrencyReceiver = CurrencyReceiverProvider.provide()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gson  = GsonBuilder().create()
        val currencies = mutableListOf<Currency>()
        var currency = Currency("", "", "")
        compositeDisposable.add(currencyReceiver.getCurrencyList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe{ json ->
                json.asJsonObject.get("results").asJsonObject.keySet().forEach {
                    currency = gson.fromJson(json.asJsonObject.get("results").asJsonObject[it], Currency::class.java)
                    currencies.add(currency)
                }
            })
    }
}
