package com.tiensinoakuma.rikaipic.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.transition.TransitionManager
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
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
    private lateinit var adapter: TranslationAdapter
    private var isExpanded: Boolean = false

    companion object {
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
        //Setup Recyclerview
        translationList.setHasFixedSize(true)
        translationList.layoutManager = LinearLayoutManager(this)
        adapter = TranslationAdapter(presenter)
        translationList.adapter = adapter
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
        //todo use lifecycle methods
        presenter.onCreate()
    }

    override fun showErrorRetry(error: Throwable, retry: Runnable) {
        Timber.e(error)
        Snackbar.make(constraintLayout, R.string.something_went_wrong, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, { _ -> retry.run() })
                .show()
    }

    override fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showPhoto(pexelsPhoto: PexelsPhoto) {
        Glide.with(this)
                .asBitmap()
                .load(pexelsPhoto.src.tiny)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        presenter.onImageLoaded(resource)
                    }
                })
        Glide.with(this).load(pexelsPhoto.src.original).into(rikaiPic)
        photographer.text = pexelsPhoto.photographer
        photographer.setOnClickListener {
            startActivityFromUrl(pexelsPhoto.url)
        }
    }

    override fun addLabel(text: String) {
        adapter.addLabel(text)
    }

    override fun showWikiForText(text: String) {
        startActivityFromUrl(String.format("%s%s", BuildConfig.WIKI_URL, text))
    }

    private fun startActivityFromUrl(url: String) = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}