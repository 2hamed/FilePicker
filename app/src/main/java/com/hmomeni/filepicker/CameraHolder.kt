package com.hmomeni.filepicker

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.util.Log
import android.view.View
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.back
import io.fotoapparat.selector.off
import kotlinx.android.synthetic.main.rcl_item_camera.view.*

class CameraHolder(context: Context, itemView: View, fpItemClickCallback: FPItemClickCallback) : BaseHolder(context, itemView, fpItemClickCallback), LifecycleObserver {
    private val fotoapparat: Fotoapparat

    init {
        (context as LifecycleOwner).lifecycle.addObserver(this)
        fotoapparat = Fotoapparat(
                context = context,
                view = itemView.cameraView,                   // view which will draw the camera preview
                scaleType = ScaleType.CenterCrop,    // (optional) we want the preview to fill the view
                lensPosition = back(),               // (optional) we want back camera
                cameraConfiguration = CameraConfiguration(flashMode = off()),
                cameraErrorCallback = { error -> Log.e("CameraHolder", "Camera Failed", error) }   // (optional) log fatal errors
        )
    }

    override fun bindView(fpItem: FPItem) {

    }

    fun bindCamera() {
        fotoapparat.start()
    }

    override fun clearHolder() {
        fotoapparat.stop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
    }
}