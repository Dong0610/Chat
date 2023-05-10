package dongdong.duan.chat.activity.user

import android.annotation.SuppressLint
import android.app.Dialog
import android.hardware.fingerprint.FingerprintManager
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Handler
import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dongdong.duan.chat.R
import dongdong.duan.chat.databinding.ActivityUpdatePassBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.CustomDialog
import dongdong.duan.chat.utility.PreferenceManager
import dongdong.duan.chat.utility.UsingPublic

@Suppress("DEPRECATION")
class UpdatePassActivity : AppCompatActivity() {
    lateinit var binding: ActivityUpdatePassBinding
    lateinit var preferenceManager: PreferenceManager
    var database = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        SetListener()
        LoadUsers()

    }

    private fun LoadUsers() {
        binding.imgtypeicon.setImageBitmap(
            BitmapUtils.StringToBitmap(
                preferenceManager.GetString(
                    Constant.USER_IMAGE
                )
            )
        )
        binding.txtusername.setText(preferenceManager.GetString(Constant.USER_NAME).toString())
    }

    private fun CheckFingerPrint(function: () -> Unit) {
        val fingerprintManager = getSystemService(FingerprintManager::class.java)
        val dialogbinding = layoutInflater.inflate(R.layout.finger_print_layout, null)
        var mydialog = Dialog(this)
        mydialog.setContentView(dialogbinding)
        mydialog.setCancelable(false)
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        mydialog.window?.setLayout(width, height)
        mydialog.show()
        if (fingerprintManager.isHardwareDetected && fingerprintManager.hasEnrolledFingerprints()) {
            val cancellationSignal = CancellationSignal()
            fingerprintManager.authenticate(
                null,
                cancellationSignal,
                0,
                object : FingerprintManager.AuthenticationCallback() {
                    @SuppressLint("MissingInflatedId")
                    @Deprecated("Deprecated in Java")
                    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
                        dialogbinding.findViewById<ImageView>(R.id.imgfinger)
                            .setImageResource(R.drawable.baseline_fingerprint_green)
                        Handler().postDelayed({
                            mydialog.dismiss()
                        }, 2000)
                        function()
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onAuthenticationFailed() {
                        dialogbinding.findViewById<ImageView>(R.id.imgfinger)
                            .setImageResource(R.drawable.baseline_fingerprint_red)
                        Handler().postDelayed({
                            mydialog.dismiss()
                        }, 2000)
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                        dialogbinding.findViewById<ImageView>(R.id.imgfinger)
                            .setImageResource(R.drawable.baseline_fingerprint_red)
                        Handler().postDelayed({
                            mydialog.dismiss()
                        }, 2000)
                    }
                },
                null
            )
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Enter password")
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            builder.setView(input)
            builder.setPositiveButton("OK") { _, _ ->
                val password = input.text.toString()

            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
                // Close app
                finish()
            }
            builder.show()
        }

    }

    private fun SetListener() {
        binding.icbackpress.setOnClickListener {
            onBackPressed()
        }
        binding.txtresetpass.setOnClickListener {
            CheckFingerPrint {
                val auth = FirebaseAuth.getInstance()
                val emailAddress = preferenceManager.GetString(Constant.USER_EMAIL).toString()
                if (emailAddress.trim().length != 0) {
                    auth.sendPasswordResetEmail(emailAddress.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                database.collection(Constant.SYS_USERS)
                                    .whereEqualTo(Constant.USER_EMAIL, emailAddress.toString())
                                    .get()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful && task.result?.size() != 0) {
                                            var id = task.result.documents[0].id
                                            database.collection(Constant.SYS_USERS)
                                                .document(id)
                                                .update(Constant.USER_PASS, "123456")
                                            UsingPublic().ShowToast(
                                                applicationContext,
                                                "Your password has been set 123456"
                                            )
                                        } else {
                                            UsingPublic().ShowToast(
                                                applicationContext,
                                                "Try again latter!"
                                            )
                                        }
                                    }
                            } else {
                                UsingPublic().ShowToast(
                                    applicationContext,
                                    "Can't not send verification"
                                )
                            }
                        }
                } else {
                    UsingPublic().ShowToast(applicationContext, "Email is null")
                }
            }
        }

        binding.btnchangepass.setOnClickListener {

            CheckFingerPrint {
                if (preferenceManager.GetString(Constant.USER_PASS).toString()
                        .equals(binding.txtoldpass.text.toString())
                ) {
                    database.collection(Constant.SYS_USERS)
                        .document(preferenceManager.GetString(Constant.USER_ID).toString())
                        .update(Constant.USER_PASS, binding.txtnewpass.text.toString())
                    CustomDialog(this).ShowDialog("Đổi mật khẩu thành công", 1)
                } else {
                    CustomDialog(this).ShowDialog("Sai mật khẩu", 3)
                }
            }
        }

    }
}