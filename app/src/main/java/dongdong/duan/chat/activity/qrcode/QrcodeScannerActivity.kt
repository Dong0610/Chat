package dongdong.duan.chat.activity.qrcode

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.SurfaceHolder
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import dongdong.duan.chat.R
import dongdong.duan.chat.activity.user.UserInfoActivity
import dongdong.duan.chat.databinding.ActivityQrcodeScannerBinding
import dongdong.duan.chat.file.SupportLibary
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.UsingPublic
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class QrcodeScannerActivity : AppCompatActivity() {
    lateinit var binding: ActivityQrcodeScannerBinding
    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private var scannedValue = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrcodeScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(
                this@QrcodeScannerActivity, android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission()
        } else {
            setupControls()
        }
        val aniSlide: Animation =
            AnimationUtils.loadAnimation(this@QrcodeScannerActivity, R.anim.scanner_animation)
        binding.barcodeLine.startAnimation(aniSlide)

        binding.icbackpress.setOnClickListener {
            finish()
        }
    }
    private fun setupControls() {
        var barcodeDetector =
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()
        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()
        binding.previewView.getHolder().addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            @SuppressLint("MissingPermission")
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                try {
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(applicationContext, "Scanner has been closed", Toast.LENGTH_LONG)
                    .show()
            }
            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() == 1) {
                    scannedValue = barcodes.valueAt(0).rawValue
                    runOnUiThread {
                        cameraSource.stop()
                        var userid=getIdFromJson(scannedValue)
                        if(userid!=null){
                            SupportLibary.GetUserByID(userid){
                                    users ->
                                if (users==null){
                                    UsingPublic().ShowToast(applicationContext,"Hệ thống đang lỗi thử lại sau")
                                }
                                else{
                                    try {
                                        val jsonObject = JSONObject(scannedValue)
                                        UsingPublic().ShowToast(applicationContext,JSONObject().getString(jsonObject.getString(Constant.USER_NAME)))

                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }

                                    val intent = Intent(applicationContext, UserInfoActivity::class.java)
                                    intent.putExtra("user", users)
                                    Handler().postDelayed({
                                        startActivity(intent)
                                    },1500)

                                }
                            }
                        }
                        else{
                            UsingPublic().ShowToast(applicationContext,"Hệ thống đang lỗi thử lại sau")
                        }
                    }
                } else {
                    UsingPublic().ShowToast(applicationContext, "Can't not get value")
                }
            }
        })
    }
    fun getIdFromJson(jsonString: String): String? {
        try {
            val jsonObject = JSONObject(jsonString)
            return jsonObject.getString("ID")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }
    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            this@QrcodeScannerActivity,
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupControls()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        cameraSource.stop()
    }
}