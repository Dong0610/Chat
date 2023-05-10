package dongdong.duan.chat.activity.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import dongdong.duan.chat.R
import dongdong.duan.chat.activity.chat.ChatActivity
import dongdong.duan.chat.activity.user.UserInfoActivity
import dongdong.duan.chat.adapter.PostAdapter
import dongdong.duan.chat.adapter.UserAdapter
import dongdong.duan.chat.databinding.FragmentUsersBinding
import dongdong.duan.chat.file.GetIdConversion
import dongdong.duan.chat.file.SupportLibary
import dongdong.duan.chat.listener.ListUserEvent
import dongdong.duan.chat.mode.Friend
import dongdong.duan.chat.mode.Posts
import dongdong.duan.chat.mode.Users
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.CustomDialog
import dongdong.duan.chat.utility.PreferenceManager
import dongdong.duan.chat.utility.UsingPublic
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UsersFragment : Fragment(), ListUserEvent {
    lateinit var binding: FragmentUsersBinding
    lateinit var preferenceManager: PreferenceManager
    var theme: Int = 0
    lateinit var userlistall:java.util.ArrayList<Users>
    val database = FirebaseFirestore.getInstance()
    var reference = FirebaseDatabase.getInstance()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsersBinding.inflate(layoutInflater)

        preferenceManager = PreferenceManager(this.context)
        theme = SupportLibary.GetThemeType(this.context);
        LoadGiaoDien(theme)
//        GetListUser { users: List<Users> ->
//
//            binding.rclistusersview.adapter=UserAdapter(theme,users,this.context)
//            binding.progressload.visibility=View.GONE
//        }
        GetListUser{
            userlistall= ArrayList()
            userlistall=it
            binding.rclistusersview.adapter =
                UserAdapter(theme, it, this.context,preferenceManager.GetString(Constant.USER_ID).toString(), listener = this)
            binding.progressload.visibility = View.GONE
        }
        binding.iconsreach.setOnClickListener {
            Find()
        }
        return binding.root
    }

    private fun Find (){
        var stringsr= binding.txtsreach.text.toString()
        if(!stringsr.equals("")){
            val listuser=kotlin.collections.ArrayList<Users>()
           userlistall .forEach { posts ->
                if(posts.USER_NAME.lowercase(Locale.getDefault())
                        .contains(stringsr.lowercase(Locale.getDefault())) ){
                    listuser.add(posts)
                }
            }
            binding.rclistusersview.adapter =
                UserAdapter(theme, listuser, this.context,preferenceManager.GetString(Constant.USER_ID).toString(), listener = this)
        }
        binding.txtsreach.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(count==0){
                    binding.rclistusersview.adapter =
                        UserAdapter(theme, userlistall, this@UsersFragment.requireContext(),preferenceManager.GetString(Constant.USER_ID).toString(), listener = this@UsersFragment)
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
    private fun GetListUser(users:(kotlin.collections.ArrayList<Users>)->Unit) {
        binding.progressload.visibility = View.VISIBLE
        val userlist: ArrayList<Users> = ArrayList()
        val usid = preferenceManager.GetString(Constant.USER_ID)
        database.collection(Constant.SYS_USERS).get().addOnSuccessListener { snapshot ->
            val userIds = snapshot.documents.map { it.id }
            val query = reference.getReference(Constant.SYS_FRIEND)
                .child(preferenceManager.GetString(Constant.USER_ID).toString())
                .orderByKey()
                .startAt(userIds.first())
                .endAt(userIds.last())
            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val friendTypeMap = mutableMapOf<String, Long?>()
                    for (document in task.result.children) {
                        friendTypeMap[document.key.toString()] = document.child(Constant.FRIEND_TYPE).value as Long?
                    }
                    for (document in snapshot.documents) {
                        val user = Users()
                        user.USER_ID = document.id
                        user.USER_EMAIL = document.getString(Constant.USER_EMAIL).toString()
                        user.USER_NAME = document.getString(Constant.USER_NAME).toString()
                        user.USER_PASS = document.getString(Constant.USER_PASS).toString()
                        user.USER_TOKEN=document.getString(Constant.USER_TOKEN).toString()
                        user.USER_IMAGE = document.getString(Constant.USER_IMAGE).toString()
                        user.USER_URLIMG = document.getString(Constant.USER_URLIMG).toString()
                        val friendType = friendTypeMap[document.id]
                        user.IS_FRIEND = friendType?.toInt() ?: 0
                        if (user.USER_ID != usid) {
                            userlist.add(user)
                        }
                    }
                    users(userlist)
                } else {
                    SupportLibary.ShowDialog(task.exception?.message.toString(), 3, this@UsersFragment)
                }
            }
        }.addOnFailureListener { ex ->
            SupportLibary.ShowDialog(ex.message.toString(), 3, this@UsersFragment)
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

    override fun StatChat(users: Users?) {
        val senderId = preferenceManager.GetString(Constant.USER_ID).toString()
        val conversion = GetConVer(users!!)
        GetIdConversion.CheckConversation(users.USER_ID, conversion, senderId, reference) { idrm ->
            val intent = Intent(this.requireContext(), ChatActivity::class.java)
            intent.putExtra("user", users)
            intent.putExtra("idcv", idrm)
            startActivity(intent)
        }
    }

    private  fun SendAddFriend(users: Users?){
        val userchild=kotlin.collections.HashMap<String,Any>()
        userchild[Constant.FRIEND_ID]=users!!.USER_ID
        userchild[Constant.FRIEND_NAME]= users.USER_NAME
        userchild[Constant.FRIEND_URLIMG]= users.USER_URLIMG
        userchild[Constant.FRIEND_TYPE]=2
        reference.getReference(Constant.SYS_FRIEND)
            .child(preferenceManager.GetString(Constant.USER_ID).toString())
            .child(users.USER_ID)
            .setValue(userchild)
        val usersend=kotlin.collections.HashMap<String,Any>()
        userchild[Constant.FRIEND_ID]=preferenceManager.GetString(Constant.USER_ID).toString()
        userchild[Constant.FRIEND_NAME]=preferenceManager.GetString(Constant.USER_NAME).toString()
        userchild[Constant.FRIEND_URLIMG]=preferenceManager.GetString(Constant.USER_URLIMG).toString()
        userchild[Constant.FRIEND_TYPE]=2
        reference.getReference(Constant.SYS_FRIEND)
            .child(users.USER_ID)
            .child(preferenceManager.GetString(Constant.USER_ID).toString())
            .setValue(usersend)
    }
    override fun AddFriend(users: Users?,isadd:Int) {
        if(isadd==0){
            SendAddFriend(users)
            GetListUser{
                binding.rclistusersview.adapter =
                    UserAdapter(theme, it, this.context,preferenceManager.GetString(Constant.USER_ID).toString(), listener = this)
                binding.progressload.visibility = View.GONE
            }
        }
    }

    override fun RejectFriend(users: Users?, isadd: Int) {

    }

    override fun AccetpFriend(users: Users?, isadd: Int) {

    }

    override fun ViewInfo(usersid:String) {
        SupportLibary.GetUserByID(usersid){
                users ->
            if (users==null){
                UsingPublic().ShowToast(requireContext(),"Hệ thống đang lỗi thử lại sau")
            }
            else{
                val intent = Intent(requireContext(), UserInfoActivity::class.java)
                intent.putExtra("user", users)
                startActivity(intent)
            }
        }
    }
}