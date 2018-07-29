package com.tiensinoakuma.rikaipic.api.g_translate

import io.reactivex.Single
import retrofit2.http.*

interface GTranslateApi {
    @POST("language/translate/v2")
    fun getTranslation(
            @Query("q") textToTranslate: String,
            @Query("source") sourceLanguage: String,
            @Query("target") targetLanguage: String,
            @Query("key") key: String
    ): Single<GTranslateResponse>
}