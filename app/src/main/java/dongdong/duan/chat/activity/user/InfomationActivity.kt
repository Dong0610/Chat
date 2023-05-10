package dongdong.duan.chat.activity.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dongdong.duan.chat.activity.qrcode.UserQrcodeActivity
import dongdong.duan.chat.databinding.ActivityInfomationBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.PreferenceManager

class InfomationActivity : AppCompatActivity() {
    lateinit var preferenceManager: PreferenceManager
    lateinit var binding:ActivityInfomationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityInfomationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager= PreferenceManager(applicationContext)

        binding.icbackpress.setOnClickListener {
            onBackPressed()
        }

        binding.txtchatname.text=preferenceManager.GetString(Constant.USER_NAME).toString()
        binding.txtuserID.text=preferenceManager.GetString(Constant.USER_ID).toString()
        binding.txtemail.text=preferenceManager.GetString(Constant.USER_EMAIL).toString()
        binding.txttime.text=preferenceManager.GetString(Constant.USER_TIME).toString()
        binding.urlimg.text=preferenceManager.GetString(Constant.USER_URLIMG).toString()
        var s=""
        for (i in preferenceManager.GetString(Constant.USER_PASS).toString()){
            s+="*"
        }
        binding.passuse.text=s
        binding.imguserchat.setImageBitmap(BitmapUtils.StringToBitmap(preferenceManager.GetString(Constant.USER_IMAGE).toString()))
        var isclick=true
        binding.layoutpass.setOnClickListener {
            if(isclick){
                binding.passuse.text=preferenceManager.GetString(Constant.USER_PASS).toString()
                isclick=false
            }
            else{
                var s2=""
                for (i in preferenceManager.GetString(Constant.USER_PASS).toString()){
                    s2+="*"
                }
                binding.passuse.text=s2
                isclick=true
            }
        }
        binding.qrcodecanhan.setOnClickListener {
            startActivity(Intent(applicationContext, UserQrcodeActivity::class.java))
        }

    }
}