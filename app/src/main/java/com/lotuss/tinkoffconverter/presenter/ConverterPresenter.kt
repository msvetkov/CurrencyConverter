package com.lotuss.tinkoffconverter.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.lotuss.tinkoffconverter.data.CurrencyListProvider
import com.lotuss.tinkoffconverter.data.RateProvider
import com.lotuss.tinkoffconverter.model.Rates
import com.lotuss.tinkoffconverter.view.ConverterView

@InjectViewState
class ConverterPresenter: MvpPresenter<ConverterView>() {

    private val currencyListProvider = CurrencyListProvider(this)
    private val rateProvider = RateProvider(this)

    init {
        startLoadCurrencyList()
    }

    fun startLoadCurrencyList(){
        viewState.showProgress()
        viewState.hideErrorView()
        currencyListProvider.loadCurrencyList()
    }

    fun errorLoadCurrencyList(){
        viewState.hideProgress()
        viewState.showErrorView()
    }

    fun finishLoadCurrencyListOffline(currencies: MutableList<String>){
        viewState.hideProgress()
        viewState.showErrorMessage()
        viewState.setCurrencyList(currencies)
        viewState.showConverterView()
    }

    fun finishLoadCurrencyList(currencies: MutableList<String>){
        viewState.hideProgress()
        viewState.hideErrorView()
        viewState.setCurrencyList(currencies)
        viewState.showConverterView()
    }

    fun startLoadRates(firstId: String, secondId: String){
        rateProvider.loadCurrentRates(firstId, secondId)
    }

    fun errorLoadRates(){
        viewState.showErrorMessage()
    }

    fun finishLoadRates(rates: Rates){
        viewState.setRates(rates)
    }
}