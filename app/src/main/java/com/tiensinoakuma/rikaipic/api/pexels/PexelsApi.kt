package com.tiensinoakuma.rikaipic.api.pexels

import com.tiensinoakuma.rikaipic.BuildConfig
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PexelsApi {
    @GET("v1/curated")
    fun getCuratedPics(
            @Query("per_page") perPage: Int = 10,
            @Query("page") page: Int = 1,
            @Header("Authorization") token: String = BuildConfig.PEXELS_API_KEY
    ): Single<PexelsResponse>
}