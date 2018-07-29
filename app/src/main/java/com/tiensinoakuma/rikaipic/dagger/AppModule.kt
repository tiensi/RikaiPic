package com.tiensinoakuma.rikaipic.dagger

import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector
import com.tiensinoakuma.rikaipic.RikaiPicApp
import com.tiensinoakuma.rikaipic.dagger.scopes.AppScope
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
}