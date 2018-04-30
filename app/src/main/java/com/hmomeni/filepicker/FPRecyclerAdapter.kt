package com.hmomeni.filepicker

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class FPRecyclerAdapter(private val context: Context, private val items: List<FPItem>, private val fpItemClickCallback: FPItemClickCallback) : RecyclerView.Adapter<BaseHolder>() {

    override fun getItemViewType(position: Int) =
            items[position].type


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        return when (viewType) {
            0 -> FPFileHolder(context, LayoutInflater.from(context).inflate(R.layout.rcl_item_file, parent, false), fpItemClickCallback)
            1 -> FPDirHolder(context, LayoutInflater.from(context).inflate(R.layout.rcl_item_dir, parent, false), fpItemClickCallback)
            2 -> CameraHolder(context, LayoutInflater.from(context).inflate(R.layout.rcl_item_camera, parent, false), fpItemClickCallback)
            else -> throw RuntimeException("Invalid item view type")
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        if (getItemViewType(position) == 2) {
            (holder as CameraHolder).bindCamera()
        } else {
            holder.bindView(items[position])
        }
    }

    override fun onViewRecycled(holder: BaseHolder) {
        holder.clearHolder()
    }
}