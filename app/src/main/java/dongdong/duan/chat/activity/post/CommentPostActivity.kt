package dongdong.duan.chat.activity.post

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import dongdong.duan.chat.R
import dongdong.duan.chat.adapter.CommentAdapter
import dongdong.duan.chat.databinding.ActivityCommentPostBinding
import dongdong.duan.chat.listener.CommentListener
import dongdong.duan.chat.mode.Comment
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.PreferenceManager
import dongdong.duan.chat.utility.UsingPublic
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("DEPRECATION")
class CommentPostActivity : AppCompatActivity(),CommentListener {
    lateinit var binding:ActivityCommentPostBinding
    lateinit var preferenceManager: PreferenceManager
    lateinit var postID:String
    val database=FirebaseFirestore.getInstance()
    lateinit var listcomment:ArrayList<Comment>
    var reldatabase=FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCommentPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager= PreferenceManager(applicationContext)
        postID=intent.getStringExtra("postID").toString()
        ListenerComment()
        binding.icsendmess.setOnClickListener {
            CommentToPost()
        }
        binding.icbackpress.setOnClickListener {
            onBackPressed()
        }

        var islike=intent.getStringExtra("islike")
        UsingPublic().ShowToast(applicationContext,islike)
        if(islike=="1"){
            binding.iconlike.setImageResource(R.drawable.icon_likeok)
        }
        else{
            binding.iconlike.setImageResource(R.drawable.icon_none_like)
        }
    }
    var COMMENT_USERID="Comment_UsID"
    var COMMENT_USENAME="Comment_UsName"
    var COMMENT_USERIMG="Comment_UsImg"
    var COMMENT_CONTENT="Comment_Content"
    var COMMENT_TIME="Comment_Time"
    private fun CommentToPost() {
        var comm= HashMap<String,Any>()
        comm[COMMENT_USERID]=preferenceManager.GetString(Constant.USER_ID).toString()
        comm[COMMENT_USENAME]=preferenceManager.GetString(Constant.USER_NAME).toString()
        comm[COMMENT_USERIMG]=preferenceManager.GetString(Constant.USER_IMAGE).toString()
        comm[COMMENT_CONTENT]=binding.edtinputmess.text.toString()
        comm[COMMENT_TIME]=Date()
        binding.edtinputmess.setText("")
        reldatabase.getReference(Constant.SYS_COMMENT_POST).child(postID)
            .push().setValue(comm)
        database.collection(Constant.SYS_POST).document(postID)
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val view= it.result.getLong(Constant.POST_VIEW)
                    database.collection(Constant.SYS_POST).document(postID)
                        .update(Constant.POST_COMMENT,view!!+1)
                    reldatabase.getReference(Constant.SYS_POST)
                        .child(preferenceManager.GetString(Constant.USER_ID).toString())
                        .child(postID)
                        .child(Constant.POST_COMMENT)
                        .setValue(view+1)
                }
            }
    }

    private fun ListenerComment() {

        val database =
            FirebaseDatabase.getInstance().getReference(Constant.SYS_COMMENT_POST+"/"+postID)
        database.addValueEventListener(valueEventListener)
    }
    val valueEventListener = object : ValueEventListener {
        @SuppressLint("NotifyDataSetChanged")
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            listcomment= ArrayList()
            for (cmsn in dataSnapshot.children) {
                val  comment= Comment().apply {
                    Post_ID = cmsn.key.toString()
                    Post_UsId = cmsn.child(Constant.COMMENT_USERID).value.toString()
                    PostUS_Name = cmsn.child(Constant.COMMENT_USENAME).value.toString()
                    Post_UsImg = cmsn.child(Constant.COMMENT_USERIMG).value.toString()
                    Post_Content = cmsn.child(Constant.COMMENT_CONTENT).value.toString()
                    Post_Time = cmsn.child(Constant.COMMENT_TIME).child("hours").value.toString()
                }
                listcomment.add(comment)
            }
            binding.rcvcomment.adapter=CommentAdapter(listcomment,this@CommentPostActivity)
        }

        override fun onCancelled(error: DatabaseError) {
            UsingPublic().ShowToast(applicationContext, error.message)
        }
    }
    override fun UserViewClick(idUser: String) {

    }
}