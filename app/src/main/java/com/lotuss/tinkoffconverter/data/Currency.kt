package com.lotuss.tinkoffconverter.data

import com.google.gson.annotations.SerializedName


data class Result(
    @SerializedName("results") val results: List<Currency>
)

data class Currency(
    @SerializedName("currencyNam") val currencyName: String,
    @SerializedName("currencySymbol") val currencySymbol: String,
    @SerializedName("id") val currencyId: String
)