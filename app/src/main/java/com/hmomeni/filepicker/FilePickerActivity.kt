package com.hmomeni.filepicker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_file_picker.*
import java.io.File
import java.io.FileFilter
import java.util.*
import java.util.regex.Pattern

class FilePickerActivity : AppCompatActivity(), FPItemClickCallback {


    val MY_WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 6823
    val fpItems = mutableListOf<FPItem>()
    val levels = Stack<String>()
    val fileRegex = Pattern.compile("^(png|jpe?g|gif)$", Pattern.CASE_INSENSITIVE).toRegex()
    var currentPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_picker)
        checkPermission()


    }

    private fun initPicker() {
        getDirContent(Environment.getExternalStorageDirectory().absolutePath)
    }

    private fun getDirContent(path: String) {
        currentPath = path

        fpItems.clear()
        val dir = File(path)
        for (node in dir.listFiles(FileFilter { it.isDirectory or it.extension.matches(fileRegex) })) {
            fpItems.add(FPItem(
                    node.absolutePath,
                    if (node.isDirectory) 1 else 0,
                    node.name,
                    if (node.isDirectory) node.listFiles(FileFilter { it.isDirectory or it.extension.matches(fileRegex) }).size else 0)
            )
        }

        populateRecyclerView()

    }

    private fun populateRecyclerView() {
        if (recyclerView.adapter == null) {
            recyclerView.adapter = FPRecyclerAdapter(this, fpItems, this@FilePickerActivity)
            recyclerView.layoutManager = GridLayoutManager(this, 3)
        } else {
            recyclerView.adapter.notifyDataSetChanged()
        }
    }

    override fun onItemClicked(position: Int) {
        if (fpItems[position].type == 1) {
            levels.push(currentPath)
            getDirContent(fpItems[position].path)
        }
    }

    override fun onBackPressed() {
        if (levels.size > 0) {
            getDirContent(levels.pop())
        } else {
            super.onBackPressed()
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST)

            }

        } else {
            initPicker()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    initPicker()
                } else {
                    finish()
                }
            }
        }
    }
}
