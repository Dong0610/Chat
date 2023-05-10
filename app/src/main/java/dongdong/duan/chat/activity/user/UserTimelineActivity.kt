package dongdong.duan.chat.activity.user

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import dongdong.duan.chat.R
import dongdong.duan.chat.activity.chat.ImageShow
import dongdong.duan.chat.activity.post.CommentPostActivity
import dongdong.duan.chat.activity.post.PostViewDetailActivity
import dongdong.duan.chat.activity.reels.ReelsDetailViewActivity
import dongdong.duan.chat.adapter.PostAdapter
import dongdong.duan.chat.databinding.ActivityReelsDetailViewBinding
import dongdong.duan.chat.databinding.ActivityUserTimelineBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.file.SupportLibary
import dongdong.duan.chat.listener.PostListener
import dongdong.duan.chat.mode.Posts
import dongdong.duan.chat.mode.Reels
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.PreferenceManager
import java.util.*
import kotlin.collections.ArrayList

class UserTimelineActivity : AppCompatActivity(), PostListener,ReelsPageListener {
    lateinit var binding: ActivityUserTimelineBinding
    val reldatabase = FirebaseDatabase.getInstance()
    lateinit var preferenceManager: PreferenceManager

    //var theme: Int = 0
    val database = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserTimelineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        val theme = preferenceManager.GetString(Constant.KEY_THEME_APP).toString().toInt()
        LoadPostView {
            binding.rcposttimeline.visibility=View.VISIBLE
            binding.rcposttimeline.adapter = PostAdapter(it, 1, applicationContext, this)
        }
        binding.btnpost.setOnClickListener {
            binding.progressload.visibility = View.VISIBLE
            binding.rcposttimeline.visibility=View.VISIBLE
            binding.viewpageritem.visibility=View.GONE
            LoadPostView {
                binding.rcposttimeline.adapter = PostAdapter(it, 1, applicationContext, this)
                binding.progressload.visibility = View.GONE
            }
        }
        binding.btntin.setOnClickListener {
            binding.rcposttimeline.visibility=View.GONE
            binding.viewpageritem.visibility=View.VISIBLE

            LoadReels {
                binding.viewpageritem.adapter=ReelsPagerAdapter(it,applicationContext,this)
            }
        }

    }

    fun LoadReels(listreels: (ArrayList<Reels>) -> Unit) {
        val listreel = ArrayList<Reels>()
        database.collection(Constant.SYS_REELS).get().addOnSuccessListener { snapshot ->
            val userIds = snapshot.documents.map { it.id }
            val query = reldatabase.getReference(Constant.SYS_FRIEND)
                .child(preferenceManager.GetString(Constant.USER_ID).toString())
                .orderByKey()
                .startAt(userIds.first())
                .endAt(userIds.last())
            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val friendTypeMap = mutableMapOf<String, Long?>()
                    for (document in task.result.children) {
                        friendTypeMap[document.key.toString()] =
                            document.child(Constant.FRIEND_TYPE).value as Long?
                    }
                    for (document in snapshot.documents) {
                        val friendType = friendTypeMap[document.id]
                        if (friendType?.toInt() != 1) {
                            val reels = Reels(
                                REELS_ID =
                                document.id,
                                REELS_US_ID = document.getString(Constant.REELS_US_ID).toString(),
                                REELS_US_NAME = document.getString(Constant.REELS_US_NAME)
                                    .toString(),
                                REELS_US_IMG = document.getString(Constant.REELS_US_IMG).toString(),
                                REELS_TIME = SupportLibary.GetRealDateTime(document.getDate(Constant.REELS_TIME) as Date)!!,
                                REELS_URL_IMG = document.getString(Constant.REELS_URL_IMG)
                                    .toString(),
                                REELS_URL_MUSIC = document.getString(Constant.REELS_URL_MUSIC)
                                    .toString(),
                                REELS_SECURITY = document.getString(Constant.REELS_SECURITY)
                                    .toString()
                            )
                            listreel.add(reels)

                        }
                    }
                    listreels(listreel)
                }
            }
        }.addOnFailureListener { ex ->
            SupportLibary.ShowDialog(
                ex.message.toString(),
                3,
                this@UserTimelineActivity as Fragment
            )
        }

    }

    fun LoadPostView(listpost: (ArrayList<Posts>) -> Unit) {
        database.collection(Constant.SYS_POST)
            .get().addOnSuccessListener { task ->
                if (!task.isEmpty) {
                    val listpost = mutableListOf<Posts>()
                    val postsIds = task.documents.mapNotNull { it.id }
                    val likesRef = reldatabase.getReference("UserLike")
                    likesRef.addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        @SuppressLint("UseRequireInsteadOfGet")
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val likesMap = mutableMapOf<String, Int>()
                            for (postId in postsIds) {
                                val userLikeSnapshot = snapshot.child(postId)
                                val isLiked =
                                    userLikeSnapshot.child(
                                        preferenceManager.GetString(Constant.USER_ID).toString()
                                    )
                                        .exists()
                                likesMap[postId] = if (isLiked) 1 else 0
                            }
                            for (document in task.documents) {
                                val postId = document.id
                                val post = Posts(
                                    ID_Post = postId,
                                    User_ID = document.getString(Constant.POST_USERID)
                                        ?: "",
                                    User_Name = document.getString(Constant.POST_USERNAME)
                                        ?: "",
                                    User_IMG = document.getString(Constant.POST_USEIMG)
                                        ?: "",
                                    Uri_Music = document.getString(Constant.POST_MUSIC_URL)
                                        ?: "",
                                    Status = document.getString(Constant.POST_STATUS)
                                        ?: "",
                                    Url_Img = document.getString(Constant.POST_URLIMG)
                                        ?: "",
                                    Time_Put = SupportLibary.GetRealDateTime(
                                        document.getDate(Constant.POST_TIME)!!
                                    ),
                                    Security = document.getLong(Constant.POST_SECURYTY)
                                        ?.toInt() ?: 0,
                                    View = document.getLong(Constant.POST_VIEW)
                                        ?.toInt() ?: 0,
                                    Like = document.getLong(Constant.POST_LIKE)
                                        ?.toInt() ?: 0,
                                    Comment = document.getLong(Constant.POST_COMMENT)
                                        ?.toInt() ?: 0,
                                    islike = likesMap[postId] ?: 0
                                )
                                if (post.User_ID.equals(
                                        preferenceManager.GetString(Constant.USER_ID).toString()
                                    )
                                ) {
                                    listpost.add(post)
                                }
                            }
                            listpost(listpost as ArrayList<Posts>)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                applicationContext,
                                "Error: ${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    applicationContext,
                    "Error: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun IncreaseView(postID: String) {
        database.collection(Constant.SYS_POST).document(postID)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val view = it.result.getLong(Constant.POST_VIEW)
                    database.collection(Constant.SYS_POST).document(postID)
                        .update(Constant.POST_VIEW, view!! + 1)
                }
            }
    }

    override fun IncreaseLike(postID: String, islike: Int) {
        if (islike == 0) {
            val hashMap = HashMap<String, Any>()
            hashMap[Constant.USER_ID] =
                preferenceManager.GetString(Constant.USER_ID).toString()
            hashMap[Constant.USER_NAME] =
                preferenceManager.GetString(Constant.USER_NAME).toString()
            hashMap[Constant.USER_IMAGE] =
                preferenceManager.GetString(Constant.USER_IMAGE).toString()
            reldatabase.getReference("UserLike/" + postID)
                .child(preferenceManager.GetString(Constant.USER_ID).toString())
                .setValue(hashMap)
            database.collection(Constant.SYS_POST).document(postID)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        var view = it.result.getLong(Constant.POST_LIKE)
                        if(view!!.toInt()<=0){
                            view=0
                        }
                        database.collection(Constant.SYS_POST).document(postID)
                            .update(Constant.POST_LIKE, view + 1)
                    }
                }
        } else {
            reldatabase.getReference("UserLike/" + postID)
                .child(preferenceManager.GetString(Constant.USER_ID).toString())
                .removeValue()
            database.collection(Constant.SYS_POST).document(postID)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val view = it.result.getLong(Constant.POST_LIKE)
                        if(view!!.toInt()<=0){
                            database.collection(Constant.SYS_POST).document(postID)
                                .update(Constant.POST_LIKE, 0)
                        }
                        else{
                            database.collection(Constant.SYS_POST).document(postID)
                                .update(Constant.POST_LIKE, view!! +islike)
                        }

                    }
                }
        }
    }

    override fun Comment(PosIS: String, islike: Int) {
        database.collection(Constant.SYS_POST).document(PosIS)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val view = it.result.getLong(Constant.POST_VIEW)
                    database.collection(Constant.SYS_POST).document(PosIS)
                        .update(Constant.POST_VIEW, view!! + 1)
                }
            }
        val intent = Intent(applicationContext, CommentPostActivity::class.java)
        intent.putExtra("postID", PosIS)
        intent.putExtra("islike", islike.toString())
        startActivity(intent)
    }

    override fun PlayMusic(urlms: String, play: Int) {

    }
    override fun ShowImage(urlimg: String, name: String) {
        val intent = Intent(applicationContext, ImageShow::class.java)
        intent.putExtra("urlimg", urlimg)
        intent.putExtra("name", name)
        startActivity(intent)
    }
    override fun ReelsClick(reels: Reels) {
        val intent = Intent(applicationContext, ReelsDetailViewActivity::class.java)
        intent.putExtra("reels", reels)
        startActivity(intent)
    }

    override fun PostClick(posts: Posts) {
        val intent = Intent(applicationContext, PostViewDetailActivity::class.java)
        intent.putExtra("post", posts)
        startActivity(intent)
    }

    override fun BackPress() {
        onBackPressed()
    }
}
    interface ReelsPageListener{
        fun BackPress()
    }

    class ReelsPagerAdapter(private val books: List<Reels>, private val context: Context,var reelsPageListener: ReelsPageListener) : PagerAdapter() {
        override fun getCount(): Int {
            return books.size
        }
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(context)
            val binding = ActivityReelsDetailViewBinding.inflate(inflater, container, false)
            val book = books[position]
           binding.icbackpress.visibility=View.GONE
            binding.imgUser.setImageBitmap(BitmapUtils.StringToBitmap(book.REELS_US_IMG))
            binding.txttextname.text=book.REELS_US_NAME
         BitmapUtils.ShowImage(context,book.REELS_URL_IMG,binding.imgreels)
            container.addView(binding.root)
            return binding.root
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }
    }
