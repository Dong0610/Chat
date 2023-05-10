package dongdong.duan.chat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import dongdong.duan.chat.R
import dongdong.duan.chat.databinding.ItemlistPostViewBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.file.SupportLibary
import dongdong.duan.chat.listener.PostListener
import dongdong.duan.chat.mode.Posts
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.PreferenceManager
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class PostAdapter(var listposts:ArrayList<Posts>, var themetype:Int, var context: Context,var postListener: PostListener): RecyclerView.Adapter<PostAdapter.PostsViewHolder>()  {
    inner class PostsViewHolder(var binding: ItemlistPostViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

       var mediaPlayer = MediaPlayer()
        lateinit var timer: ScheduledExecutorService
        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n", "SuspiciousIndentation")
        fun SetData(posts: Posts,userID:String) {
            if(posts.User_ID.equals(userID)){
                posts.Security=2
            }
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
                binding.bgroundapp.background= context.getDrawable(R.drawable.itemview_bg_listpost_light)
                binding.txtnameuspos.setTextColor(Color.BLACK)
                binding.txttimecm.setTextColor(Color.GRAY)
                binding.txtshare.setTextColor(Color.GRAY)
                binding.txtcountcm.setTextColor(Color.GRAY)
                binding.txtstatus.setTextColor(Color.BLACK)
              // binding.iclike.setImageDrawable(context.getDrawable(R.drawable.ic_round_like_light))

            }
            if (posts.Url_Img==""){
                binding.layoutmusic.visibility=View.VISIBLE
                binding.imgpost.visibility=View.GONE
            }
            else{
                BitmapUtils.ShowImage(context,posts.Url_Img,binding.imgpost)
                binding.layoutmusic.visibility=View.GONE
                binding.imgpost.visibility=View.VISIBLE
            }

            binding.imgpost.setOnClickListener {
                postListener.ShowImage(posts.Url_Img,posts.User_Name)
            }
            val database=FirebaseFirestore.getInstance()

            if(posts.Uri_Music!=""){
                mediaPlayer.setDataSource(posts.Uri_Music)
                binding.imgplay.setOnClickListener {

                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.pause();
                        binding.imgplay.setImageResource(R.drawable.baseline_play_arrow_24)
                        timer.shutdown();
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
                binding.layoutuserpostview.visibility=View.VISIBLE
                binding.layoutsecuruty.visibility=View.GONE
            }
            else{
                binding.layoutuserpostview.visibility=View.GONE
                binding.layoutsecuruty.visibility=View.VISIBLE
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
            }
            else {
                binding.iconlikeuser.setImageResource(R.drawable.icon_none_like)
            }
            binding.iconlikeuser.setOnClickListener {
                if(islike==0){
                    islike=0
                    postListener.IncreaseLike(posts.ID_Post,0)
                    database.collection(Constant.SYS_POST).document(posts.ID_Post)
                        .get()
                        .addOnCompleteListener {
                            if(it.isSuccessful){
                                var view= it.result.getLong(Constant.POST_LIKE)
                                binding.txtcoutlike.text= abs(view!!).toString()+" thích"
                            }
                        }
                    binding.iconlikeuser.setImageResource(R.drawable.icon_likeok)

                }
                else if(islike==1) {
                    islike=1
                    postListener.IncreaseLike(posts.ID_Post,-1)
                    database.collection(Constant.SYS_POST).document(posts.ID_Post)
                        .get()
                        .addOnCompleteListener {
                            if(it.isSuccessful){
                                var view= it.result.getLong(Constant.POST_LIKE)
                                binding.txtcoutlike.text= abs( view!!).toString()+" thích"
                            }
                        }
                    binding.iconlikeuser.setImageResource(R.drawable.icon_none_like)

                }
                postListener.IncreaseLike(posts.ID_Post,islike)

            }
            binding.iconcommant.setOnClickListener {
                postListener.Comment(posts.ID_Post,posts.islike)
            }
            binding.root.setOnClickListener {
                postListener.IncreaseView(posts.ID_Post)
            }
            binding.root.setOnClickListener {
                postListener.PostClick(posts)
            }

            binding.txtcountcm.text= abs(posts.Comment).toString()+" Bình luận"
            binding.txtshare.text=abs( posts.View).toString()+" lượt xem"
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):PostsViewHolder  {
        return PostsViewHolder(
            ItemlistPostViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        var userid=PreferenceManager(context).GetString(Constant.USER_ID).toString()
        holder.SetData(listposts.get(position),userid)
    }

    override fun getItemCount(): Int {
        return listposts.size
    }
}