package com.tiensinoakuma.rikaipic.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.transition.TransitionManager
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.tiensinoakuma.rikaipic.BuildConfig
import com.tiensinoakuma.rikaipic.R
import com.tiensinoakuma.rikaipic.RikaiPicApp
import com.tiensinoakuma.rikaipic.api.pexels.PexelsPhoto
import com.tiensinoakuma.rikaipic.dagger.rikai_image.RikaiPicModule
import kotlinx.android.synthetic.main.activity_rikai_pic_expanded.*
import timber.log.Timber
import javax.inject.Inject


class RikaiPicActivity : AppCompatActivity(), RikaiPicContract.View {

    @Inject
    lateinit var presenter: RikaiPicContract.Presenter
    private lateinit var translationAdapter: TranslationAdapter
    private lateinit var languageAdapter: LanguageAdapter
    private var isExpanded: Boolean = false
    private var largeBitmap: Bitmap? = null

    companion object {
        const val MAX_SPAN = 3

        fun startActivity(context: Context) {
            context.startActivity(Intent(context, RikaiPicActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rikai_pic_collapsed)
        RikaiPicApp.getComponent(this)
                .rikaiPicActivity(RikaiPicModule(this))
                .inject(this)

        //Setup translations list
        translationList.setHasFixedSize(true)
        translationList.layoutManager = LinearLayoutManager(this)
        translationAdapter = TranslationAdapter(presenter)
        translationList.adapter = translationAdapter

        //Setup translation layout animation
        val collapsed = ConstraintSet()
        collapsed.clone(constraintLayout)
        val expanded = ConstraintSet()
        expanded.clone(this, R.layout.activity_rikai_pic_expanded)
        constraintLayout.setOnClickListener {
            TransitionManager.beginDelayedTransition(constraintLayout)
            val constraint = if (isExpanded) collapsed else expanded
            constraint.applyTo(constraintLayout)
            isExpanded = !isExpanded
        }

        fab.setOnClickListener {
            presenter.onNextSelected()
        }

        //todo use lifecycle methods
        presenter.onCreate()
    }

    override fun setSupportedLanguages(supportedLanguages: List<String>, defaultLanguage: String) {
        languageList.setHasFixedSize(true)
        languageAdapter = LanguageAdapter(presenter, defaultLanguage, supportedLanguages)
        languageList.layoutManager = GridLayoutManager(this, MAX_SPAN)
        languageList.adapter = languageAdapter
    }

    override fun enableLanguageClick(isClickable: Boolean) {
        languageAdapter.isClickable = false
    }

    override fun showImagesReady() {
        fab.isEnabled = false
    }

    override fun showLoadingScreen() {
        //todo
    }

    override fun enableFab(enable: Boolean) {
        fab.isEnabled = enable
    }

    override fun showErrorRetry(error: Throwable, runnable: Runnable) {
        Timber.e(error)
        Snackbar.make(constraintLayout, R.string.something_went_wrong, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, { _ -> runnable.run() })
                .show()
    }

    override fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showPhoto(pexelsPhoto: PexelsPhoto) {
        Glide.with(this)
                .asBitmap()
                .load(pexelsPhoto.src.large)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        largeBitmap = resource
                        presenter.onLargeImageLoaded(resource)
                    }
                })
        Glide.with(this)
                .load(pexelsPhoto.src.original)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        presenter.onOriginalImageLoaded()
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }
                }).into(rikaiPic)
        photographer.text = pexelsPhoto.photographer
        photographer.setOnClickListener {
            startActivityFromUrl(pexelsPhoto.url)
        }
    }

    override fun showLabels(labels: List<String>) {
        translationAdapter.setLabels(labels)
    }

    override fun enableSupportedLanguages(isClickable: Boolean) {
        languageAdapter.isClickable = true
    }

    override fun clearData() {
        translationAdapter.clear()
        if (largeBitmap != null) {
            largeBitmap?.recycle()
        }
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun showWikiForText(text: String) {
        startActivityFromUrl(String.format("%s%s", BuildConfig.WIKI_URL, text))
    }

    private fun startActivityFromUrl(url: String) = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

}