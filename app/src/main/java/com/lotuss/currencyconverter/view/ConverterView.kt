package com.lotuss.currencyconverter.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.lotuss.currencyconverter.model.Rates

@StateStrategyType(AddToEndSingleStrategy::class)
interface ConverterView: MvpView {

    fun setCurrencyList(currencies: MutableList<String>)

    fun setRates(rates: Rates)

    fun setHistoryAdapter()

    fun addToHistoryList(item: String)

    fun showProgress()

    fun hideProgress()

    fun showErrorView()

    fun hideErrorView()

    fun showConverterView()

    fun hideConverterView()

    fun showImportantRatesView()

    fun hideImportantRatesView()

    @StateStrategyType(SkipStrategy::class)
    fun showErrorMessage()

    @StateStrategyType(SkipStrategy::class)
    fun showOfflineMessage()

    fun setImportantCurses(usd: Double, eur: Double)

    fun setSelectionToBackUp()

    fun returnToPreviousSelections()

    fun updateEditText()
}