package com.lotuss.currencyconverter.data

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.lotuss.currencyconverter.MyApplication
import com.lotuss.currencyconverter.model.Currency
import com.lotuss.currencyconverter.presenter.ConverterPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class CurrencyListProvider(private val presenter: ConverterPresenter) {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val converterReceiver: ConverterReceiver =
        ConverterReceiverProvider.provide()
    private var currencies: MutableList<String> = mutableListOf()

    // To parse response
    private val gson: Gson = GsonBuilder().create()


    fun loadCurrencyList() {
        // Unpacking the response
        compositeDisposable.add(converterReceiver.getCurrencyList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
               successfullyLoaded(it)
            }, {
                loadingError()
            }))
    }

    private fun successfullyLoaded(json: JsonElement) {
        var currency: Currency
        json.asJsonObject.get("results").asJsonObject.keySet().forEach {
            currency = gson.fromJson(json.asJsonObject.get("results").asJsonObject[it], Currency::class.java)
            currencies.add(currency.currencyId)
        }
        saveCurrenciesToCache()
        presenter.finishLoadCurrencyList(currencies)
    }

    private fun loadingError() {
        if (loadCurrenciesFromCache()) {
            presenter.finishLoadCurrencyListOffline(currencies)
        } else presenter.errorLoadCurrencyList()
    }

    private fun saveCurrenciesToCache() {
        val sharedPreferences = MyApplication.getContext().getSharedPreferences("MainActivity", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = gson.toJson(currencies)
        editor.putString("Currencies", json)
        editor.apply()
    }

    // In case of poor connection
    private fun loadCurrenciesFromCache(): Boolean{
        val sharedPreferences: SharedPreferences = MyApplication.getContext().getSharedPreferences("MainActivity", AppCompatActivity.MODE_PRIVATE)
        val json: String? = sharedPreferences.getString("Currencies", "")
        val type = object : TypeToken<ArrayList<String>>() {}.type
        if(json != "")
            currencies = gson.fromJson(json, type)
        return currencies.isNotEmpty()
    }
}