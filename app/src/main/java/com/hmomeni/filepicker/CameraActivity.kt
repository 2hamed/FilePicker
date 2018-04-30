package com.hmomeni.filepicker

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hmomeni.filepicker.cameraview.cameraview.CameraView
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        captureBtn.setOnClickListener {
            cameraView.takePicture()
        }

        cameraView.addCallback(object : CameraView.Callback() {
            override fun onCameraOpened(cameraView: CameraView?) {
                super.onCameraOpened(cameraView)
            }

            override fun onCameraClosed(cameraView: CameraView?) {
                super.onCameraClosed(cameraView)
            }

            override fun onPictureTaken(cameraView: CameraView?, data: ByteArray?) {
                super.onPictureTaken(cameraView, data)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        cameraView.start()
    }

    override fun onStop() {
        super.onStop()
        cameraView.stop()
    }
}
