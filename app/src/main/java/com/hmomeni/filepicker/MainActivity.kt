package com.hmomeni.filepicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        browseBtn.setOnClickListener {
            FilePickerActivity.start(this, maxItems = 5, mediaType = FilePickerActivity.MediaType.VIDEO, allowMultiType = true, withCamera = true)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            requestCode == FilePickerActivity.FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK -> {
                val items = FilePickerActivity.parse(data!!)
                Toast.makeText(this, "${items.size} items was selected!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
