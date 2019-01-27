package com.lotuss.tinkoffconverter

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.AdapterView
import com.lotuss.tinkoffconverter.data.*
import android.text.Editable
import android.text.TextWatcher
import java.util.concurrent.TimeUnit
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val converterReceiver: ConverterReceiver = ConverterReceiverProvider.provide()
    private var currencies: MutableList<String> = mutableListOf()
    private val rateReceiver: RateReceiver = RateReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadCurrencyList()

        val firstEditText:EditText = this.first_input
        val secondEditText: EditText = this.second_input
        convertValues(firstEditText, secondEditText)

        retry.setOnClickListener {
                loadCurrencyList()
        }
    }

    private fun convertValues(first: EditText, second: EditText){
        first.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {Log.d("convertValues", "something")}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(currentFocus == first)
                    if (s.isNotEmpty()){
                        Log.d("convertValues", (s.toString().toDouble() * rateReceiver.firstToSecond).toString())
                        second.setText((s.toString().toDouble() * rateReceiver.firstToSecond).toString())
                    }else second.setText("")
            }
        })
        second.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {Log.d("convertValues", "something")}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(currentFocus == second)
                    if (s.isNotEmpty()) {
                        first.setText((s.toString().toDouble() * rateReceiver.secondToFirst).toString())
                    }else first.setText("")
            }
        })
    }

    // Loading currency list from network
    private fun loadCurrencyList(){
        if(connection_error_view.visibility == View.VISIBLE){
            connection_error_view.visibility = View.GONE
            progress.visibility = View.VISIBLE
        }

        val gson: Gson  = GsonBuilder().create()
        var currency: Currency
        compositeDisposable.add(converterReceiver.getCurrencyList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .timeout(2, TimeUnit.SECONDS)
            .subscribe({ json ->
                json.asJsonObject.get("results").asJsonObject.keySet().forEach {
                    currency = gson.fromJson(json.asJsonObject.get("results").asJsonObject[it], Currency::class.java)
                    currencies.add(currency.currencyId)
                }
                setListToSpinners()
                this.progress.visibility = View.GONE
                this.converter_view.visibility = View.VISIBLE
                saveCurrenciesToCash(applicationContext)
            },{
                // Triggered by poor connection
                if (loadCurrenciesFromCash()) {
                    progress.visibility = View.GONE
                    converter_view.visibility = View.VISIBLE
                    setListToSpinners()
                }else{
                    progress.visibility = View.GONE
                    connection_error_view.visibility = View.VISIBLE
                }
            }))
    }

    private fun saveCurrenciesToCash(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MainActivity", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(currencies)
        editor.putString("Currencies", json)
        editor.apply()
    }

    private fun loadCurrenciesFromCash(): Boolean{
        val sharedPreferences: SharedPreferences = getSharedPreferences("MainActivity", MODE_PRIVATE)
        val gson = Gson()
        val json: String? = sharedPreferences.getString("Currencies", "")
        val type = object : TypeToken<ArrayList<String>>() {}.type
        if(json != "")
            currencies = gson.fromJson(json, type)
        return currencies.isNotEmpty()
    }

    private fun setListToSpinners(){
        val adapter: ArrayAdapter<String>  = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        first_spinner.adapter = adapter
        second_spinner.adapter = adapter

        first_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                rateReceiver.setCurrentRates(currencies[position], currencies[second_spinner.selectedItemPosition])
            }
        }
        second_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                rateReceiver.setCurrentRates(currencies[first_spinner.selectedItemPosition], currencies[position])
            }
        }
    }
}
