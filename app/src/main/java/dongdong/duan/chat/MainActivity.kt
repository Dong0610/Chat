package dongdong.duan.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dongdong.duan.chat.activity.fragment.HomeFragment
import dongdong.duan.chat.activity.fragment.SettingFragment
import dongdong.duan.chat.activity.fragment.UsersFragment
import dongdong.duan.chat.activity.fragment.*
import dongdong.duan.chat.activity.qrcode.QrcodeScannerActivity
import dongdong.duan.chat.activity.qrcode.UserQrcodeActivity
import dongdong.duan.chat.databinding.ActivityMainBinding
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.PreferenceManager

class MainActivity : BaseActivity() {
    private lateinit var binding:ActivityMainBinding
     override lateinit var preferenceManager:PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager= PreferenceManager(applicationContext)
        LoadColorTheme()
        LoadUser()
        binding.bottomNavigation.setOnItemSelectedListener (OnItemSelectedBottomBar)
        LoadFragment(HomeFragment())
        binding.icamera.setOnClickListener {
            startActivity(Intent(applicationContext,QrcodeScannerActivity::class.java))
        }
        binding.icqrcode.setOnClickListener {
            startActivity(Intent(applicationContext,UserQrcodeActivity::class.java))
        }
    }

    private val OnItemSelectedBottomBar =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.iconhome -> {
                    LoadFragment(HomeFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.iconuser -> {
                    LoadFragment(UsersFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.iconmess -> {
                    LoadFragment(ConversionFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.iconnotification -> {
                    LoadFragment(NotificationFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.iconsetiing -> {
                    LoadFragment(SettingFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
            true
        }

    private fun LoadFragment(fragment: Fragment):Boolean {
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.viewPager, fragment)
                .commit()
            return true
        }
        return false
    }

    private fun LoadUser() {
        binding.txtusername.text=preferenceManager.GetString(Constant.USER_NAME)
    }
    @SuppressLint("ResourceType")
    private fun LoadColorTheme() {
        var themetype = preferenceManager.GetString(Constant.KEY_THEME_APP)!!.toInt()
        if (themetype == 1) {
            binding.bottomNavigation.itemIconTintList =
                getColorStateList(R.drawable.icon_color_bar_light)
            binding.bgroundapp.setBackgroundColor(Color.WHITE)
            binding.txtusername.setTextColor(Color.BLACK)
            binding.icamera.setImageResource(R.drawable.ic_camera_baseline_light)
            binding.icqrcode.setImageResource(R.drawable.ic_img_qrcode_light)
        }
    }
}















