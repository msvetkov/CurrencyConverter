package com.lotuss.currencyconverter.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.lotuss.currencyconverter.R
import com.lotuss.currencyconverter.model.Rates
import com.lotuss.currencyconverter.presenter.ConverterPresenter
import com.lotuss.currencyconverter.view.ConverterView
import java.lang.NumberFormatException


class MainActivity : MvpAppCompatActivity(), ConverterView, AdapterView.OnItemSelectedListener {

    @InjectPresenter
    lateinit var converterPresenter: ConverterPresenter

    private var rates = Rates(1.0, 1.0)

    private lateinit var firstEditText: EditText
    private lateinit var secondEditText: EditText

    private val historyList = mutableListOf<String>()
    private lateinit var historyAdapter: HistoryAdapter

    // Adapter for spinners
    private lateinit var arrayAdapter: ArrayAdapter<String>

    // To return in case of a bad connection
    private var previousSelectionFirst: Int = 141
    private var previousSelectionSecond: Int = 8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firstEditText = this.first_input
        secondEditText = this.second_input

        convertValues(firstEditText, secondEditText)

        first_spinner.onItemSelectedListener = this
        second_spinner.onItemSelectedListener = this


        retry.setOnClickListener {
            converterPresenter.startLoadCurrencyList()
            converterPresenter.startLoadImportantRates()
        }
    }

    // Set recycler view with history of currencies
    override fun setHistoryAdapter() {
        historyAdapter = HistoryAdapter(
            this.layoutInflater,
            historyList,
            first_spinner,
            second_spinner,
            arrayAdapter
        )
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        history_list.layoutManager = layoutManager
        history_list.adapter = historyAdapter
    }

    override fun addToHistoryList(item: String) {
        historyList.remove(item)
        historyList.add(0, item)
        historyAdapter.notifyDataSetChanged()
    }

    override fun showImportantRatesView() {
        important_rates_view.visibility = View.VISIBLE
    }
    override fun hideImportantRatesView() {
        important_rates_view.visibility = View.GONE
    }

    override fun setImportantCurses(usd: Double, eur: Double) {
        this.usd.text = getString(R.string.format, usd)
        this.eur.text = getString(R.string.format, eur)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        converterPresenter.startLoadRates(first_spinner.selectedItem as String, second_spinner.selectedItem as String)
    }

    override fun showProgress() {
        progress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress.visibility = View.GONE
    }

    override fun showErrorView() {
        connection_error_view.visibility = View.VISIBLE
    }

    override fun hideErrorView() {
        connection_error_view.visibility = View.GONE
    }

    override fun showConverterView() {
        converter_view.visibility = View.VISIBLE
    }

    override fun hideConverterView() {
        converter_view.visibility = View.GONE
    }

    override fun setRates(rates: Rates) {
        this.rates = rates
    }

    override fun setCurrencyList(currencies: MutableList<String>) {
        arrayAdapter   = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        first_spinner.adapter = arrayAdapter
        second_spinner.adapter = arrayAdapter
        first_spinner.setSelection(previousSelectionFirst) //RUB
        second_spinner.setSelection(previousSelectionSecond) //USD
    }

    override fun showErrorMessage() {
        Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show()
    }

    override fun showOfflineMessage() {
        Toast.makeText(this, R.string.loaded_offline, Toast.LENGTH_LONG).show()
    }

    override fun setSelectionToBackUp() {
        previousSelectionFirst = first_spinner.selectedItemPosition
        previousSelectionSecond = second_spinner.selectedItemPosition
    }

    override fun returnToPreviousSelections() {
        first_spinner.setSelection(previousSelectionFirst)
        second_spinner.setSelection(previousSelectionSecond)
    }

    // Set listeners for both fields
    private fun convertValues(first: EditText, second: EditText) {
        first.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(currentFocus == first)
                    setResult(second, s, rates.firstToSecond)
            }
        })
        second.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(currentFocus == second)
                    setResult(first, s, rates.secondToFirst)
            }
        })
    }

    private fun setResult(editText: EditText, s: CharSequence, rate: Double) {
        if (s.isNotEmpty()) {
            try {
                editText.setText((s.toString().toDouble() * rate).toString())
            }catch (e: NumberFormatException){
                Toast.makeText(applicationContext,
                    R.string.error_number_format, Toast.LENGTH_LONG).show()
            }
        }else editText.setText("")
    }

    override fun updateEditText() {
        val s = firstEditText.text
        setResult(secondEditText, s, rates.firstToSecond)
    }
}
