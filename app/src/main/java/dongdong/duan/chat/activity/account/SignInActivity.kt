package dongdong.duan.chat.activity.account

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dongdong.duan.chat.MainActivity
import dongdong.duan.chat.databinding.ActivitySignInBinding
import dongdong.duan.chat.file.SupportLibary
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.CustomDialog
import dongdong.duan.chat.utility.PreferenceManager
import dongdong.duan.chat.utility.UsingPublic
import java.util.Date

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var preferenceManager: PreferenceManager
    private val database = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        kotlin.run {

            super.onCreate(savedInstanceState)
            binding = ActivitySignInBinding.inflate(layoutInflater)
            setContentView(binding.root)
            preferenceManager = PreferenceManager(applicationContext)
            if (this.preferenceManager.GetBoolean(Constant.KEY_IS_SIGN)) {
                startActivity(Intent(this.applicationContext, MainActivity::class.java))
                finish()
            }
            SetListener()

            binding.txtfogotpass.setOnClickListener {
                FogotPass()
            }
        }
    }

    private fun FogotPass() {
        val auth = FirebaseAuth.getInstance()
        val emailAddress = binding.edtuseremail.text
        if (emailAddress.trim().length != 0){
            auth.sendPasswordResetEmail(emailAddress.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        database.collection(Constant.SYS_USERS)
                            .whereEqualTo(Constant.USER_EMAIL, emailAddress.toString())
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful && task.result?.size() != 0) {
                                    var id= task.result.documents[0].id
                                    database.collection(Constant.SYS_USERS)
                                        .document(id)
                                        .update(Constant.USER_PASS,"123456")
                                    UsingPublic().ShowToast(applicationContext, "Your password has been set 123456")
                                }
                                else{
                                    UsingPublic().ShowToast(applicationContext, "Try again latter!")
                                }
                            }
                    } else {
                        UsingPublic().ShowToast(applicationContext,"Can't not send verification")
                    }
                }
        }
        else{
            UsingPublic().ShowToast(applicationContext,"Email is null")
        }
    }

    private fun SetListener() {
        binding.signuptxt.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
            finish()
        }
        binding.signinbtn.setOnClickListener {
            val dialog = CustomDialog(this@SignInActivity).CreateDialog();
            dialog.show()
            var email = binding.edtuseremail.text
            var pass = binding.edtpassw.text

            database.collection(Constant.SYS_USERS)
                .whereEqualTo(Constant.USER_EMAIL, email.toString())
                .whereEqualTo(Constant.USER_PASS, pass.toString())
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result.size() > 0) {
                        preferenceManager.edit {

                            val document = task.result.documents.get(0)
                            putBoolean(Constant.KEY_IS_SIGN, true)
                            putString(Constant.USER_ID, document.id)
                            putString(
                                Constant.USER_EMAIL,
                                document.getString(Constant.USER_EMAIL).toString()
                            )
                            putString(
                                Constant.USER_NAME,
                                document.getString(Constant.USER_NAME).toString()
                            )
                            putString(
                                Constant.USER_PASS,
                                document.getString(Constant.USER_PASS).toString()
                            )
                            putString(
                                Constant.USER_ANHBIA,
                                document.getString(Constant.USER_ANHBIA).toString()
                            )
                            putString(Constant.USER_TIME,SupportLibary.GetRealDateTime(document.getDate(Constant.USER_TIME) as Date))
                            putString(
                                Constant.USER_IMAGE,
                                document.getString(Constant.USER_IMAGE).toString()
                            )
                            putString(Constant.USER_TOKEN, document.getString(Constant.USER_TOKEN))
                            putString(
                                Constant.USER_URLIMG,
                                document.getString(Constant.USER_URLIMG).toString()
                            )
                            putString(Constant.KEY_THEME_APP, "0")
                        }

                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val token = task.result
                                SendFCMToDataBase(token)
                            } else {
                                UsingPublic().ShowToast(
                                    applicationContext,
                                    "Fetching FCM registration token failed: " + task.exception
                                )
                            }
                        }

                        dialog.dismiss()
                        val intent = Intent(applicationContext, MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        }
                        startActivity(intent)
                    } else {
                        dialog.dismiss()
                        CustomDialog(this@SignInActivity).ShowDialog(
                            "Tên đăng nhập hoặc mật khẩu không đúng",
                            2
                        )
                    }
                }

        }
    }

    private fun SendFCMToDataBase(token: String) {
        var document = database.collection(Constant.SYS_USERS)
            .document(preferenceManager.GetString(Constant.USER_ID).toString())
        document.update(Constant.USER_TOKEN, token)
            .addOnSuccessListener {
                UsingPublic().ShowToast(applicationContext, "Update Success")
            }
            .addOnFailureListener { e ->
                UsingPublic().ShowToast(applicationContext, "Error: ${e.message}")
            }
    }

    fun CheckVal(email: String, pass: String): Boolean {
        binding.requireemail.text = if (email == "") "Nhập email" else ""
        binding.requirepass.text = if (pass == "") "Vui lòng nhập pass" else ""
        return binding.requireemail.text != "" && binding.requirepass.text !== ""
    }
}