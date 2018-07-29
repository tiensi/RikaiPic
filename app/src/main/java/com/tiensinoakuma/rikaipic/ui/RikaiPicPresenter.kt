package com.tiensinoakuma.rikaipic.ui

import android.graphics.Bitmap
import android.text.TextUtils
import com.tiensinoakuma.rikaipic.BuildConfig
import com.tiensinoakuma.rikaipic.api.firebase.FirebaseVisionApi
import com.tiensinoakuma.rikaipic.api.g_translate.GTranslateApi
import com.tiensinoakuma.rikaipic.api.pexels.PexelsApi
import com.tiensinoakuma.rikaipic.api.pexels.PexelsPhoto
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class RikaiPicPresenter(
        private val view: RikaiPicContract.View,
        private val translateApi: GTranslateApi,
        private val pexelsApi: PexelsApi,
        private val firebaseVisionApi: FirebaseVisionApi
) : RikaiPicContract.Presenter {

    private val imageLoadDisposable = SerialDisposable()
    private val translationsDisposable = SerialDisposable()
    private val pexelsPhotoList: MutableList<PexelsPhoto> = ArrayList()
    private var photoTranslations: MutableList<String> = ArrayList()

    override
    fun onCreate() {
        //Retrieve images from pexels api
        view.showLoading(true)
        imageLoadDisposable.set(
                pexelsApi.getCuratedPics()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doAfterTerminate {
                            view.showLoading(false)
                        }
                        .subscribe(
                                { pexelsResponse ->
                                    pexelsPhotoList.clear()
                                    pexelsPhotoList.addAll(pexelsResponse.photos)
                                    //todo apply this to a fragmentlist
                                    view.showPhoto(pexelsPhotoList[0])
                                },
                                { error ->
                                    Timber.e(error)
                                    view.showErrorRetry(error, Runnable { onCreate() })
                                }
                        )
        )
    }

    override fun onImageLoaded(resource: Bitmap) {
        photoTranslations.clear()
        translationsDisposable.set(
                firebaseVisionApi.getAllVisionLabels(resource)
                        .doAfterTerminate { view.showLoading(false) }
                        .flattenAsFlowable { it }
                        .filter {
                            //Only accept labels with 90% confidence
                            it.confidence > CONFIDENCE_THRESHOLD
                        }
                        .flatMapSingle {
                            translateApi.getTranslation(
                                    it.label,
                                    "en",
                                    "es",
                                    BuildConfig.GOOGLE_TRANSLATE_API_KEY)
                        }
                        //todo see what contents
                        .map { it.data.translations?.first()?.translatedText ?: "" }
                        .filter {
                            !TextUtils.isEmpty(it)
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    photoTranslations.add(it!!)
                                    view.addLabel(it)
                                }, Timber::e
                                //ignore errors for individual translations
                        )
        )
    }

    override fun onTranslationClicked(position: Int) {
        view.showWikiForText(photoTranslations[position])
    }


    companion object {
        const val CONFIDENCE_THRESHOLD = .82
    }
}