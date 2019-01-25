package com.lotuss.tinkoffconverter.data

import com.google.gson.annotations.SerializedName

data class Currency(
    @SerializedName("currencyName") var currencyName: String,
    @SerializedName("currencySymbol") var currencySymbol: String?,
    @SerializedName("id") var currencyId: String
)