package com.tiensinoakuma.rikaipic.dagger

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector
import com.tiensinoakuma.rikaipic.BuildConfig
import com.tiensinoakuma.rikaipic.RikaiPicApp
import com.tiensinoakuma.rikaipic.dagger.scopes.AppScope
import com.tiensinoakuma.rikaipic.model.RikaiLanguage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(val application: RikaiPicApp) {

    @Provides
    internal fun provideApplication(): RikaiPicApp {
        return application
    }

    @AppScope
    @Provides
    fun provideVisionCloudLabelDetector(): FirebaseVisionCloudLabelDetector =
            FirebaseVision.getInstance().visionCloudLabelDetector

    @AppScope
    @Provides
    fun provideVisionLabelDetector(): FirebaseVisionLabelDetector =
            FirebaseVision.getInstance().visionLabelDetector

    @AppScope
    @Provides
    fun provideSupportedLanguages(): List<String> {
        return listOf(
                ("en"),
                ("zh-CN"),
                ("zh-TW"),
                ("ar"),
                ("es"),
                ("ja"),
                ("de"),
                ("pt"),
                ("ru"),
                ("fr"),
                ("hi"),
                ("it")
        )
    }

    @AppScope
    @Provides
    fun provideSelectedLanguage(prefs: SharedPreferences): String {
        return prefs.getString(PREF_SELECTED_LANGUAGE, BuildConfig.DEFAULT_LANGUAGE)
    }

    @AppScope
    @Provides
    fun provideSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }

    companion object {
        const val PREF_SELECTED_LANGUAGE = "pref_selected_language"
    }
}