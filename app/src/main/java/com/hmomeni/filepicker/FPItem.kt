package com.hmomeni.filepicker

data class FPItem(
        val path: String,
        val type: Int,
        val title: String,
        val childCount: Int,
        var selected: Boolean = false
)