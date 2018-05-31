package com.hmomeni.filepicker

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.*
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var fotoapparat: Fotoapparat
    private lateinit var cameraConfiguration: CameraConfiguration
    private var flashMode = FlashMode.AUTO
    private var cameraFacing = CameraFacing.BACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        cameraConfiguration = CameraConfiguration(
                flashMode = autoFlash()
        )
        fotoapparat = Fotoapparat(
                context = this,
                view = cameraView,                   // view which will draw the camera preview
                scaleType = ScaleType.CenterCrop,    // (optional) we want the preview to fill the view
                lensPosition = back(),               // (optional) we want back camera
                cameraConfiguration = cameraConfiguration,
                cameraErrorCallback = { error -> Log.e("CameraHolder", "Camera Failed", error) }   // (optional) log fatal errors

        )

        switchCameraBtn.setOnClickListener(this)
        captureBtn.setOnClickListener(this)
        flashBtn.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        fotoapparat.start()
    }

    override fun onStop() {
        super.onStop()
        fotoapparat.stop()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.switchCameraBtn -> when (cameraFacing) {
                CameraFacing.BACK -> {
                    cameraFacing = CameraFacing.FRONT
                    fotoapparat.switchTo(front(), cameraConfiguration)
                    switchCameraBtn.setImageResource(R.drawable.ic_camera_back)
                }
                CameraFacing.FRONT -> {
                    cameraFacing = CameraFacing.BACK
                    fotoapparat.switchTo(back(), cameraConfiguration)
                    switchCameraBtn.setImageResource(R.drawable.ic_camera_back)
                }
            }
            R.id.captureBtn -> {
                val photoResult = fotoapparat.takePicture()
                photoResult
                        .toBitmap()
                        .whenAvailable {
                            cropImageView.setImageBitmap(getResizedBitmap(it!!.bitmap, 2048, 2048))
                            cropImageView.rotatedDegrees = it.rotationDegrees - 180

                            cropImageView.visibility = View.VISIBLE
                            cameraView.visibility = View.GONE
                            fotoapparat.stop()

                            captureBtn.animate().translationYBy(300f).duration = 500
                            switchCameraBtn.animate().translationYBy(300f).duration = 500
                            flashBtn.animate().translationYBy(300f).duration = 500
                        }
                flash()
            }
            R.id.flashBtn -> when (flashMode) {
                FlashMode.OFF -> {
                    flashMode = FlashMode.ON
                    cameraConfiguration = cameraConfiguration.copy(
                            flashMode = on()
                    )
                    fotoapparat.updateConfiguration(cameraConfiguration)
                    flashBtn.setImageResource(R.drawable.ic_flash)
                }
                FlashMode.ON -> {
                    flashMode = FlashMode.AUTO
                    cameraConfiguration = cameraConfiguration.copy(
                            flashMode = autoFlash()
                    )
                    fotoapparat.updateConfiguration(cameraConfiguration)
                    flashBtn.setImageResource(R.drawable.ic_flash_auto)
                }
                FlashMode.AUTO -> {
                    flashMode = FlashMode.OFF
                    cameraConfiguration = cameraConfiguration.copy(
                            flashMode = off()
                    )
                    fotoapparat.updateConfiguration(cameraConfiguration)
                    flashBtn.setImageResource(R.drawable.ic_flash_off)
                }
            }
        }
    }

    private fun flash() {
        flashView.alpha = 1f
        flashView.animate().alpha(0f).duration = 500
    }

    private enum class FlashMode {
        OFF, ON, AUTO
    }

    private enum class CameraFacing {
        FRONT, BACK
    }

    fun getResizedBitmap(bm: Bitmap, newHeight: Int, newWidth: Int): Bitmap {

        val width = bm.width

        val height = bm.height

        val scaleWidth = newWidth.toFloat() / width

        val scaleHeight = newHeight.toFloat() / height

        // create a matrix for the manipulation

        val matrix = Matrix()

        // resize the bit map

        matrix.postScale(scaleWidth, scaleHeight)

        // recreate the new Bitmap

        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)

    }
}
