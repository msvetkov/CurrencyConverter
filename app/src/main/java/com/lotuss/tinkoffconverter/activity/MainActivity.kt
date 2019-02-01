package com.lotuss.tinkoffconverter.activity

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.lotuss.tinkoffconverter.R
import com.lotuss.tinkoffconverter.model.Rates
import com.lotuss.tinkoffconverter.presenter.ConverterPresenter
import com.lotuss.tinkoffconverter.view.ConverterView
import java.lang.NumberFormatException

class MainActivity : MvpAppCompatActivity(), ConverterView, AdapterView.OnItemSelectedListener {

    @InjectPresenter
    lateinit var converterPresenter: ConverterPresenter

    private var rates = Rates(1.0, 1.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val firstEditText:EditText = this.first_input
        val secondEditText: EditText = this.second_input

        convertValues(firstEditText, secondEditText)

        first_spinner.onItemSelectedListener = this
        second_spinner.onItemSelectedListener = this

        retry.setOnClickListener { converterPresenter.startLoadCurrencyList() }
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
        val adapter: ArrayAdapter<String>  = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        first_spinner.adapter = adapter
        second_spinner.adapter = adapter
        first_spinner.setSelection(141)
        second_spinner.setSelection(8)
    }

    override fun showErrorMessage() {
        Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show()
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
}
