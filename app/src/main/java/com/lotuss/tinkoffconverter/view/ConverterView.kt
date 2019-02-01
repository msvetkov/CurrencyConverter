package com.lotuss.tinkoffconverter.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.lotuss.tinkoffconverter.model.Rates

@StateStrategyType(AddToEndSingleStrategy::class)
interface ConverterView: MvpView {

    fun setCurrencyList(currencies: MutableList<String>)

    fun setRates(rates: Rates)

    fun showProgress()

    fun hideProgress()

    fun showErrorView()

    fun hideErrorView()

    fun showConverterView()

    fun hideConverterView()

    @StateStrategyType(SkipStrategy::class)
    fun showErrorMessage()
}