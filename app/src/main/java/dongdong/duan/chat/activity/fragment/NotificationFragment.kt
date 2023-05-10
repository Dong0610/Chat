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
import dongdong.duan.chat.activity.post.PostViewDetailActivity
import dongdong.duan.chat.activity.reels.ReelsDetailViewActivity
import dongdong.duan.chat.adapter.NotificationAdapter
import dongdong.duan.chat.databinding.FragmentNotificationBinding
import dongdong.duan.chat.file.SupportLibary
import dongdong.duan.chat.listener.NotifyListener
import dongdong.duan.chat.mode.Notification
import dongdong.duan.chat.mode.Posts
import dongdong.duan.chat.mode.Reels
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.PreferenceManager
import java.util.*

class NotificationFragment : Fragment(), NotifyListener {
    lateinit var binding: FragmentNotificationBinding
    val database = FirebaseFirestore.getInstance()
    val reldatabase = FirebaseDatabase.getInstance()
    lateinit var notificationarr: ArrayList<Notification>
    lateinit var preferenceManager: PreferenceManager
    var theme = 0

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager(requireContext())
        notificationarr = ArrayList()
        LoadNotification {
            notificationarr = it
            binding.rclistusersview.adapter = NotificationAdapter(it, requireContext(), this, theme)
        }
        theme = SupportLibary.GetThemeType(requireContext())
        LoadGiaoDien(theme)
        binding.iconsreach.setOnClickListener {
            Find()
        }

        return binding.root
    }

    private fun Find() {
        var stringsr = binding.txtsreach.text.toString()
        if (!stringsr.equals("")) {
            val listuser = kotlin.collections.ArrayList<Notification>()
            notificationarr.forEach { posts ->
                if (posts.NOTI_STATUS.lowercase(Locale.getDefault())
                        .contains(stringsr.lowercase(Locale.getDefault()))
                ) {
                    listuser.add(posts)
                }
            }
            binding.rclistusersview.adapter =
                NotificationAdapter(listuser, requireContext(), this, theme)
        }
        binding.txtsreach.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 0) {
                    binding.rclistusersview.adapter = NotificationAdapter(
                        notificationarr,
                        requireContext(),
                        this@NotificationFragment,
                        theme
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun LoadGiaoDien(themetype: Int) {

        if (themetype == 1) {
            binding.txtsreach.background = context?.getDrawable(R.drawable.input_bg_edt_lighr)
            binding.txtsreach.textCursorDrawable =
                context?.getDrawable(R.drawable.cusor_color_bg_light)
            binding.txtsreach.setTextColor(Color.BLACK)
            binding.iconsreach.setImageResource(R.drawable.icon_sreach_light)
        }
    }

    fun LoadNotification(calback: (ArrayList<Notification>) -> Unit) {
        var listnotify = kotlin.collections.ArrayList<Notification>()
        database.collection(Constant.SYS_NOTIFICATION)
            .get().addOnSuccessListener { task ->
                if (!task.isEmpty) {
                    val postsIds = task.documents.mapNotNull { it.id }
                    val likesRef = reldatabase.getReference(Constant.SYS_FRIEND)
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
                                val notify = Notification(
                                    NOTI_ID = postId,
                                    NOTI_ID_VIEW = document.getString(Constant.NOTI_ID_VIEW)
                                        .toString(),
                                    NOTI_USER_ID = document.getString(Constant.NOTI_USER_ID)
                                        .toString(),
                                    NOTI_USER_NAME = document.getString(Constant.NOTI_USER_NAME)
                                        .toString(),
                                    NOTI_USER_IMG = document.getString(Constant.NOTI_USER_IMG)
                                        .toString(),
                                    NOTI_STATUS = document.getString(Constant.NOTI_STATUS)
                                        .toString(),
                                    NOTI_DATE = SupportLibary.GetRealDateTime(
                                        document.getDate(
                                            Constant.NOTI_DATE
                                        ) as Date
                                    )!!,
                                    NOTY_TYPE = document.getString(Constant.NOTY_TYPE).toString()
                                )
                                var isfriend = likesMap[postId] ?: 0
                                if (isfriend != 1) {
                                    listnotify.add(notify)
                                }

                            }
                            Collections.sort(listnotify) { obj1: Notification, obj2: Notification ->
                                obj1.NOTI_DATE.compareTo(
                                    obj2.NOTI_DATE
                                )
                            }
                            calback(listnotify)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                requireContext(),
                                "Error: ${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Error: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun LoadPostView(idpost: String, postrt: (Posts) -> Unit) {
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
                                val postval = Posts(
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
                                if (postId.equals(idpost)) {
                                    postrt(postval)
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                requireContext(),
                                "Error: ${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Error: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun LoadReels(idreels:String,listreels: (Reels) -> Unit) {
        val listreel=ArrayList<Reels>()
        database.collection(Constant.SYS_REELS).get().addOnSuccessListener { snapshot ->
            val userIds = snapshot.documents.map { it.id }
                    for (document in snapshot.documents) {
                            val reels= Reels(
                                REELS_ID =document.id,
                                REELS_US_ID = document.getString(Constant.REELS_US_ID).toString(),
                                REELS_US_NAME = document.getString(Constant.REELS_US_NAME).toString(),
                                REELS_US_IMG = document.getString(Constant.REELS_US_IMG).toString(),
                                REELS_TIME = SupportLibary.GetRealDateTime(document.getDate(Constant.REELS_TIME) as Date)!!,
                                REELS_URL_IMG = document.getString(Constant.REELS_URL_IMG).toString(),
                                REELS_URL_MUSIC = document.getString(Constant.REELS_URL_MUSIC).toString(),
                                REELS_SECURITY = document.getString(Constant.REELS_SECURITY).toString()
                            )
                        if(reels.REELS_ID.equals(idreels)){
                            listreels(reels)
                            break
                        }
            }
        }.addOnFailureListener { ex ->
            SupportLibary.ShowDialog(ex.message.toString(), 3, this@NotificationFragment)
        }
    }

    override fun StartNotiify(notify: Notification) {
        if (notify.NOTY_TYPE.equals("post")) {
            LoadPostView(notify.NOTI_ID_VIEW) {
                val intent = Intent(requireContext(), PostViewDetailActivity::class.java)
                intent.putExtra("post", it)
                startActivity(intent)
            }
        }
        else if(notify.NOTY_TYPE.equals("reels")){
            LoadReels(notify.NOTI_ID_VIEW) {
                val intent = Intent(requireContext(), ReelsDetailViewActivity::class.java)
                intent.putExtra("reels", it)
                startActivity(intent)
            }
        }

    }
}