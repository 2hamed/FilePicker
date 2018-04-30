package com.hmomeni.filepicker

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.rcl_item_camera.view.*

class CameraHolder(context: Context, itemView: View, fpItemClickCallback: FPItemClickCallback) : BaseHolder(context, itemView, fpItemClickCallback), LifecycleObserver {

    init {
        (context as LifecycleOwner).lifecycle.addObserver(this)
    }

    override fun bindView(fpItem: FPItem) {
    }

    fun bindCamera() {
        itemView.cameraView.start()
    }

    override fun clearHolder() {
        itemView.cameraView.stop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        itemView.cameraView.stop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        itemView.cameraView.start()
    }
}