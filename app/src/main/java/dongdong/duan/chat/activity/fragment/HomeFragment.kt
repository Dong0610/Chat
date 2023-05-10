package dongdong.duan.chat.activity.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
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
import dongdong.duan.chat.adapter.ReelsAdapter
import dongdong.duan.chat.databinding.FragmentHomeBinding
import dongdong.duan.chat.file.SupportLibary
import dongdong.duan.chat.listener.PostListener
import dongdong.duan.chat.mode.Posts
import dongdong.duan.chat.mode.Reels
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.PreferenceManager
import dongdong.duan.chat.utility.UsingPublic
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), PostListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var preferenceManager: PreferenceManager
    var database = FirebaseFirestore.getInstance()
    lateinit var listpost: ArrayList<Posts>
    var themetype = 0
    var reldatabase = FirebaseDatabase.getInstance()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager(context)
        themetype = preferenceManager.GetString(Constant.KEY_THEME_APP)!!.toInt()
        LoadGiaoDien(themetype)
        listpost = ArrayList()
        LoadPostView { listpostrt ->
            listpost = listpostrt
            sortPosts(listpost)
            binding.rcvpostview.adapter =
                PostAdapter(listposts = listpost, 1, this.requireContext(), this@HomeFragment)
        }
        LoadReels { litreels->
            if(litreels.count()==0){
                litreels.add(
                    Reels("Null",preferenceManager.GetString(Constant.USER_ID).toString(),preferenceManager.GetString(Constant.USER_NAME).toString()
                        ,preferenceManager.GetString(Constant.USER_IMAGE).toString(),Date().toString(),
                    "https://www.kindpng.com/picc/m/24-248253_user-profile-default-image-png-clipart-png-download.png","Null","Null")
                )
            }
            binding.reelsview.adapter=ReelsAdapter(listreels = litreels,this.requireContext(),this@HomeFragment)
        }

        binding.iconsreach.setOnClickListener {
            FindPost()
        }

        return binding.root
    }
    fun sortPosts(posts: List<Posts>): List<Posts> {
        val sortedPosts = posts.sortedByDescending { it.Time_Put }
        sortedPosts.sortedByDescending { it.Like + it.Comment + it.View }
        val visiblePosts = sortedPosts.filter { it.Security == 0 }
        return visiblePosts
    }



    private fun FindPost() {
        var stringsr= binding.edtsreach.text.toString()
        if(!stringsr.equals("")){
            val listPosts=kotlin.collections.ArrayList<Posts>()
            listpost.forEach { posts ->
                if(posts.User_Name.lowercase(Locale.getDefault())
                        .contains(stringsr.lowercase(Locale.getDefault())) || posts.Status.lowercase(
                        Locale.getDefault()
                    )
                        .contains(stringsr.lowercase(Locale.getDefault()))){
                    listPosts.add(posts)
                }
            }
            sortPosts(listPosts)
            binding.rcvpostview.adapter =
                PostAdapter(listposts = listPosts, 1, this.requireContext(), this@HomeFragment)
        }
        binding.edtsreach.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(count==0){
                    LoadPostView { listpostrt ->
                        listpost = listpostrt
                        sortPosts(listpost)
                        binding.rcvpostview.adapter =
                            PostAdapter(listposts = listpost, 1, this@HomeFragment.requireContext(), this@HomeFragment)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun LoadReels(listreels: (ArrayList<Reels>) -> Unit) {
        val listreel=ArrayList<Reels>()
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
                            val reels=Reels(REELS_ID =
                                document.id,
                                REELS_US_ID = document.getString(Constant.REELS_US_ID).toString(),
                                REELS_US_NAME = document.getString(Constant.REELS_US_NAME).toString(),
                                REELS_US_IMG = document.getString(Constant.REELS_US_IMG).toString(),
                                REELS_TIME = SupportLibary.GetRealDateTime(document.getDate(Constant.REELS_TIME) as Date)!!,
                                REELS_URL_IMG = document.getString(Constant.REELS_URL_IMG).toString(),
                                REELS_URL_MUSIC = document.getString(Constant.REELS_URL_MUSIC).toString(),
                                REELS_SECURITY = document.getString(Constant.REELS_SECURITY).toString()
                            )
                            if(!isHourGreaterThan24(reels.REELS_TIME)){
                                listreel.add(reels)
                            }
                       }
                    }
                    listreels(listreel)
                } else {
                    SupportLibary.ShowDialog(
                        task.exception?.message.toString(),
                        3,
                        this@HomeFragment
                    )
                }
            }
        }.addOnFailureListener { ex ->
            SupportLibary.ShowDialog(ex.message.toString(), 3, this@HomeFragment)
        }
    }
    fun isHourGreaterThan24(dateString: String): Boolean {
        val format = SimpleDateFormat("MM-dd-yyyy hh:mm:ss", Locale.getDefault())
        val date = format.parse(dateString)
        val cal = Calendar.getInstance()
        cal.time = date
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        return hour > 23
    }

    private fun LoadPostView(listpostrt: (ArrayList<Posts>) -> Unit) {
        binding.progressload.visibility = View.VISIBLE
        val currentUserID = preferenceManager.GetString(Constant.USER_ID)
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
                                    userLikeSnapshot.child(currentUserID!!)
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
                                listpost.add(post)
                            }
                            binding.progressload.visibility = View.GONE
                            listpostrt(listpost as ArrayList<Posts>)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                requireContext(),
                                "Error: ${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                } else {
                    binding.progressload.visibility = View.GONE
                    binding.rcvpostview.adapter = PostAdapter(
                        listpost,
                        1,
                        requireContext(),
                        this@HomeFragment
                    )
                }
            }.addOnFailureListener { exception ->
                binding.progressload.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Error: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun LoadGiaoDien(themetype: Int) {
        if (themetype == 1) {
            binding.edtsreach.background =
                context?.getDrawable(R.drawable.input_bg_edt_lighr)
                binding.edtsreach.textCursorDrawable=context?.getDrawable(R.drawable.cusor_color_bg_light)
            binding.edtsreach.setTextColor(Color.BLACK)
            binding.iconsreach.setImageResource(R.drawable.icon_sreach_light)
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
        val intent = Intent(requireContext(), CommentPostActivity::class.java)
        intent.putExtra("postID", PosIS)
        intent.putExtra("islike", islike.toString())
        startActivity(intent)
    }

    override fun PlayMusic(urlms: String, play: Int) {

    }
    override fun ShowImage(urlimg: String, name: String) {
        val intent = Intent(requireContext(), ImageShow::class.java)
        intent.putExtra("urlimg", urlimg)
        intent.putExtra("name", name)
        startActivity(intent)
    }
    override fun ReelsClick(reels: Reels) {
        val intent = Intent(requireContext(), ReelsDetailViewActivity::class.java)
        intent.putExtra("reels", reels)
        startActivity(intent)
    }

    override fun PostClick(posts: Posts) {
        val intent = Intent(requireContext(), PostViewDetailActivity::class.java)
        intent.putExtra("post", posts)
        startActivity(intent)
    }
}