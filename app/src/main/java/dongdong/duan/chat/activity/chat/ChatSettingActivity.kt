package dongdong.duan.chat.activity.chat

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dongdong.duan.chat.R
import dongdong.duan.chat.activity.qrcode.UserQrcodeActivity
import dongdong.duan.chat.activity.user.UserInfoActivity
import dongdong.duan.chat.databinding.ActivityChatSettingBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.file.SupportLibary
import dongdong.duan.chat.mode.Users
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.CustomDialog
import dongdong.duan.chat.utility.PreferenceManager
import dongdong.duan.chat.utility.UsingPublic
import java.lang.Exception

class ChatSettingActivity : AppCompatActivity() {
    lateinit var binding:ActivityChatSettingBinding
    var database=FirebaseFirestore.getInstance()
    val reldatabase=FirebaseDatabase.getInstance()
    lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager= PreferenceManager(applicationContext)
        val users=intent.getSerializableExtra("userreceive") as Users
        val idconversion= intent.getStringExtra("idcv") as String
        LoadInfo(users)
        LoadEvent(users,idconversion)
    }

    private fun LoadEvent(users: Users, idconversion: String) {
        binding.trangcanhan.setOnClickListener {
            SupportLibary.GetUserByID(users.USER_ID){
                    users ->
                if (users==null){
                    UsingPublic().ShowToast(applicationContext,"Hệ thống đang lỗi thử lại sau")
                }
                else{
                    val intent = Intent(applicationContext, UserInfoActivity::class.java)
                    intent.putExtra("user", users)
                    startActivity(intent)
                }
            }
        }
        binding.xoacuoctrchuyen.setOnClickListener {
            CustomDialog(this@ChatSettingActivity).ConfirmDialog("Bạn có chắc chắn xóa trò chuyện"){
                database.collection(Constant.CONVERSION_SYS).document(idconversion)
                    .delete()
                    .addOnCompleteListener{
                        SupportLibary.ShowToast(applicationContext,"Đã xóa cuộc trò chuyện")
                    }
                    .addOnFailureListener(object :OnFailureListener{
                        override fun onFailure(p0: Exception) {
                            SupportLibary.ShowToast(applicationContext,p0.message)
                        }
                    })
            }
        }
        binding.chiasethongtin.setOnClickListener {
            startActivity(Intent(applicationContext,UserQrcodeActivity::class.java))
        }

        binding.bietdanh.setOnClickListener {
            ChangName(users, idconversion)
        }
        binding.icbackpress.setOnClickListener {
            onBackPressed()
        }
    }
    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "MissingInflatedId")
    fun ChangName(users: Users,idconversion: String){
        val dialogbinding=layoutInflater.inflate(R.layout.actvity_change_name,null)
        val mydialog= Dialog(this)
        mydialog.setContentView(dialogbinding)
        mydialog.setCancelable(false)
        var ischoose=0
        dialogbinding.findViewById<TextView>(R.id.friendname).text="Cho ${users.USER_NAME}"
        dialogbinding.findViewById<EditText>(R.id.txtoldname).setText(preferenceManager.GetString(Constant.USER_NAME).toString())
        dialogbinding.findViewById<TextView>(R.id.friendname).setOnClickListener {
            dialogbinding.findViewById<EditText>(R.id.txtoldname).setText(users.USER_NAME)
            ischoose=1
        }
        dialogbinding.findViewById<TextView>(R.id.youname).setOnClickListener {
            ischoose=2
            dialogbinding.findViewById<EditText>(R.id.txtoldname).setText(preferenceManager.GetString(Constant.USER_NAME).toString())
        }
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        mydialog.window?.setLayout(width, height)

        mydialog.show()

        dialogbinding.findViewById<Button>(R.id.btncancelSEW).setOnClickListener {
            mydialog.dismiss()
        }
        dialogbinding.findViewById<Button>(R.id.btnconfirmSEW).setOnClickListener {
            val txtnewname= dialogbinding.findViewById<EditText>(R.id.txtnewname).text.toString()
            if(ischoose==1){
                database.collection(Constant.CONVERSION_SYS)
                    .document(idconversion)
                    .update(Constant.CHATCV_RECEIVE_NAME,txtnewname)
                    .addOnCompleteListener {
                        SupportLibary.ShowToast(applicationContext,"Đổi tên thành công")
                    }
            }
            else{
                database.collection(Constant.CONVERSION_SYS)
                    .document(idconversion)
                    .update(Constant.CHATCV_SENDER_NAME,txtnewname)
                    .addOnCompleteListener {
                        SupportLibary.ShowToast(applicationContext,"Đổi tên thành công")
                    }
            }
            mydialog.dismiss()
        }
    }
    private fun LoadInfo(users: Users) {
        binding.imguserchat.setImageBitmap(BitmapUtils.StringToBitmap(users.USER_IMAGE))
        binding.txtchatemail.text=users.USER_EMAIL
        binding.txtchatname.text=users.USER_NAME
    }
}