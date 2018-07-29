package com.tiensinoakuma.rikaipic.dagger.rikai_image

import com.tiensinoakuma.rikaipic.api.firebase.FirebaseVisionApi
import com.tiensinoakuma.rikaipic.api.g_translate.GTranslateApi
import com.tiensinoakuma.rikaipic.api.pexels.PexelsApi
import com.tiensinoakuma.rikaipic.ui.RikaiPicContract
import com.tiensinoakuma.rikaipic.ui.RikaiPicPresenter
import dagger.Module
import dagger.Provides

@Module
class RikaiPicModule(private val view: RikaiPicContract.View) {
    @Provides
    internal fun providesView(): RikaiPicContract.View {
        return view
    }

    @Provides
    internal fun providesPresenter(
            view: RikaiPicContract.View,
            translateApi: GTranslateApi,
            pexelsApi: PexelsApi,
            firebaseVisionApi: FirebaseVisionApi
    ): RikaiPicContract.Presenter {
        return RikaiPicPresenter(view, translateApi, pexelsApi, firebaseVisionApi)
    }
}