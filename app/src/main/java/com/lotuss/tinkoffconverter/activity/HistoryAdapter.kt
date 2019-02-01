package com.lotuss.tinkoffconverter.activity

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import com.lotuss.tinkoffconverter.R
import kotlinx.android.synthetic.main.history_item.view.*

class HistoryAdapter(private val layoutInflater: LayoutInflater, private val items: MutableList<String>):
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = layoutInflater.inflate(R.layout.history_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setSpinnerPosition(first: Spinner, second: Spinner) {

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val historyViewHolder: HistoryViewHolder = holder as HistoryViewHolder
        val first = items[position].substringBefore("_")
        val second = items[position].substringAfter("_")
        historyViewHolder.first.text = first
        historyViewHolder.second.text = second
    }

    class HistoryViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val first: TextView = v.first_currency
        val second: TextView = v.second_currency
    }

}