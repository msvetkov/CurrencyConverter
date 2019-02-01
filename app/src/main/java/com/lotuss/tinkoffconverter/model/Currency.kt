package com.lotuss.tinkoffconverter.model

import com.google.gson.annotations.SerializedName

data class Currency(
    @SerializedName("id") var currencyId: String
)

data class Rates(
    var firstToSecond: Double,
    var secondToFirst: Double
)