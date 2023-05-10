package dongdong.duan.chat.activity.user

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dongdong.duan.chat.activity.chat.ChatActivity
import dongdong.duan.chat.adapter.FriendAdapter
import dongdong.duan.chat.databinding.ActivityFriendBinding
import dongdong.duan.chat.file.GetIdConversion
import dongdong.duan.chat.file.SupportLibary
import dongdong.duan.chat.listener.FriendListener
import dongdong.duan.chat.mode.Friend
import dongdong.duan.chat.mode.Users
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.CustomDialog
import dongdong.duan.chat.utility.PreferenceManager
import dongdong.duan.chat.utility.UsingPublic
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FriendActivity : AppCompatActivity(),FriendListener {
    lateinit var binding: ActivityFriendBinding
    var reldatabase = FirebaseDatabase.getInstance()
    lateinit var preferenceManager: PreferenceManager
    lateinit var listFiriend: ArrayList<Friend>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        LoadFriend{
            listFiriend->
            binding.rclistusersview.adapter= FriendAdapter(listFiriend,applicationContext,this)
        }

        binding.icclosed.setOnClickListener {
            onBackPressed()
        }
    }

    private fun LoadFriend(calback:(ArrayList<Friend>)->Unit) {
        listFiriend=ArrayList()
        reldatabase.getReference(Constant.SYS_FRIEND)
            .child(preferenceManager.GetString(Constant.USER_ID).toString())
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
                    SupportLibary.ShowToast(applicationContext,error.message)
                }

            })
    }

    override fun RemoveFriend(friend: Friend) {
        CustomDialog(this@FriendActivity).ConfirmDialog("Bạn có muốn hủy kết bạn không?"){
            reldatabase.getReference(Constant.SYS_FRIEND)
                .child(preferenceManager.GetString(Constant.USER_ID).toString())
                .child(friend.userID)
                .removeValue().addOnCompleteListener {
                    LoadFriend{
                            listFiriend->
                        binding.rclistusersview.adapter= FriendAdapter(listFiriend,applicationContext,this)
                    }
                    reldatabase.getReference(Constant.SYS_FRIEND)
                        .child(friend.userID)
                        .child(preferenceManager.GetString(Constant.USER_ID).toString())
                        .removeValue().addOnCompleteListener {
                            SupportLibary.ShowToast(applicationContext,"Đã hủy kết bạn")
                        }.addOnFailureListener {
                            SupportLibary.ShowToast(applicationContext,it.message)
                        }
                }.addOnFailureListener {
                    SupportLibary.ShowToast(applicationContext,it.message)
                }
        }
    }

    override fun StartChat(friend: Friend) {

        SupportLibary.GetUserByID(friend.userID){
                users ->
            if (users==null){
                UsingPublic().ShowToast(applicationContext,"Hệ thống đang lỗi thử lại sau")
            }
            else{
                val senderId = preferenceManager.GetString(Constant.USER_ID).toString()
                val conversion = GetConVer(users!!)
                GetIdConversion.CheckConversation(users.USER_ID, conversion, senderId, reldatabase) { idrm ->
                    val intent = Intent(applicationContext, ChatActivity::class.java)
                    intent.putExtra("user", users)
                    intent.putExtra("idcv", idrm)
                    startActivity(intent)
                }
            }
        }

    }

    override fun ViewInfo(friend: Friend) {
        SupportLibary.GetUserByID(friend.userID){
                users ->
            if (users==null){
                UsingPublic().ShowToast(applicationContext,"Hệ thống đang lỗi thử lại sau")
            }
            else{
                val intent = Intent(applicationContext, UserInfoActivity::class.java)
                intent.putExtra("user", users)
                startActivity(intent)
            }
        }
    }

    fun GetConVer(users: Users): HashMap<String, Any> {
            val coversion = java.util.HashMap<String, Any>()
            coversion[Constant.CHATCV_SENDER_ID] =
                preferenceManager.GetString(Constant.USER_ID).toString()
            coversion[Constant.CHATCV_SENDER_NAME] =
                preferenceManager.GetString(Constant.USER_NAME).toString()
            coversion[Constant.CHATCV_RECEIVE_ID] = users.USER_ID
            coversion[Constant.CHATCV_RECEIVE_NAME] = users.USER_NAME
            coversion[Constant.CHATCV_RECEIVE_IMAGE] = users.USER_IMAGE
            coversion[Constant.CHATCV_LAST_MESSAGE] = ""
            coversion[Constant.CHATCV_TIME] = Date()
            return coversion
        }
}