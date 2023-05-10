package dongdong.duan.chat.activity.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dongdong.duan.chat.MainActivity
import dongdong.duan.chat.R
import dongdong.duan.chat.activity.account.SignInActivity
import dongdong.duan.chat.activity.chat.ImageShow
import dongdong.duan.chat.activity.reels.AddReelsActivity
import dongdong.duan.chat.activity.user.*
import dongdong.duan.chat.databinding.FragmentSettingBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.file.SupportLibary
import dongdong.duan.chat.mode.Users
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.PreferenceManager
import dongdong.duan.chat.utility.UsingPublic

class SettingFragment : Fragment() {
    private lateinit var binding:FragmentSettingBinding
    private lateinit var preferenceManager:PreferenceManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentSettingBinding.inflate(layoutInflater)
        preferenceManager= PreferenceManager(context)

        LoadTypeTheme()

        var user=Users()
        user.USER_ID=preferenceManager.GetString(Constant.USER_ID)!!
        user.USER_NAME=preferenceManager.GetString(Constant.USER_NAME)!!
        user.USER_EMAIL=preferenceManager.GetString(Constant.USER_EMAIL)!!
        user.USER_PASS=preferenceManager.GetString(Constant.USER_PASS)!!
        user.USER_IMAGE=preferenceManager.GetString(Constant.USER_IMAGE)!!
         user.USER_URLIMG= preferenceManager.GetString(Constant.USER_URLIMG)!!
        LoadUsers(user)
        SetListener(user)
        return binding.root
    }

    private fun LoadUsers(user: Users) {
        binding.imgUsers.setImageBitmap(BitmapUtils.StringToBitmap(user.USER_IMAGE))
        binding.txtusername.text=user.USER_NAME
        binding.txtemail.text=user.USER_EMAIL
    }

    @SuppressLint("SetTextI18n")
    private fun SetListener(user: Users) {
        binding.layoutcanhan.setOnClickListener {
            SupportLibary.GetUserByID(user.USER_ID){
                    users ->
                if (users==null){
                    UsingPublic().ShowToast(requireContext(),"Hệ thống đang lỗi thử lại sau")
                }
                else{
                    val intent = Intent(this.requireContext(), UserInfoActivity::class.java)
                    intent.putExtra("user", users)
                    startActivity(intent)
                }
            }
        }
        binding.layoutbaomat.setOnClickListener {
            startActivity(Intent(requireContext(),PrivacyPolicyActivity::class.java))
        }
        binding.layoutdongtg.setOnClickListener {
            startActivity(Intent(requireContext(),UserTimelineActivity::class.java))
        }
        binding.layoutthemtin.setOnClickListener {
            var intent = Intent(requireContext(),AddReelsActivity::class.java)
            startActivity(intent)
        }
        binding.layoutbanbe.setOnClickListener {
            startActivity(Intent(requireContext(),FriendActivity::class.java))
        }
        binding.btnsignout.setOnClickListener {
            SignOut()
        }

        binding.layoutvietbai.setOnClickListener {
            startActivity(Intent(requireContext(),AddNewPostActivity::class.java))
        }

        binding.layoutdoipass.setOnClickListener {
            startActivity(Intent(requireContext(),UpdatePassActivity::class.java))
        }
        binding.layoutttcanha.setOnClickListener {
            startActivity(Intent(requireContext(),InfomationActivity::class.java))
        }

        binding.switchicongd.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                preferenceManager.PutString(Constant.KEY_THEME_APP,"0")
                binding.tctcolor.text="Giao diện ngày"
                startActivity(Intent(context, MainActivity::class.java))
            }
            else{
                preferenceManager.PutString(Constant.KEY_THEME_APP,"1")
                binding.tctcolor.text="Giao diện đêm"
                startActivity(Intent(context,MainActivity::class.java))
            }
        }

        binding.imgUsers.setOnClickListener {
            var intent=Intent(requireContext(), ImageShow::class.java)
            intent.putExtra("urlimg",user.USER_URLIMG)
            intent.putExtra("name",user.USER_NAME)
            startActivity(intent)
        }
    }
    private fun SignOut() {
        preferenceManager.PutBoolean(Constant.KEY_IS_SIGN,false)
        preferenceManager.clear()
        startActivity(Intent(context, SignInActivity::class.java))
    }



    @SuppressLint("UseCompatLoadingForDrawables")
    private fun LoadTypeTheme() {

        var themetype=preferenceManager.GetString(Constant.KEY_THEME_APP)!!.toInt()
        if(themetype== 1){
            binding.switchicongd.isChecked=false
            binding.tctcolor.text="Giao diện ngày"
            binding.layoutcanhan.background=requireContext().getDrawable(R.drawable.bg_item_setting_light)
            binding.layoutuser.background=requireContext().getDrawable(R.drawable.bg_item_setting_light)
            binding.layoutbanbe.background=requireContext().getDrawable(R.drawable.bg_item_setting_light)
            binding.layoutbaomat.background=requireContext().getDrawable(R.drawable.bg_item_setting_light)
            binding.layoutdoipass.background=requireContext().getDrawable(R.drawable.bg_item_setting_light)
            binding.layoutgiaodien.background=requireContext().getDrawable(R.drawable.bg_item_setting_light)
            binding.layoutthemtin.background=requireContext().getDrawable(R.drawable.bg_item_setting_light)
            binding.layoutttcanha.background=requireContext().getDrawable(R.drawable.bg_item_setting_light)
            binding.layoutvietbai.background=requireContext().getDrawable(R.drawable.bg_item_setting_light)
            binding.layoutdongtg.background=requireContext().getDrawable(R.drawable.bg_item_setting_light)
            binding.btnsignout.background=requireContext().getDrawable(R.drawable.itemview_user_btn_light)

            binding.btnsignout.setTextColor(Color.BLACK)
            binding.txtusername.setTextColor(Color.BLACK)
            binding.txtcanhan.setTextColor(Color.BLACK)
            binding.txtbaomat.setTextColor(Color.BLACK)
            binding.txtbanbe.setTextColor(Color.BLACK)
            binding.txtdoipass.setTextColor(Color.BLACK)
            binding.txtdongtg.setTextColor(Color.BLACK)
            binding.txtgiaodien.setTextColor(Color.BLACK)
            binding.txtthongtin.setTextColor(Color.BLACK)
            binding.txtthemtin.setTextColor(Color.BLACK)
            binding.txtvietbai.setTextColor(Color.BLACK)
        }
        else{
            binding.switchicongd.isChecked=true
            binding.tctcolor.text="Giao diện đêm"
        }
    }
}