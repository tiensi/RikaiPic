package com.tiensinoakuma.rikaipic.api.firebase

import android.graphics.Bitmap
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionLabel
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class FirebaseVisionApi(
        private val visionLabelDetector: FirebaseVisionLabelDetector,
        private val visionCloudLabelDetector: FirebaseVisionCloudLabelDetector
) {

    private fun getVisionLabels(image: Bitmap): Single<List<FirebaseVisionLabel>> =
            Single.create<List<FirebaseVisionLabel>> {
                val emitter = it
                visionLabelDetector.detectInImage(FirebaseVisionImage.fromBitmap(image))
                        .addOnSuccessListener {
                            emitter.onSuccess(it)
                        }
                        .addOnFailureListener {
                            emitter.onError(it)
                        }
            }

    private fun getCloudVisionLabels(image: Bitmap): Single<List<FirebaseVisionCloudLabel>> =
            Single.create<List<FirebaseVisionCloudLabel>> {
                val emitter = it
                visionCloudLabelDetector.detectInImage(FirebaseVisionImage.fromBitmap(image))
                        .addOnSuccessListener {
                            emitter.onSuccess(it)
                        }
                        .addOnFailureListener {
                            emitter.onError(it)
                        }
            }

    fun getAllVisionLabels(image: Bitmap): Single<List<FirebaseLabel>> =
            Single.zip(
                    getVisionLabels(image),
                    getCloudVisionLabels(image),
                    BiFunction<List<FirebaseVisionLabel>, List<FirebaseVisionCloudLabel>, List<FirebaseLabel>> { visionLabels, visionCloudLabels ->
                        val labels: MutableList<FirebaseLabel> = ArrayList()
                        visionLabels.forEach {
                            labels.add(FirebaseLabel(it.confidence, it.label))
                        }
                        visionCloudLabels.forEach {
                            labels.add(FirebaseLabel(it.confidence, it.label))
                        }
                        labels
                    }
            ).subscribeOn(Schedulers.io())
}