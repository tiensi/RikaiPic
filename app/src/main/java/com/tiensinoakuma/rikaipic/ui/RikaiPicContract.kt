package com.tiensinoakuma.rikaipic.ui

import android.graphics.Bitmap
import com.tiensinoakuma.rikaipic.api.pexels.PexelsPhoto

class RikaiPicContract {

    interface View {
        fun showErrorRetry(error: Throwable, runnable: Runnable)
        fun showPhoto(pexelsPhoto: PexelsPhoto)
        fun showLoading(show: Boolean)
        fun showWikiForText(text: String)
        fun showLabels(labels: List<String>)
        fun showImagesReady()
        fun showLoadingScreen()
        fun enableFab(enable: Boolean)
        fun clearData()
        fun setSupportedLanguages(supportedLanguages: List<String>, defaultLanguage: String)
        fun enableSupportedLanguages(isClickable: Boolean)
        fun enableLanguageClick(isClickable: Boolean)
    }

    interface Presenter : TranslationAdapter.TranslationListener, LanguageAdapter.LanguageListener {
        fun onCreate()
        fun onLargeImageLoaded(resource: Bitmap)
        fun onDestroy()
        fun onNextSelected()
        fun onOriginalImageLoaded()
    }
}