package com.hmomeni.filepicker

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View

abstract class BaseHolder(context: Context, itemView: View, fpItemClickCallback: FPItemClickCallback) : RecyclerView.ViewHolder(itemView) {
    init {
        itemView.setOnClickListener { fpItemClickCallback.onItemClicked(adapterPosition) }
    }

    abstract fun bindView(fpItem: FPItem)

    open fun clearHolder() {
    }
}