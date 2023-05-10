package dongdong.duan.chat.activity.qrcode

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.ImageView
import dongdong.duan.chat.databinding.ActivityUserQrcodeBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.PreferenceManager
import dongdong.duan.chat.utility.UsingPublic

@Suppress("DEPRECATION")
class UserQrcodeActivity : AppCompatActivity() {
    lateinit var binding:ActivityUserQrcodeBinding
    lateinit var preferenceManager:PreferenceManager
    data class MyObject(val ID:String,val name:String,val email:String)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUserQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager=PreferenceManager(applicationContext)
        val imageView = binding.imgQrcode as ImageView
        imageView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                var myqrinf=MyObject(preferenceManager.GetString(Constant.USER_ID).toString(),
                preferenceManager.GetString(Constant.USER_NAME).toString(),preferenceManager.GetString(Constant.USER_EMAIL).toString())
                imageView.setImageBitmap(BitmapUtils.MyQrCodeUser(myqrinf,imageView.width))
                imageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        binding.txttypeqr.text=preferenceManager.GetString(Constant.USER_NAME)
        binding.imgtypeicon.setImageBitmap(BitmapUtils.StringToBitmap(preferenceManager.GetString(Constant.USER_IMAGE)))
        binding.icbackpress.setOnClickListener {
            onBackPressed()
        }

    }
}