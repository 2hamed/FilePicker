package com.hmomeni.filepicker

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.rcl_item_file.view.*
import java.io.File

class FPFileHolder(private val context: Context, itemView: View, fpItemClickCallback: FPItemClickCallback) : BaseHolder(context, itemView, fpItemClickCallback) {

    override fun bindView(fpItem: FPItem) {
        itemView.titleV.text = fpItem.title
        GlideApp.with(context).load(File(fpItem.path)).into(itemView.previewV)
        if (fpItem.selected) {
            itemView.checkImg.visibility = View.VISIBLE
        } else {
            itemView.checkImg.visibility = View.GONE
        }
    }
}