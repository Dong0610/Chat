package dongdong.duan.chat.activity.post

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import com.google.firebase.firestore.FirebaseFirestore
import dongdong.duan.chat.R
import dongdong.duan.chat.activity.chat.ImageShow
import dongdong.duan.chat.databinding.ActivityPostViewDetailBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.mode.Posts
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.PreferenceManager
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class PostViewDetailActivity : AppCompatActivity() {
    lateinit var binding:ActivityPostViewDetailBinding
     var themetype=0
    var mediaPlayer = MediaPlayer()
    lateinit var timer: ScheduledExecutorService
    lateinit var preferenceManager:PreferenceManager
    var database=FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPostViewDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager= PreferenceManager(applicationContext)
        var post= intent.getSerializableExtra("post") as Posts
        themetype = preferenceManager.GetString(Constant.KEY_THEME_APP)!!.toInt()
        database.collection(Constant.SYS_POST).document(post.ID_Post)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val view = it.result.getLong(Constant.POST_VIEW)
                    database.collection(Constant.SYS_POST).document(post.ID_Post)
                        .update(Constant.POST_VIEW, view!! + 1)
                }
            }
        SetData(post)
    }
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    fun SetData(posts: Posts) {

        binding.seekbarload.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val millis = mediaPlayer.currentPosition
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mediaPlayer.seekTo(binding.seekbarload.getProgress())
            }
        })
        if(themetype==1){
            binding.bgroundapp.background= getDrawable(R.drawable.itemview_bg_listpost_light)
            binding.txtnameuspos.setTextColor(Color.BLACK)
            binding.txttimecm.setTextColor(Color.GRAY)
            binding.txtshare.setTextColor(Color.GRAY)
            binding.txtcountcm.setTextColor(Color.GRAY)
            binding.txtstatus.setTextColor(Color.BLACK)
        }
        if (posts.Url_Img==""){
            binding.layoutmusic.visibility= View.VISIBLE
            binding.imgpost.visibility= View.GONE
        }
        else{
            BitmapUtils.ShowImage(applicationContext,posts.Url_Img,binding.imgpost)
            binding.layoutmusic.visibility= View.GONE
            binding.imgpost.visibility= View.VISIBLE
        }

        binding.imgpost.setOnClickListener {
            var intent= Intent(applicationContext, ImageShow::class.java)
            intent.putExtra("urlimg",posts.Url_Img)
            intent.putExtra("name",posts.User_Name)
            startActivity(intent)

        }
        val database= FirebaseFirestore.getInstance()

        if(posts.Uri_Music!=""){
            mediaPlayer.setDataSource(posts.Uri_Music)
            binding.imgplay.setOnClickListener {

                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause();
                    binding.imgplay.setImageResource(R.drawable.baseline_play_arrow_24)
                    timer.run { shutdown() };
                } else {
                    timer = Executors.newScheduledThreadPool(1)
                    timer.scheduleAtFixedRate({
                        mediaPlayer.prepare()
                        mediaPlayer.start();
                        if (!binding.seekbarload.isPressed()) {
                            binding.seekbarload.setProgress(mediaPlayer!!.currentPosition)
                        }
                    }, 10, 10, TimeUnit.MILLISECONDS)
                    binding.imgplay.setImageResource(R.drawable.baseline_pause_24)
                }
            }
        }
        if(posts.Security!=1){
            binding.layoutuserpostview.visibility= View.VISIBLE
            binding.layoutsecuruty.visibility= View.GONE
        }
        else{
            binding.layoutuserpostview.visibility= View.GONE
            binding.layoutsecuruty.visibility= View.VISIBLE
        }

        database.collection(Constant.SYS_POST).document(posts.ID_Post)
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    var view= it.result.getLong(Constant.POST_LIKE)
                    binding.txtcoutlike.text=view!!.toString()+" thích"
                }
            }
        var islike: Int=posts.islike

        if(islike==1){
            binding.iconlikeuser.setImageResource(R.drawable.icon_likeok)
            binding.iconlikeuser.setOnClickListener {
                database.collection(Constant.SYS_POST).document(posts.ID_Post)
                    .get()
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            var view= it.result.getLong(Constant.POST_LIKE)
                            binding.txtcoutlike.text=view!!.toString()+" thích"
                        }
                    }
//                if(islike==1){
//                    islike=0
//                    postListener.IncreaseLike(posts.ID_Post,islike)
//                    binding.iconlikeuser.setImageResource(R.drawable.icon_likeok)
//                }
//                else {
//                    islike=1
//                    postListener.IncreaseLike(posts.ID_Post,islike)
//                    binding.iconlikeuser.setImageResource(R.drawable.icon_none_like)
//                }
//                postListener.IncreaseLike(posts.ID_Post,islike)

            }
        }
        else {
            binding.iconlikeuser.setImageResource(R.drawable.icon_none_like)
            binding.iconlikeuser.setOnClickListener {
                database.collection(Constant.SYS_POST).document(posts.ID_Post)
                    .get()
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            var view= it.result.getLong(Constant.POST_LIKE)
                            binding.txtcoutlike.text=view!!.toString()+" thích"
                        }
                    }
//                if(islike==1){
//                    islike=0
//                    postListener.IncreaseLike(posts.ID_Post,islike)
//                    binding.iconlikeuser.setImageResource(R.drawable.icon_likeok)
//                }
//                else {
//                    islike=1
//                    postListener.IncreaseLike(posts.ID_Post,islike)
//                    binding.iconlikeuser.setImageResource(R.drawable.icon_none_like)
//                }
//                postListener.IncreaseLike(posts.ID_Post,islike)

            }
        }

        binding.iconcommant.setOnClickListener {
            database.collection(Constant.SYS_POST).document(posts.ID_Post)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val view = it.result.getLong(Constant.POST_VIEW)
                        database.collection(Constant.SYS_POST).document(posts.ID_Post)
                            .update(Constant.POST_VIEW, view!! + 1)
                    }
                }
            val intent = Intent(applicationContext, CommentPostActivity::class.java)
            intent.putExtra("postID", posts.ID_Post)
            intent.putExtra("islike", islike.toString())
            startActivity(intent)
        }
        binding.root.setOnClickListener {
            database.collection(Constant.SYS_POST).document(posts.ID_Post)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val view = it.result.getLong(Constant.POST_VIEW)
                        database.collection(Constant.SYS_POST).document(posts.ID_Post)
                            .update(Constant.POST_VIEW, view!! + 1)
                    }
                }
        }

        binding.txtcountcm.text=posts.Comment.toString()+" Bình luận"
        binding.txtshare.text=posts.View.toString()+" lượt xem"
        binding.btnundo.setOnClickListener {
            binding.layoutpost.visibility= View.VISIBLE
            binding.layoutundo.visibility= View.GONE
        }
        binding.icclose.setOnClickListener {
            binding.layoutpost.visibility= View.GONE
            binding.layoutundo.visibility= View.VISIBLE
        }
        binding.imguser.setImageBitmap(BitmapUtils.StringToBitmap(posts.User_IMG))
        binding.txtnameuspos.text=posts.User_Name
        binding.txtstatus.text=posts.Status
        binding.txttimecm.text=posts.Time_Put.toString()

    }
}