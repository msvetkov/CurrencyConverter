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
        rateProvider.loadImportantRates()
    }

    fun addItemToHistory(item: String){
        viewState.addToHistoryList(item)
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
        viewState.showOfflineMessage()
        viewState.setCurrencyList(currencies)
        viewState.showConverterView()
        viewState.setHistoryAdapter()
    }

    fun finishLoadCurrencyList(currencies: MutableList<String>){
        viewState.hideProgress()
        viewState.hideErrorView()
        viewState.setCurrencyList(currencies)
        viewState.showConverterView()
        viewState.setHistoryAdapter()
    }

    fun finishLoadImportantRates(usd: Double, eur: Double) {
        viewState.setImportantCurses(usd, eur)
        viewState.showImportantRatesView()
    }

    fun errorLoadImportantRates() {
        viewState.hideImportantRatesView()
    }

    fun startLoadRates(firstId: String, secondId: String){
        rateProvider.loadCurrentRates(firstId, secondId)
    }

    fun errorLoadRates(){
        viewState.showErrorMessage()
        viewState.returnToPreviousSelections()
    }

    fun finishLoadRates(rates: Rates){
        viewState.setRates(rates)
        viewState.setSelectionToBackUp()
        viewState.updateEditText()
    }

    fun finishLoadRatesOffline(rates: Rates){
        viewState.setRates(rates)
        viewState.setSelectionToBackUp()
        viewState.showOfflineMessage()
        viewState.updateEditText()
    }
}