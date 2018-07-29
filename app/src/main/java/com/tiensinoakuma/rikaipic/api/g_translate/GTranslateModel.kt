package com.tiensinoakuma.rikaipic.api.g_translate

import com.google.gson.annotations.SerializedName

data class GTranslateResponse(val data: GData)

data class GData(val translations: List<TranslatedText>)

data class TranslatedText(@SerializedName("detectedSourceLanguage") val detectedSourceLanguage: String?, @SerializedName("translatedText") val translatedText: String)