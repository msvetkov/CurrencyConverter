package com.lotuss.currencyconverter.data

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.lotuss.currencyconverter.MyApplication
import com.lotuss.currencyconverter.model.Rates
import com.lotuss.currencyconverter.presenter.ConverterPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

// Constant value of request parameter
const val COMPACT = "ultra"

class RateProvider (private val presenter: ConverterPresenter) {

    private val rates = Rates(1.0, 1.0)

    private var ratesMap: MutableMap<String, Double> = mutableMapOf()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val converterReceiver: ConverterReceiver =
        ConverterReceiverProvider.provide()

    // To parse answer from server
    private val gson: Gson = GsonBuilder().create()

    init {
        loadRatesFromCache()
    }

    // Assign a pair of ratesMap from network or cache
    fun loadCurrentRates(firstId: String, secondId: String) {
        compositeDisposable.add(converterReceiver.getRates(
            firstId + "_" + secondId + "," + secondId + "_" + firstId, COMPACT
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                successfullyLoaded(it, firstId, secondId)
            },{
                // Triggered by poor connection
                loadingError(firstId, secondId)
            }))
    }

    private fun successfullyLoaded(json: JsonElement, firstId: String, secondId: String) {
        rates.firstToSecond = gson.fromJson( json.asJsonObject
            .get(firstId + "_" + secondId), Double::class.java)
        ratesMap[firstId + "_" + secondId] = rates.firstToSecond
        rates.secondToFirst = gson.fromJson(json.asJsonObject
            .get(secondId + "_" + firstId), Double::class.java)
        ratesMap[secondId + "_" + firstId] = rates.secondToFirst
        saveRatesToCache()
        presenter.finishLoadRates(rates)
        presenter.addItemToHistory(firstId + "_" + secondId)
    }

    private fun loadingError(firstId: String, secondId: String) {
        if(searchForRatesInCache(firstId, secondId)) {
            rates.firstToSecond = ratesMap[firstId + "_" + secondId]!!
            rates.secondToFirst = ratesMap[secondId + "_" + firstId]!!
            presenter.finishLoadRatesOffline(rates)
            presenter.addItemToHistory(firstId + "_" + secondId)
        } else presenter.errorLoadRates()
    }

    private fun saveRatesToCache() {
        val sharedPreferences = MyApplication.getContext()
                        .getSharedPreferences("RateReceiver", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = gson.toJson(ratesMap)
        editor.putString("Rates", json)
        editor.apply()
    }

    // Load ratesMap with ratesMap from cache
    private fun loadRatesFromCache() {
        val sharedPreferences: SharedPreferences = MyApplication.getContext()
                        .getSharedPreferences("RateReceiver", AppCompatActivity.MODE_PRIVATE)
        val json: String? = sharedPreferences.getString("Rates", "")
        val type = object : TypeToken<MutableMap<String, Double>>() {}.type
        ratesMap = gson.fromJson(json, type)
        if(json != "")
            ratesMap = gson.fromJson(json, type)
    }

    // To display USD and EUR to RUB
    fun loadImportantRates(){
        Log.d("TAGTAG", searchForRatesInCache("USD", "RUB").toString())
        var usd: Double
        var eur: Double
        compositeDisposable.add(converterReceiver.getRates("USD_RUB,EUR_RUB", COMPACT)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                usd= gson.fromJson( it.asJsonObject
                    .get("USD_RUB"), Double::class.java)
                eur = gson.fromJson( it.asJsonObject
                    .get("EUR_RUB"), Double::class.java)
                presenter.finishLoadImportantRates(usd, eur)
            },{
                if (searchForRatesInCache("USD","RUB") && searchForRatesInCache("EUR","RUB")) {
                    usd = ratesMap["USD_RUB"]!!
                    eur = ratesMap["EUR_RUB"]!!
                    presenter.finishLoadImportantRates(usd, eur)
                } else{
                    presenter.errorLoadImportantRates()}
            }))
    }

    private fun searchForRatesInCache(firstId: String, secondId: String): Boolean {
        return ratesMap[firstId + "_" + secondId] != null
    }
}