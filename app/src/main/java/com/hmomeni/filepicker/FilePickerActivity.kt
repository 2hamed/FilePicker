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

    @Suppress("PrivatePropertyName")
    private val MY_WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 6823
    private val fpItems = mutableListOf<FPItem>()
    private val levels = Stack<String>()
    private lateinit var fileRegex: Regex
    private var currentPath = ""
    private var spinnerInited = false
    private var maxItems = 1
    private var withCamera = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_picker)

        initViews()

        val allowMultiType = intent.getBooleanExtra("multi_type", false)

        if (!allowMultiType) {
            typeSpinner.isEnabled = false
        }

        withCamera = intent.getBooleanExtra("with_camera", false)

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

    private fun initViews() {
        typeSpinner.adapter = ArrayAdapter.createFromResource(this, R.array.media_types, android.R.layout.simple_spinner_item).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        typeSpinner.onItemSelectedListener = this

        selectBtn.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().putStringArrayListExtra("files", ArrayList(fpItems.filter { it.selected }.map { it.path })))
            finish()
        }
    }

    private fun applyMediaType(mediaType: MediaType) {
        fileRegex = when (mediaType) {
        //photos
            MediaType.PHOTO -> Pattern.compile("^(png|jpe?g|gif)$", Pattern.CASE_INSENSITIVE).toRegex()
        //videos
            MediaType.VIDEO -> Pattern.compile("^(mp4|mpe?g|3gp)$", Pattern.CASE_INSENSITIVE).toRegex()
        //audios
            MediaType.AUDIO -> Pattern.compile("^(mp3)$", Pattern.CASE_INSENSITIVE).toRegex()
        //all
            MediaType.FILE -> Pattern.compile("^(.*)$", Pattern.CASE_INSENSITIVE).toRegex()
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
        if (withCamera && levels.size == 0) {
            fpItems.add(0, FPItem("", 2, "", 0))
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


        when {
            fpItems[position].type == 1 -> {
                levels.push(currentPath)
                getDirContent(fpItems[position].path)
            }
            fpItems[position].type == 2 -> {
                startActivity(Intent(this, CameraActivity::class.java))
            }
            else -> if (isAllowToPickMore() or fpItems[position].selected) {
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
        if (fpItems.none { it.selected }) {
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
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            when {
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {

                }
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA) -> {

                }
                else -> ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), MY_WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST)
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

        fun start(activity: Activity, maxItems: Int = 1, mediaType: MediaType = MediaType.PHOTO, allowMultiType: Boolean = false, withCamera: Boolean = false) {
            val intent = Intent(activity, FilePickerActivity::class.java)
                    .putExtra("max_items", maxItems)
                    .putExtra("media_type", mediaType)
                    .putExtra("multi_type", allowMultiType)
                    .putExtra("with_camera", withCamera)
            activity.startActivityForResult(intent, FILE_PICKER_REQUEST_CODE)
        }

        fun parse(data: Intent): List<String> {
            return data.getStringArrayListExtra("files")
        }
    }
}
