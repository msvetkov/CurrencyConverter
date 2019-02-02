package com.lotuss.currencyconverter.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.lotuss.currencyconverter.data.CurrencyListProvider
import com.lotuss.currencyconverter.data.RateProvider
import com.lotuss.currencyconverter.model.Rates
import com.lotuss.currencyconverter.view.ConverterView

@InjectViewState
class ConverterPresenter: MvpPresenter<ConverterView>() {

    private val currencyListProvider = CurrencyListProvider(this)
    private val rateProvider = RateProvider(this)

    private var isPreviousRateError: Boolean = false

    init {
        startLoadCurrencyList()
        startLoadImportantRates()
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

    fun finishLoadCurrencyList(currencies: MutableList<String>){
        viewState.hideProgress()
        viewState.hideErrorView()
        viewState.setCurrencyList(currencies)
        viewState.showConverterView()
        viewState.setHistoryAdapter()
    }

    fun finishLoadCurrencyListOffline(currencies: MutableList<String>){
        viewState.hideProgress()
        viewState.showOfflineMessage()
        viewState.setCurrencyList(currencies)
        viewState.showConverterView()
        viewState.setHistoryAdapter()
    }

    fun startLoadImportantRates(){
        rateProvider.loadImportantRates()
    }

    fun errorLoadImportantRates() {
        viewState.hideImportantRatesView()
    }

    fun finishLoadImportantRates(usd: Double, eur: Double) {
        viewState.setImportantCurses(usd, eur)
        viewState.showImportantRatesView()
    }

    fun startLoadRates(firstId: String, secondId: String){
        rateProvider.loadCurrentRates(firstId, secondId)
    }

    fun errorLoadRates(){
        viewState.showErrorMessage()
        isPreviousRateError = true
        viewState.returnToPreviousSelections()
    }

    fun finishLoadRates(rates: Rates){
        viewState.setRates(rates)
        viewState.setSelectionToBackUp()
        viewState.updateEditText()
        isPreviousRateError = false
    }

    fun finishLoadRatesOffline(rates: Rates){
        viewState.setRates(rates)
        viewState.setSelectionToBackUp()
        if (!isPreviousRateError)
            viewState.showOfflineMessage()
        isPreviousRateError = false
        viewState.updateEditText()
    }

    fun addItemToHistory(item: String){
        viewState.addToHistoryList(item)
    }
}