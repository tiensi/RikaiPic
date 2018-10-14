package com.tiensinoakuma.rikaipic.api.g_translate

import com.tiensinoakuma.rikaipic.BuildConfig
import com.tiensinoakuma.rikaipic.ui.RikaiPicPresenter
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Source for retrieving translations from english, if it already exists then use existing translation
 */
class GTranslateSource(val translationApi: GTranslateApi) {
    val translationMap: MutableMap<String, MutableMap<String, String>> = HashMap()

    fun getTranslation(query: String, targetLanguage: String): Single<String> {
        if (targetLanguage == BuildConfig.DEFAULT_LANGUAGE || query == "¯\\ _（ツ）_ /¯") {
            return Single.just(query)
        }
        val savedValue = translationMap[targetLanguage]?.get(query)
        if (savedValue != null) {
            return Single.just(savedValue)
        }
        return translationApi.getTranslation(
                query,
                BuildConfig.DEFAULT_LANGUAGE,
                targetLanguage,
                BuildConfig.GOOGLE_TRANSLATE_API_KEY)
                .map { it.data.translations.first().translatedText }
                .doOnSuccess {
                    if (translationMap[targetLanguage] == null) {
                        translationMap[targetLanguage] = HashMap()
                    }
                    translationMap[targetLanguage]!![query] = it
                }
                .subscribeOn(Schedulers.io())
    }
}