package com.hmomeni.filepicker

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.rcl_item_dir.view.*

class FPDirHolder(context: Context, itemView: View, fpItemClickCallback: FPItemClickCallback) : BaseHolder(context, itemView, fpItemClickCallback) {

    override fun bindView(fpItem: FPItem) {
        itemView.titleV.text = fpItem.title
        itemView.childCountV.text = fpItem.childCount.toString()
    }
}