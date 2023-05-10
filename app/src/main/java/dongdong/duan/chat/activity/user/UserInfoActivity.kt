package dongdong.duan.chat.activity.user

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dongdong.duan.chat.R
import dongdong.duan.chat.activity.chat.ImageShow
import dongdong.duan.chat.activity.post.CommentPostActivity
import dongdong.duan.chat.activity.post.PostViewDetailActivity
import dongdong.duan.chat.activity.reels.ReelsDetailViewActivity
import dongdong.duan.chat.adapter.FriendInfoUserAdapter
import dongdong.duan.chat.adapter.PostAdapter
import dongdong.duan.chat.databinding.ActivityUserInfoBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.file.SupportLibary
import dongdong.duan.chat.listener.ListUserEvent
import dongdong.duan.chat.listener.PostListener
import dongdong.duan.chat.mode.Friend
import dongdong.duan.chat.mode.Posts
import dongdong.duan.chat.mode.Reels
import dongdong.duan.chat.mode.Users
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.PreferenceManager
import dongdong.duan.chat.utility.UsingPublic
import java.util.*

@Suppress("DEPRECATION")
class UserInfoActivity : AppCompatActivity(), ListUserEvent, PostListener {
    lateinit var binding: ActivityUserInfoBinding
    lateinit var preferenceManager: PreferenceManager
    lateinit var users: Users
    lateinit var listFiriend: ArrayList<Friend>
    var reldatabase = FirebaseDatabase.getInstance()
    val database = FirebaseFirestore.getInstance()
    var showAnim: Animation? = null
    var hideAnim: Animation? = null
    var storage = FirebaseStorage.getInstance().getReference("ImgUsers")

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        users = intent.getSerializableExtra("user") as Users
        binding.btnaddpost.setOnClickListener {
            startActivity(Intent(applicationContext, AddNewPostActivity::class.java))
        }


        showAnim = AnimationUtils.loadAnimation(this, R.anim.show_animation_center)
        hideAnim = AnimationUtils.loadAnimation(this, R.anim.hide_animation_center)

        LoadInfoUser(users)
        LoadFriend(users.USER_ID) {
            binding.rcvlistfriend.adapter = FriendInfoUserAdapter(it, applicationContext, this)
            binding.txtsumfirend.text = it.count().toString() + " bạn"
        }

        binding.icbackpress.setOnClickListener {
            onBackPressed()
        }


        LoadInfo()
        LoadPost(users) { listpost ->
            binding.rcvpostuser.adapter = PostAdapter(
                listpost,
                1,
                applicationContext,
                this
            )
        }
    }

    var isclick = 0
    var PICK_IMAGE_REQUEST_CODE = 1001
    private fun LoadInfo() {
        binding.icchooseimg.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            this.startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
            isclick = 1
        }
        binding.iconanhbia.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            this.startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
            isclick = 2
        }
        Glide.with(applicationContext)
            .load(users.USER_ANHBIA).into(binding.anhbia)

    }

    private fun GetNotification(ID: String, strput: String): HashMap<String, Any> {
        var hasmap = kotlin.collections.HashMap<String, Any>()
        hasmap[Constant.NOTI_ID_VIEW] = ID
        hasmap[Constant.NOTI_USER_ID] = preferenceManager.GetString(Constant.USER_ID).toString()
        hasmap[Constant.NOTI_USER_NAME] = preferenceManager.GetString(Constant.USER_NAME).toString()
        hasmap[Constant.NOTI_USER_IMG] = preferenceManager.GetString(Constant.USER_IMAGE).toString()
        hasmap[Constant.NOTI_USER_ID] = preferenceManager.GetString(Constant.USER_ID).toString()
        hasmap[Constant.NOTI_DATE] = Date()
        hasmap[Constant.NOTY_TYPE] = "users"
        hasmap[Constant.NOTI_STATUS] =
            preferenceManager.GetString(Constant.USER_NAME).toString() + strput
        return hasmap
    }

    var uriimg = null

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE) {
            var uri = data!!.data
            if (isclick == 1) {
                binding.imgUsers.setImageURI(uri)
                val inputStream =
                    uri?.let { contentResolver.openInputStream(it) }
                val bitmap = BitmapFactory.decodeStream(inputStream)
                var endcodeImg = BitmapUtils.BitmapToString(bitmap)
                PrUpdateImg(isclick, uri, endcodeImg)
            } else if (isclick == 2) {
                BitmapUtils.ShowImage(applicationContext, uri!!, binding.anhbia)
                val inputStream =
                    uri?.let { contentResolver.openInputStream(it) }
                val bitmap = BitmapFactory.decodeStream(inputStream)
                var endcodeImg = BitmapUtils.BitmapToString(bitmap)
                PrUpdateImg(isclick, uri, endcodeImg)
            }
        }
    }

    private fun PrUpdateImg(isclick: Int, uri: Uri?, endcodeImg: String) {
        binding.layoutupdate.visibility = View.VISIBLE
        binding.layoutupdate.startAnimation(showAnim)

        binding.btnconfirmSEW.setOnClickListener {
            var storageref = storage.child(
                System.currentTimeMillis()
                    .toString() + "." + SupportLibary.FileExTension(
                    uri!!,
                    contentResolver
                )
            )
            storageref.putFile(uri).addOnSuccessListener {
                storageref.downloadUrl.addOnSuccessListener { url ->
                    if (isclick == 1) {
                        preferenceManager.PutString(Constant.USER_URLIMG, url.toString())
                        preferenceManager.PutString(Constant.USER_IMAGE, endcodeImg)
                        database.collection(Constant.SYS_USERS)
                            .document(preferenceManager.GetString(Constant.USER_ID).toString())
                            .update(Constant.USER_URLIMG, url.toString()).addOnSuccessListener {
                                database.collection(Constant.SYS_USERS)
                                    .document(
                                        preferenceManager.GetString(Constant.USER_ID).toString()
                                    )
                                    .update(Constant.USER_IMAGE, endcodeImg)
                                    .addOnSuccessListener {
                                        database.collection(Constant.SYS_NOTIFICATION)
                                            .add(GetNotification(users.USER_ID,"đã cập nhật ảnh đại diện của bạn ấy"))
                                        binding.layoutupdate.startAnimation(hideAnim)
                                        binding.layoutupdate.visibility = View.GONE

                                        this.isclick = 0
                                    }
                            }

                    } else if (isclick == 2) {
                        preferenceManager.PutString(Constant.USER_ANHBIA, url.toString())
                        database.collection(Constant.SYS_USERS)
                            .document(preferenceManager.GetString(Constant.USER_ID).toString())
                            .update(Constant.USER_ANHBIA, url.toString()).addOnSuccessListener {
                                BitmapUtils.ShowImage(applicationContext, url, binding.anhbia)
                                binding.layoutupdate.startAnimation(hideAnim)
                                binding.layoutupdate.visibility = View.GONE
                                database.collection(Constant.SYS_NOTIFICATION)
                                    .add(GetNotification(users.USER_ID,"đã cập nhật ảnh bìa của bạn ấy"))
                                this.isclick = 0
                                Glide.with(applicationContext).load(
                                    preferenceManager.GetString(Constant.USER_ANHBIA).toString()
                                ).into(binding.anhbia)
                            }
                    }
                }
            }
        }
    }

    private fun LoadPost(users: Users, postlist: (ArrayList<Posts>) -> Unit) {
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
                                    userLikeSnapshot.child(users.USER_ID)
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
                                if (post.User_ID.equals(users.USER_ID)) {
                                    listpost.add(post)
                                }
                            }
                            postlist(listpost as ArrayList<Posts>)
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

    private fun LoadFriend(userID: String, calback: (ArrayList<Friend>) -> Unit) {
        listFiriend = ArrayList()
        reldatabase.getReference(Constant.SYS_FRIEND)
            .child(userID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (document in snapshot.children) {
                        val user = Friend(
                            userID = document.key.toString(),
                            userName = document.child(Constant.FRIEND_NAME).value.toString(),
                            userurl = document.child(Constant.FRIEND_URLIMG).value.toString(),
                            friendType = document.child(Constant.FRIEND_TYPE).value.toString()
                                .toInt()
                        )
                        listFiriend.add(user)
                    }
                    calback(listFiriend)
                }

                override fun onCancelled(error: DatabaseError) {
                    SupportLibary.ShowToast(applicationContext, error.message)
                }

            })
    }

    private fun LoadInfoUser(users: Users) {
        binding.txtusername.text = users.USER_NAME
        Glide.with(applicationContext).load(users.USER_URLIMG).into(binding.imgUsers)
        if (!users.USER_ID.equals(preferenceManager.GetString(Constant.USER_ID).toString())) {
            binding.btnaddpost.isEnabled = false;
        }
    }

    override fun StatChat(users: Users?) {
        finish()
    }

    override fun AddFriend(users: Users?, isadd: Int) {
        finish()
    }

    override fun RejectFriend(users: Users?, isadd: Int) {
        finish()
    }

    override fun AccetpFriend(users: Users?, isadd: Int) {
        finish()
    }

    override fun ViewInfo(iduser: String) {
        SupportLibary.GetUserByID(iduser) { users ->
            if (users == null) {
                UsingPublic().ShowToast(applicationContext, "Hệ thống đang lỗi thử lại sau")
            } else {
                val intent = Intent(applicationContext, UserInfoActivity::class.java)
                intent.putExtra("user", users)
                startActivity(intent)
            }
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
}