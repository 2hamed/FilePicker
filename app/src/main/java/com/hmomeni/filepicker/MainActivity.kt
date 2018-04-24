package com.hmomeni.filepicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        browseBtn.setOnClickListener {
            startActivityForResult(Intent(this, FilePickerActivity::class.java).putExtra("media_type", 1), 1234)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1234 -> {
                if (resultCode == Activity.RESULT_OK)
                    GlideApp.with(this).load(data!!.getStringExtra("file_path")).into(imageView)
            }
        }
    }
}
