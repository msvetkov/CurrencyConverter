package com.lotuss.tinkoffconverter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

        compositeDisposable.add(currencyReceiver.getCurrencyList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe{
                Log.d("TAG", it.results[0].currencyId)
            })
    }
}
