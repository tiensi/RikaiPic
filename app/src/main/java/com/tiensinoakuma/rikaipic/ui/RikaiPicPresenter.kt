package com.tiensinoakuma.rikaipic.ui

import android.graphics.Bitmap
import android.text.TextUtils
import com.tiensinoakuma.rikaipic.BuildConfig
import com.tiensinoakuma.rikaipic.api.firebase.FirebaseVisionApi
import com.tiensinoakuma.rikaipic.api.g_translate.GTranslateSource
import com.tiensinoakuma.rikaipic.api.pexels.PexelsApi
import com.tiensinoakuma.rikaipic.api.pexels.PexelsPhoto
import com.tiensinoakuma.rikaipic.api.pexels.PexelsResponse
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.disposables.SerialDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import kotlin.collections.HashMap

class RikaiPicPresenter(
        private val view: RikaiPicContract.View,
        private val translateSource: GTranslateSource,
        private val pexelsApi: PexelsApi,
        private val firebaseVisionApi: FirebaseVisionApi,
        private val supportedLanguages: List<String>,
        private var selectedLanguage: String
) : RikaiPicContract.Presenter {

    private val imageLoadDisposable = SerialDisposable()
    private val translationsDisposable = SerialDisposable()
    private val pexelsPhotoList: MutableList<PexelsPhoto> = ArrayList()
    private val translationMap: MutableMap<String, List<String>> = HashMap()
    private var imageIndex = 0
    private var pexelPage = 0

    override
    fun onCreate() {
        view.setSupportedLanguages(supportedLanguages, selectedLanguage)
        imageLoadDisposable.set(
                getCuratedPics()
                        .subscribe(
                                { pexelsResponse ->
                                    pexelsPhotoList.clear()
                                    pexelsPhotoList.addAll(pexelsResponse.photos)
                                    view.showPhoto(pexelsPhotoList[imageIndex])
                                },
                                { error ->
                                    Timber.e(error)
                                    view.showErrorRetry(error, Runnable { onCreate() })
                                }
                        )
        )
    }

    //Retrieve images from pexels api
    private fun getCuratedPics(): Single<PexelsResponse> {
        view.showLoading(true)
        return pexelsApi.getCuratedPics(pexelPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    pexelPage++
                }
                .doAfterTerminate {
                    view.showLoading(false)
                }
    }

    override fun onNextSelected() {
        view.clearData()
        view.enableFab(false)
        view.enableLanguageClick(false)
        imageIndex++
        if (imageIndex == pexelsPhotoList.size) {
            view.showLoadingScreen()
            //show loading screen while retrieving more images
            imageLoadDisposable.set(
                    getCuratedPics()
                            .subscribe(
                                    { pexelsResponse ->
                                        pexelsPhotoList.clear()
                                        pexelsPhotoList.addAll(pexelsResponse.photos)
                                        imageIndex = 0
                                        view.enableFab(true)
                                    },
                                    { error ->
                                        Timber.e(error)
                                        view.showErrorRetry(error, Runnable {
                                            imageIndex--
                                            onNextSelected()
                                        })
                                    }
                            )
            )
        } else {
            view.showLoading(true)
            translationsDisposable.set(Disposables.empty())
            //show next image
            view.showPhoto(pexelsPhotoList[imageIndex])
        }
    }

    override fun onOriginalImageLoaded() {
        view.enableFab(true)
        view.enableLanguageClick(true)
        view.showLoading(false)
    }

    override fun onDestroy() {
        imageLoadDisposable.dispose()
        translationsDisposable.dispose()
    }


    override fun onLargeImageLoaded(resource: Bitmap) {
        val sourceTranslations = ArrayList<String>()
        translationMap.clear()
        translationsDisposable.set(
                firebaseVisionApi.getAllVisionLabels(resource)
                        .doAfterTerminate { view.showLoading(false) }
                        .flattenAsFlowable { it }
                        .filter {
                            //Only accept labels with 90% confidence
                            it.confidence > CONFIDENCE_THRESHOLD
                        }
                        .doOnNext { sourceTranslations.add(it.label) }
                        .flatMapSingle {
                            translateSource.getTranslation(
                                    it.label,
                                    selectedLanguage)
                        }
                        .filter {
                            !TextUtils.isEmpty(it)
                        }
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    if (it.isEmpty()) {
                                        it.add("¯\\_(ツ)_/¯")
                                    }
                                    translationMap[BuildConfig.DEFAULT_LANGUAGE] = sourceTranslations
                                    translationMap[selectedLanguage] = it
                                    view.showLabels(it)
                                    view.enableSupportedLanguages(true)
                                }, { Timber.e(it) }
                                //ignore errors for individual translations
                        )
        )
    }

    override fun onTranslationClicked(position: Int) {
        view.showWikiForText(translationMap[selectedLanguage]!![position])
    }

    override fun onLanguageClicked(language: String) {
        selectedLanguage = language
        if (translationMap[selectedLanguage] != null) {
            view.showLabels(translationMap[selectedLanguage]!!)
        } else {
            translationsDisposable.set(Observable.fromIterable(translationMap[BuildConfig.DEFAULT_LANGUAGE])
                    .flatMapSingle { translateSource.getTranslation(it, selectedLanguage) }
                    .toList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                if (it.isEmpty()) {
                                    it.add("¯\\_(ツ)_/¯")
                                }
                                translationMap[selectedLanguage] = it
                                view.showLabels(it)
                            }, { Timber.e(it) }
                            //ignore errors for individual translations
                    )
            )
        }
    }

    companion object {
        const val CONFIDENCE_THRESHOLD = .9
    }
}