package com.hmomeni.filepicker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_file_picker.*
import java.io.File
import java.io.FileFilter
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class FilePickerActivity : AppCompatActivity(), FPItemClickCallback, AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (spinnerInited) {
            when (position) {
                0 -> applyMediaType(MediaType.PHOTO)
                1 -> applyMediaType(MediaType.VIDEO)
                2 -> applyMediaType(MediaType.AUDIO)
                3 -> applyMediaType(MediaType.FILE)
            }
            initPicker()
        } else {
            spinnerInited = true
        }
    }

    enum class MediaType {
        PHOTO, VIDEO, AUDIO, FILE
    }

    val MY_WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 6823
    val fpItems = mutableListOf<FPItem>()
    val levels = Stack<String>()
    lateinit var fileRegex: Regex
    var currentPath = ""
    var spinnerInited = false
    var maxItems = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_picker)

        initViews()

        val allowMultiType = intent.getBooleanExtra("multi_type", false)

        if (!allowMultiType) {
            typeSpinner.isEnabled = false
        }

        val mediaType = intent.getSerializableExtra("media_type") as MediaType
        maxItems = intent.getIntExtra("max_items", 1)
        applyMediaType(mediaType)

        when (mediaType) {
            MediaType.PHOTO -> typeSpinner.setSelection(0)
            MediaType.VIDEO -> typeSpinner.setSelection(1)
            MediaType.AUDIO -> typeSpinner.setSelection(2)
            MediaType.FILE -> typeSpinner.setSelection(3)
        }

        checkPermission()
    }

    fun initViews() {
        typeSpinner.adapter = ArrayAdapter.createFromResource(this, R.array.media_types, android.R.layout.simple_spinner_item).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        typeSpinner.onItemSelectedListener = this

        selectBtn.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().putStringArrayListExtra("files", ArrayList(fpItems.filter { it.selected }.map { it.path })))
            finish()
        }
    }

    fun applyMediaType(mediaType: MediaType) {
        when (mediaType) {
        //photos
            MediaType.PHOTO -> fileRegex = Pattern.compile("^(png|jpe?g|gif)$", Pattern.CASE_INSENSITIVE).toRegex()
        //videos
            MediaType.VIDEO -> fileRegex = Pattern.compile("^(mp4|mpe?g|3gp)$", Pattern.CASE_INSENSITIVE).toRegex()
        //audios
            MediaType.AUDIO -> fileRegex = Pattern.compile("^(mp3)$", Pattern.CASE_INSENSITIVE).toRegex()
        //all
            MediaType.FILE -> fileRegex = Pattern.compile("^(.*)$", Pattern.CASE_INSENSITIVE).toRegex()
        }

    }

    private fun initPicker() {
        levels.empty()
        getDirContent(Environment.getExternalStorageDirectory().absolutePath)
    }

    private fun getDirContent(path: String) {
        currentPath = path

        fpItems.clear()
        val dir = File(path)
        for (node in dir.listFiles(FileFilter { (it.isDirectory && !it.name.startsWith('.')) or it.extension.matches(fileRegex) })) {
            val childCount = if (node.isDirectory) node.listFiles(FileFilter { (it.isDirectory && !it.name.startsWith('.')) or it.extension.matches(fileRegex) }).size else 0
            if (!node.isDirectory || childCount != 0) {
                fpItems.add(FPItem(
                        node.absolutePath,
                        if (node.isDirectory) 1 else 0,
                        if (node.name.length < 15) node.name else node.name.take(10) + "(...)." + node.extension,
                        childCount)
                )
            }

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
        } else {
            if (isAllowToPickMore() or fpItems[position].selected) {
                fpItems[position].selected = !fpItems[position].selected
                recyclerView.adapter.notifyItemChanged(position)
                checkSelectedItems()
            } else {
                Toast.makeText(this, "You can't pick more than $maxItems items", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun isAllowToPickMore() = fpItems.filter { it.selected }.size < maxItems

    private fun checkSelectedItems() {
        if (fpItems.filter { it.selected }.isEmpty()) {
            selectBtn.visibility = View.GONE
        } else {
            selectBtn.visibility = View.VISIBLE
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

    companion object {
        const val FILE_PICKER_REQUEST_CODE = 4543

        fun start(activity: Activity, maxItems: Int = 1, mediaType: MediaType = MediaType.PHOTO, allowMultiType: Boolean = false) {
            val intent = Intent(activity, FilePickerActivity::class.java)
                    .putExtra("max_items", maxItems)
                    .putExtra("media_type", mediaType)
                    .putExtra("multi_type", allowMultiType)
            activity.startActivityForResult(intent, FILE_PICKER_REQUEST_CODE)
        }

        fun parse(data: Intent): List<String> {
            return data.getStringArrayListExtra("files")
        }
    }
}
