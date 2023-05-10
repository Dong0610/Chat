package dongdong.duan.chat.activity.reels

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dongdong.duan.chat.MainActivity
import dongdong.duan.chat.databinding.ActivityReelViewBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.file.SupportLibary
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.PreferenceManager
import dongdong.duan.chat.utility.UsingPublic
import java.util.*

class ReelViewActivity : AppCompatActivity() {
    lateinit var binding: ActivityReelViewBinding
    private var scaleFactor = 1.0f
    private var reldatabase = FirebaseDatabase.getInstance()
    private var database = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance().getReference("ImgReels")
    lateinit var preferenceManager: PreferenceManager

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReelViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = getIntent()
        preferenceManager = PreferenceManager(applicationContext)
        val uri: Uri? = intent.data
        if (uri != null) {
            BitmapUtils.ShowImage(
                applicationContext,
                uri,
                binding.imgview
            )
        } else {
            UsingPublic().ShowToast(applicationContext, "This is null")
        }
        binding.btncontinue.setOnClickListener {
            PutReels(uri)
        }
    }

    private fun GetNotification(ID:String): HashMap<String, Any> {
        var hasmap = kotlin.collections.HashMap<String, Any>()
        hasmap[Constant.NOTI_ID_VIEW]=ID
        hasmap[Constant.NOTI_USER_ID]=preferenceManager.GetString(Constant.USER_ID).toString()
        hasmap[Constant.NOTI_USER_NAME]=preferenceManager.GetString(Constant.USER_NAME).toString()
        hasmap[Constant.NOTI_USER_IMG]=preferenceManager.GetString(Constant.USER_IMAGE).toString()
        hasmap[Constant.NOTI_USER_ID]=preferenceManager.GetString(Constant.USER_ID).toString()
        hasmap[Constant.NOTI_DATE]=Date()
        hasmap[Constant.NOTY_TYPE]="reels"
        hasmap[Constant.NOTI_STATUS]=preferenceManager.GetString(Constant.USER_NAME).toString()+" đã thêm vào tin của mình"
        return hasmap
    }
    var SYS_REELS = "Reels_Sys"
    var REELS_US_ID = "Reels_User_Id"
    var REELS_US_NAME = "Reels_User_Name"
    var REELS_US_IMG="Reels_User_Img"
    var REELS_TIME = "Reels_Time"
    var REELS_URL_IMG = "Reels_Url"
    var REELS_URL_MUSIC = "Reels_Url_Music"
    var REELS_SECURITY = "Reels_Security"

    @SuppressLint("SuspiciousIndentation")
    private fun PutReels(uri: Uri?) {

        val ref = storage.child(
            System.currentTimeMillis().toString() + "." + SupportLibary.FileExTension(
                uri!!,
                contentResolver
            )
        )
        ref.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                ref.downloadUrl.addOnSuccessListener { url ->
                    val reels = HashMap<String, Any>()
                    reels[REELS_US_ID] = preferenceManager.GetString(Constant.USER_ID).toString()
                    reels[REELS_US_NAME] = preferenceManager.GetString(Constant.USER_NAME).toString()
                    reels[REELS_US_IMG] = preferenceManager.GetString(Constant.USER_IMAGE).toString()
                    reels[REELS_TIME] = Date()
                    reels[REELS_URL_IMG] = url.toString()
                    reels[REELS_URL_MUSIC] = ""
                    reels[REELS_SECURITY] = "1"
                    database.collection(Constant.SYS_REELS)
                        .add(reels)
                        .addOnCompleteListener {
                            var idreesl=it.result.id
                            reldatabase.getReference(Constant.SYS_REELS)
                                .child(preferenceManager.GetString(Constant.USER_ID).toString())
                                .child(it.result.id)
                                .setValue(reels)
                                .addOnCompleteListener {
                                    database.collection(Constant.SYS_NOTIFICATION).add(GetNotification(idreesl))
                                    startActivity(
                                        Intent(
                                            applicationContext,
                                            MainActivity::class.java
                                        ).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        })
                                }
                        }
                }
            }
    }

}