package dongdong.duan.chat.activity.user

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dongdong.duan.chat.MainActivity
import dongdong.duan.chat.R
import dongdong.duan.chat.databinding.ActivityAddNewPostBinding
import dongdong.duan.chat.file.SupportLibary
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.CustomDialog
import dongdong.duan.chat.utility.PreferenceManager
import dongdong.duan.chat.utility.UsingPublic
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


@Suppress("DEPRECATION")
class AddNewPostActivity : AppCompatActivity() {
    lateinit var duration: String
    var mediaPlayer: MediaPlayer? = null
    private var urichoosems: Uri? = null
    private var urichooseimg: Uri? = null
    lateinit var timer: ScheduledExecutorService
    var database = FirebaseFirestore.getInstance()
    var reldb = FirebaseDatabase.getInstance()
    lateinit var preferenceManager: PreferenceManager
    val PICK_FILE = 99
    lateinit var binding: ActivityAddNewPostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        LoadImgRolate()
        SetListener()
        binding.layoutsecutyty.setOnClickListener {
            ChangeSecurity()
        }
    }

    private fun ChangeSecurity() {
        binding.layoutupdate.visibility=View.VISIBLE
        binding.btncancelSEW.setOnClickListener {
            binding.txtserc.text="Chỉ mình tôi"
            postsecutity=1
            binding.layoutupdate.visibility=View.GONE
        }
        binding.btnconfirmSEW.setOnClickListener {
            binding.txtserc.text="Công khai"
            postsecutity=2
            binding.layoutupdate.visibility=View.GONE
        }
    }

    var POST_USERID = "PostUsID"
    var POST_USERNAME = "Post_UsName"
    var POST_STATUS = "Post_Status"
    var POST_URLIMG = "Post_Img"
    var POST_MUSIC_URL = "Post_Music"
    var POST_TIME = "Post_Time"
    var POST_SECURYTY = "Post_Secutity"
    var POST_LIKE = "Post_Like"
    var POST_VIEW = "Post_View"
    var POST_USEIMG = "Post_UsImg"
    var POST_COMMENT = "Post_Comment"
    var POST_SOF = "Post_SOF"

    var postsecutity = 2
    fun GetConVersion(urlimg: String, urlmusuc: String): Any {
        var hasmap = HashMap<String, Any>()
        hasmap[POST_USERID] = preferenceManager.GetString(Constant.USER_ID).toString()
        hasmap[POST_USERNAME] = preferenceManager.GetString(Constant.USER_NAME).toString()
        hasmap[POST_USEIMG] = preferenceManager.GetString(Constant.USER_IMAGE).toString()
        hasmap[POST_STATUS] = binding.txtstatus.text.toString()
        hasmap[POST_URLIMG] = urlimg
        hasmap[POST_MUSIC_URL] = urlmusuc
        hasmap[POST_TIME] = Date()
        hasmap[POST_SECURYTY] = postsecutity
        hasmap[POST_LIKE] = 0
        hasmap[POST_COMMENT] = 0
        hasmap[POST_VIEW] = 0
        return hasmap
    }

    var isplay = true
    private fun SetListener() {
        binding.icbackpress.setOnClickListener {
            onBackPressed()
        }
        binding.layouchonnhac.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "audio/*"
            startActivityForResult(intent, PICK_FILE)
        }
        binding.layoutchonanh.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            this.startActivityForResult(intent, PICK_IMG)
        }
        binding.imgplay.setOnClickListener {
            if (mediaPlayer != null) {
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.pause();
                    binding.imgplay.setImageResource(R.drawable.baseline_play_arrow_24)
                    timer.shutdown();
                } else {
                    timer = Executors.newScheduledThreadPool(1)
                    timer.scheduleAtFixedRate({
                        mediaPlayer!!.start();
                        if (!binding.seekbarload.isPressed()) {
                            binding.seekbarload.setProgress(mediaPlayer!!.currentPosition)
                        }
                    }, 10, 10, TimeUnit.MILLISECONDS)
                    binding.imgplay.setImageResource(R.drawable.baseline_pause_24)
                }
            } else {
                UsingPublic().ShowToast(applicationContext, "Chưa chọn bài")
            }
        }

        binding.savepost.setOnClickListener {
            val dialog = CustomDialog(this@AddNewPostActivity).CreateDialog()
            dialog.show()
            if (urichoosems != null) {
                val reference = FirebaseStorage.getInstance().getReference("Music")
                val ref = reference.child(
                    System.currentTimeMillis().toString() + "." + SupportLibary.FileExTension(
                        urichoosems!!,
                        contentResolver
                    )
                )
                ref.putFile(urichoosems!!)
                    .addOnSuccessListener { taskSnapshot ->
                        dialog.dismiss()
                        // File upload success
                        ref.downloadUrl.addOnSuccessListener { ull ->
                            var value = GetConVersion("", ull.toString())
                            database.collection(Constant.SYS_POST).add(value)
                                .addOnCompleteListener { tast ->
                                    if (tast.isSuccessful) {
                                        val intent = Intent(
                                            applicationContext,
                                            MainActivity::class.java
                                        ).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        }
                                        reldb.getReference(Constant.SYS_POST)
                                            .child(
                                                preferenceManager.GetString(Constant.USER_ID)
                                                    .toString()
                                            )
                                            .child(tast.result.id)
                                            .setValue(value)
                                        val notify: kotlin.collections.HashMap<String, Any> =
                                            GetNotification(tast.result.id)
                                        database.collection(Constant.SYS_NOTIFICATION).add(notify)
                                        startActivity(intent)

                                    }
                                }
                        }


                    }
                    .addOnFailureListener { exception ->
                        // File upload failure
                        UsingPublic().ShowToast(applicationContext, exception.message)
                    }
            } else if (urichooseimg != null) {
                val reference = FirebaseStorage.getInstance().getReference("ImagePost")
                val ref = reference.child(
                    System.currentTimeMillis().toString() + "." + SupportLibary.FileExTension(
                        urichooseimg!!,
                        contentResolver
                    )
                )
                ref.putFile(urichooseimg!!)
                    .addOnSuccessListener { taskSnapshot ->
                        ref.downloadUrl.addOnSuccessListener { ull ->
                            dialog.dismiss()
                            var value = GetConVersion(ull.toString(), "")
                            database.collection(Constant.SYS_POST).add(value)
                                .addOnCompleteListener { tast ->
                                    if (tast.isSuccessful) {
                                        val intent = Intent(
                                            applicationContext,
                                            MainActivity::class.java
                                        ).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        }
                                        val notify: kotlin.collections.HashMap<String, Any> =
                                            GetNotification(tast.result.id)
                                        database.collection(Constant.SYS_NOTIFICATION).add(notify)
                                        startActivity(intent)

                                        reldb.getReference(Constant.SYS_POST)
                                            .child(
                                                preferenceManager.GetString(Constant.USER_ID)
                                                    .toString()
                                            )
                                            .child(tast.result.id)
                                            .setValue(value)
                                        startActivity(intent)
                                    }
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // File upload failure
                        UsingPublic().ShowToast(applicationContext, exception.message)
                    }
            } else {
                val reference = FirebaseStorage.getInstance().getReference("ImagePost")
                val ref = reference.child(
                    System.currentTimeMillis().toString() + "." + SupportLibary.FileExTension(
                        urichooseimg!!,
                        contentResolver
                    )
                )
                ref.putFile(urichooseimg!!)
                    .addOnSuccessListener { taskSnapshot ->
                        dialog.dismiss()
                        var value = GetConVersion("downloadUrl", "")
                        database.collection(Constant.SYS_POST).add(value)
                            .addOnCompleteListener { tast ->
                                if (tast.isSuccessful) {
                                    val intent =
                                        Intent(applicationContext, MainActivity::class.java).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        }
                                    startActivity(intent)
                                    reldb.getReference(Constant.SYS_POST)
                                        .child(
                                            preferenceManager.GetString(Constant.USER_ID).toString()
                                        )
                                        .child(tast.result.id)
                                        .setValue(value)
                                }
                            }
                    }
                    .addOnFailureListener { exception ->
                        // File upload failure
                        UsingPublic().ShowToast(applicationContext, exception.message)
                    }
            }

        }
        binding.seekbarload.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress == 100) {
                    isplay = true;
                }
                if (mediaPlayer != null) {
                    val millis = mediaPlayer!!.currentPosition
                    val total_secs =
                        TimeUnit.SECONDS.convert(millis.toLong(), TimeUnit.MILLISECONDS)
                    val mins = TimeUnit.MINUTES.convert(total_secs, TimeUnit.SECONDS)
                    val secs = total_secs - mins * 60
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mediaPlayer!!.seekTo(binding.seekbarload.getProgress())
            }
        })
    }

    private fun GetNotification(ID:String): HashMap<String, Any> {
        var hasmap = kotlin.collections.HashMap<String, Any>()
        hasmap[Constant.NOTI_ID_VIEW]=ID
        hasmap[Constant.NOTI_USER_ID]=preferenceManager.GetString(Constant.USER_ID).toString()
        hasmap[Constant.NOTI_USER_NAME]=preferenceManager.GetString(Constant.USER_NAME).toString()
        hasmap[Constant.NOTI_USER_IMG]=preferenceManager.GetString(Constant.USER_IMAGE).toString()
        hasmap[Constant.NOTI_USER_ID]=preferenceManager.GetString(Constant.USER_ID).toString()
        hasmap[Constant.NOTI_DATE]=Date()
        hasmap[Constant.NOTY_TYPE]="post"
        hasmap[Constant.NOTI_STATUS]=preferenceManager.GetString(Constant.USER_NAME).toString()+" đã thêm một bài viết"
        return hasmap
    }

    var PICK_IMG = 10010

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    binding.layoutmusic.visibility = View.VISIBLE
                    val uri = data.data
                    urichoosems = data.data!!
                    CreateMediaPlayer(uri!!)
                    urichooseimg = null
                }
            }
        } else if (requestCode == PICK_IMG) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    binding.imguserchoose.visibility = View.VISIBLE
                    val uri = data.data
                    urichooseimg = data.data!!
                    urichoosems = null
                    CreateMediaPlayer(uri!!)
                    binding.imguserchoose.setImageURI(urichooseimg)
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun GetNameFromUri(uri: Uri): CharSequence? {
        var fileName = ""
        var cursor: Cursor? = null
        cursor = contentResolver.query(
            uri, arrayOf(
                MediaStore.Images.ImageColumns.DISPLAY_NAME
            ), null, null, null
        )
        if (cursor != null && cursor.moveToFirst()) {
            fileName =
                cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))
        }
        if (cursor != null) {
            cursor.close()
        }
        return fileName
    }

    private fun CreateMediaPlayer(uri: Uri) {

        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        try {
            mediaPlayer!!.setDataSource(applicationContext, uri)
            mediaPlayer!!.prepare()
            binding.txtnamesong.text = GetNameFromUri(uri)
            val millis = mediaPlayer!!.duration
            val total_secs = TimeUnit.SECONDS.convert(millis.toLong(), TimeUnit.MILLISECONDS)
            val mins = TimeUnit.MINUTES.convert(total_secs, TimeUnit.SECONDS)
            val secs = total_secs - mins * 60
            duration = "$mins:$secs"
            binding.seekbarload.setMax(millis)
            binding.seekbarload.setProgress(0)
            mediaPlayer!!.setOnCompletionListener { ReleaseMediaPlayer() }
        } catch (e: IOException) {
            UsingPublic().ShowToast(applicationContext, e.message.toString())
        }
    }

    private fun ReleaseMediaPlayer() {
        timer.shutdown()
        mediaPlayer!!.release();
        mediaPlayer = null
        binding.seekbarload.setMax(100);
        binding.seekbarload.setProgress(0);
    }

    override fun onDestroy() {
        super.onDestroy()
        if (urichoosems != null) {
            ReleaseMediaPlayer()
        }

    }

    private fun LoadImgRolate() {
        binding.imgMusic
        val rotateAnimation = RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotateAnimation.duration = 3000 // 1 second
        rotateAnimation.repeatCount = Animation.INFINITE // repeat indefinitely
        rotateAnimation.interpolator = LinearInterpolator()
        binding.imgMusic.startAnimation(rotateAnimation)
    }
}