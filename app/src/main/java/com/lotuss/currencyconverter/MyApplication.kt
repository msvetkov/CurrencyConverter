package com.lotuss.currencyconverter

import android.app.Application
import android.content.Context

class MyApplication: Application(){

    init {
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null

        fun getContext(): Context {
            return instance!!.applicationContext
        }
    }
}