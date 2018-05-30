package com.hmomeni.filepicker

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.hmomeni.filepicker.cameraview.cameraview.CameraView
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

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


        switchCameraBtn.setOnClickListener(this)
        captureBtn.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        cameraView.start()
    }

    override fun onStop() {
        super.onStop()
        cameraView.stop()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.switchCameraBtn -> if (cameraView.facing == CameraView.FACING_BACK) {
                cameraView.facing = CameraView.FACING_FRONT
                switchCameraBtn.setImageResource(R.drawable.ic_camera_back)
            } else {
                cameraView.facing = CameraView.FACING_BACK
                switchCameraBtn.setImageResource(R.drawable.ic_camera_back)
            }
            R.id.captureBtn -> {
                cameraView.takePicture()
                flash()
            }
        }
    }

    private fun flash() {
        flashView.alpha = 1f
        flashView.animate().alpha(0f).duration = 500
    }
}
