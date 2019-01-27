package com.lotuss.tinkoffconverter.data

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.lotuss.tinkoffconverter.MyApplication
import com.lotuss.tinkoffconverter.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

const val COMPACT = "ultra"

class RateReceiver {

    var firstToSecond: Double = 1.0
    var secondToFirst: Double = 1.0

    private var map: MutableMap<String, Double> = mutableMapOf()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val converterReceiver: ConverterReceiver = ConverterReceiverProvider.provide()

    fun setCurrentRates(firstId: String, secondId: String) {
        val gson: Gson = GsonBuilder().create()
        compositeDisposable.add(converterReceiver.getRates(
            firstId + "_" + secondId + "," + secondId + "_" + firstId, COMPACT)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .timeout(2, TimeUnit.SECONDS)
            .subscribe({ json ->
                firstToSecond = gson.fromJson( json.asJsonObject
                    .get(firstId + "_" + secondId), Double::class.java)
                map[firstId + "_" + secondId] = firstToSecond
                secondToFirst = gson.fromJson(json.asJsonObject
                    .get(secondId + "_" + firstId), Double::class.java)
                map[secondId + "_" + firstId] = secondToFirst
                saveRatesToCash()
            },{
                // Triggered by poor connection
                if(loadRatesFromCash() && seachForRatesInCash(firstId, secondId)){
                    firstToSecond = map[firstId + "_" + secondId]!!
                    secondToFirst = map[secondId + "_" + firstId]!!
                }else Toast.makeText(MyApplication.getContext(),
                    R.string.no_internet_connection, Toast.LENGTH_LONG).show()
            }))
    }

    private fun saveRatesToCash() {
        val sharedPreferences = MyApplication.getContext()
                        .getSharedPreferences("RateReceiver", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(map)
        editor.putString("Rates", json)
        editor.apply()
    }

    private fun loadRatesFromCash(): Boolean{
        val sharedPreferences: SharedPreferences = MyApplication.getContext().getSharedPreferences("RateReceiver", AppCompatActivity.MODE_PRIVATE)
        val gson = Gson()
        val json: String = sharedPreferences.getString("Rates", null)!!
        val type = object : TypeToken<MutableMap<String, Double>>() {}.type
        map = gson.fromJson(json, type)
        return map.isNotEmpty()
    }

    fun seachForRatesInCash(firstId: String, secondId: String): Boolean {
        return map[firstId + "_" + secondId] != null
    }
}