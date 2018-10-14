package com.tiensinoakuma.rikaipic.dagger

import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.tiensinoakuma.rikaipic.BuildConfig
import com.tiensinoakuma.rikaipic.RikaiPicApp
import com.tiensinoakuma.rikaipic.api.firebase.FirebaseVisionApi
import com.tiensinoakuma.rikaipic.api.g_translate.GTranslateApi
import com.tiensinoakuma.rikaipic.api.g_translate.GTranslateSource
import com.tiensinoakuma.rikaipic.api.pexels.PexelsApi
import com.tiensinoakuma.rikaipic.dagger.scopes.AppScope
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetModule {
    @Provides
    @AppScope
    fun provideConnectionPool(): ConnectionPool {
        return ConnectionPool()
    }

    @Provides
    @AppScope
    fun provideHttpCache(application: RikaiPicApp): Cache {
        val cacheSize = 10 * 1024 * 1024
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Provides
    @AppScope
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return gsonBuilder.create()
    }

    @Provides
    @AppScope
    fun provideOkHttpClient(pool: ConnectionPool, cache: Cache): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.connectionPool(pool)
        client.cache(cache)
        if (BuildConfig.DEBUG == true) {
            client.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }
        return client.build()
    }

    @Provides
    @AppScope
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @AppScope
    fun provideGTranslateApi(okHttpClient: OkHttpClient, gsonConverterFactory: GsonConverterFactory): GTranslateApi {
        return Retrofit.Builder()
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .baseUrl(BuildConfig.GOOGLE_TRANSLATE_ENDPOINT)
                .client(okHttpClient)
                .build()
                .create(GTranslateApi::class.java)
    }


    @Provides
    @AppScope
    fun providePexelsApi(okHttpClient: OkHttpClient, gsonConverterFactory: GsonConverterFactory): PexelsApi {
        return Retrofit.Builder()
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .baseUrl(BuildConfig.PEXELS_ENDPOINT)
                .client(okHttpClient)
                .build()
                .create(PexelsApi::class.java)
    }

    @Provides
    @AppScope
    fun provideGTranslateSource(translationApi: GTranslateApi): GTranslateSource {
        return GTranslateSource(translationApi)
    }

    @Provides
    @AppScope
    fun provideFirebaseVisionApi(
            visionLabelDetector: FirebaseVisionLabelDetector,
            visionCloudLabelDetector: FirebaseVisionCloudLabelDetector
    ): FirebaseVisionApi {
        return FirebaseVisionApi(visionLabelDetector, visionCloudLabelDetector)
    }
}