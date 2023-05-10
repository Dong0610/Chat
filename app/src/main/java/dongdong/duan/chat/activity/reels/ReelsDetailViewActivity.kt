package dongdong.duan.chat.activity.reels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dongdong.duan.chat.databinding.ActivityReelsDetailViewBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.mode.Reels

class ReelsDetailViewActivity : AppCompatActivity() {
    lateinit var binding:ActivityReelsDetailViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityReelsDetailViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.icbackpress.setOnClickListener {
            onBackPressed()
        }
        val reels= intent.getSerializableExtra("reels") as Reels
        SetData(reels)
    }
    fun SetData(reels: Reels)
    {
        BitmapUtils.ShowImage(applicationContext,reels.REELS_URL_IMG,binding.imgreels)
        binding.imgUser.setImageBitmap(BitmapUtils.StringToBitmap(reels.REELS_US_IMG))
        binding.txttextname.text=reels.REELS_US_NAME
    }

}