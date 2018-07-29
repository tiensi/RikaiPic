package com.tiensinoakuma.rikaipic.ui

import android.graphics.Bitmap
import com.tiensinoakuma.rikaipic.api.pexels.PexelsPhoto

class RikaiPicContract {

    interface View {
        fun showErrorRetry(error: Throwable, runnable: Runnable)
        fun showPhoto(pexelsPhoto: PexelsPhoto)
        fun showLoading(show: Boolean)
        fun showWikiForText(text: String)
        fun addLabel(text: String)
    }

    interface Presenter : TranslationAdapter.TranslationListener {
        fun onCreate()
        fun onImageLoaded(resource: Bitmap)
        fun onDestroy()
    }
}