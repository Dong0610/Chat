package dongdong.duan.chat.activity.account

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import dongdong.duan.chat.BaseActivity
import dongdong.duan.chat.MainActivity
import dongdong.duan.chat.databinding.ActivitySignUpBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.file.SupportLibary
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.CustomDialog
import dongdong.duan.chat.utility.PreferenceManager
import dongdong.duan.chat.utility.UsingPublic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.util.*

@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    lateinit var uriImg: Uri
    private var encoderImageStr: String = ""
    lateinit var username: String
    lateinit var enailus: String
    lateinit var pass: String
    lateinit var repass: String
    lateinit var preferenceManager: PreferenceManager
    private val reference = FirebaseStorage.getInstance().getReference("ImgUsers")
    private var database = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        kotlin.run {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        if (this.preferenceManager.GetBoolean(Constant.KEY_IS_SIGN)) {
            startActivity(Intent(this.applicationContext, MainActivity::class.java))
            finish()
        }

        SetListener()
    }
    }

    private fun SetListener() {
        binding.signupbtn.setOnClickListener {
            if (CheckValue()) {
                val email = binding.edtuseremail.text.toString()
                CheckEmail(email) { exits ->
                    if (exits) {
                        CustomDialog(this@SignUpActivity).ShowDialog("Email đã đăng kí", 2)
                    } else {
                        var storageref = reference.child(
                            System.currentTimeMillis()
                                .toString() + "." + SupportLibary.FileExTension(
                                uriImg,
                                contentResolver
                            )
                        )
                        storageref.putFile(uriImg).addOnSuccessListener {
                            storageref.downloadUrl.addOnSuccessListener { uri ->
                                SignUpNewAcc(uri.toString())
                            }.addOnFailureListener { ex ->
                                UsingPublic().ShowToast(applicationContext, ex.message)
                            }
                        }.addOnFailureListener { ex ->
                            UsingPublic().ShowToast(applicationContext, ex.message)
                        }

                    }

                }
            }
            binding.imgUser.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "image/*"
                }
                this.startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
            }
            binding.signintxt.setOnClickListener {
                startActivity(Intent(applicationContext, SignInActivity::class.java))
            }
        }
    }

    private val PICK_IMAGE_REQUEST_CODE = 1

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data?.data != null) {
            val imageUri = data.data
            uriImg = data.data!!
//            val bitmap= BitmapUtils.BitmapFromUriCusSize(applicationContext,imageUri, com.intuit.sdp.R.dimen._125sdp)
//            encoderImageStr=BitmapUtils.BitmapToString
            // binding.imgUser.setImageBitmap(bitmap)
            try {
                val inputStream =
                    imageUri?.let { contentResolver.openInputStream(it) }
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.imgUser.setImageBitmap(bitmap)
                encoderImageStr = BitmapUtils.EncoderImage(bitmap)
                binding.imgUser.setImageBitmap(bitmap)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

        }
    }

    fun CreateAuthUser(email: String, pass: String) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // User creation successful
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                database.collection(Constant.SYS_USERS)
                                    .whereEqualTo(Constant.USER_EMAIL,email)
                                    .get()
                                    .addOnCompleteListener { task->
                                        if(task.isSuccessful&&task.result.size()>0){
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
                                val exception = task.exception
                                UsingPublic().ShowToast(applicationContext, exception)
                            }
                        }

                } else {
                    // User creation failed
                    val exception = task.exception
                    UsingPublic().ShowToast(applicationContext, exception)
                }
            }
    }

    private fun signUpNewAcc(uriL: String) {
        val dialog = CustomDialog(this@SignUpActivity).CreateDialog()
        dialog.show()
        val time = Date()
        val user = HashMap<String, Any>()
        user[Constant.USER_NAME] = username
        user[Constant.USER_EMAIL] = enailus
        user[Constant.USER_PASS] = pass
        user[Constant.USER_TIME] = time.toString()
        user[Constant.USER_IMAGE] = encoderImageStr
        user[Constant.USER_TOKEN] = ""
        user[Constant.USER_ANHBIA]=""
        user[Constant.USER_URLIMG] = uriL
        user[Constant.USER_ONLINE] = "0"
        CreateAuthUser(enailus, pass)
        database.collection(Constant.SYS_USERS).add(user)
            .addOnSuccessListener { document ->
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result
                        SendFCMToDataBase(token)
                        preferenceManager.edit {
                            putBoolean(Constant.KEY_IS_SIGN, true)
                            putString(Constant.USER_ID, document.id)
                            putString(Constant.USER_EMAIL, enailus)
                            putString(Constant.USER_NAME, username)
                            putString(Constant.USER_PASS, pass)
                            putString(Constant.USER_TIME, time.toString())
                            putString(Constant.USER_IMAGE, encoderImageStr)
                            putString(Constant.USER_ANHBIA,"")
                            putString(Constant.USER_TOKEN, token)
                            putString(Constant.USER_URLIMG, uriL)
                            putString(Constant.KEY_THEME_APP, "0")

                        }
                    } else {
                        UsingPublic().ShowToast(applicationContext, "Fetching FCM registration token failed: "+task.exception)
                    }
                }
                dialog.dismiss()
                val intent = Intent(applicationContext, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                startActivity(intent)
            }

    }
    private fun SendFCMToDataBase(token: String) {
        var document=database.collection(Constant.SYS_USERS)
            .document(preferenceManager.GetString(Constant.USER_ID).toString())
        document.update(Constant.USER_TOKEN,token)
            .addOnSuccessListener {
                UsingPublic().ShowToast(applicationContext,"Update Success")
            }
            .addOnFailureListener { e->
                UsingPublic().ShowToast(applicationContext,"Error: ${e.message}")
            }
    }
    @SuppressLint("SuspiciousIndentation")
    private fun SignUpNewAcc(uriL: String) {
        val dialog = CustomDialog(this@SignUpActivity).CreateDialog()
        dialog.show()
        val time = Date()
        val user = HashMap<String, Any>()
        user[Constant.USER_NAME] = username
        user[Constant.USER_EMAIL] = enailus
        user[Constant.USER_PASS] = pass
        user[Constant.USER_ANHBIA] = ""
        user[Constant.USER_TIME] = time.toString()
        user[Constant.USER_IMAGE] = encoderImageStr
        user[Constant.USER_TOKEN] = ""
        user[Constant.USER_URLIMG] = uriL
        user[Constant.USER_ONLINE] = "0"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Upload image to Firebase storage
                val ref = reference.child(System.currentTimeMillis().toString() + "." + SupportLibary.FileExTension(uriImg, contentResolver))
                ref.putFile(uriImg).await()
                val downloadUrl = ref.downloadUrl.await().toString()

                // Create user account in Firebase Authentication
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(enailus, pass).await()

                // Add user data to Firestore
              var document=   database.collection(Constant.SYS_USERS).add(user).await()
                // Save user data to shared preferences
                withContext(Dispatchers.Main) {
                    preferenceManager.edit {
                        putBoolean(Constant.KEY_IS_SIGN, true)
                        putString(Constant.USER_ID, document.id)
                        putString(Constant.USER_EMAIL, enailus)
                        putString(Constant.USER_NAME, username)
                        putString(Constant.USER_PASS, pass)
                        putString(Constant.USER_ANHBIA,"")
                        putString(Constant.USER_TIME, time.toString())
                        putString(Constant.USER_IMAGE, encoderImageStr)
                        putString(Constant.USER_TOKEN, "")
                        putString(Constant.USER_URLIMG, downloadUrl)
                        putString(Constant.KEY_THEME_APP, "0")
                    }
                    dialog.dismiss()
                    val intent = Intent(applicationContext, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                    startActivity(intent)
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    UsingPublic().ShowToast(applicationContext, ex.message)
                    dialog.dismiss()
                }
            }
        }
    }

    private fun CheckValue(): Boolean {
        username = binding.edtusername.text.toString()
        enailus = binding.edtuseremail.text.toString()
        pass = binding.edtuserpass.text.toString()
        repass = binding.edtuservfpass.text.toString()

        if (encoderImageStr.isEmpty()) {
            UsingPublic().ShowToast(applicationContext, "Chưa chọn ảnh")
            return false
        }

        binding.requrename.text =
            if (username.isEmpty() || username.length <= 3) "Yêu cầu tên dài hơn 3 kí tự *" else ""
        binding.requireemail.text =
            if (!Patterns.EMAIL_ADDRESS.matcher(enailus)
                    .matches()
            ) "Yêu cầu 1 email" else ""
        binding.requirepass.text = if (pass.length < 4) "Mật khẩu lớn hơn bốn kí tự" else ""
        binding.requirvfpass.text =
            if (!repass.equals(pass)) "Nhập lại mật khẩu không đúng" else ""

        return binding.requrename.text.isEmpty() && binding.requireemail.text.isEmpty() && binding.requirepass.text.isEmpty() && binding.requirvfpass.text.isEmpty()
    }


    private fun CheckEmail(email: String, callback: (Boolean) -> Unit) {
        var database = FirebaseFirestore.getInstance()
        database.collection(Constant.SYS_USERS)
            .whereEqualTo(Constant.USER_EMAIL, email)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result?.size() != 0) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
    }
}