package dongdong.duan.chat.activity.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessaging
import dongdong.duan.chat.databinding.ActivityInvitationGoingBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.mode.Users
import dongdong.duan.chat.network.ApiClient
import dongdong.duan.chat.network.ApiService
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.PreferenceManager
import dongdong.duan.chat.utility.UsingPublic
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import java.util.UUID

@Suppress("DEPRECATION")
class InvitationOutGoingActivity : AppCompatActivity() {
    lateinit var preferenceManager: PreferenceManager
    lateinit var binding: ActivityInvitationGoingBinding
    var mettingroom:String?=null
    var inventtoken: String? = null
    lateinit var userrcev: Users
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvitationGoingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        userrcev = intent.getSerializableExtra("user") as Users
        binding.iccancel.setOnClickListener {
            CancelInventionResPonse(userrcev.USER_TOKEN)
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                inventtoken = task.result
            }
        }
        InitMeeting("Call",userrcev.USER_TOKEN)
        LoadInfoUser()
    }
    fun CancelInventionResPonse(recvietoken:String){
        try {
            val tokens = JSONArray()
            tokens.put(recvietoken)
            val body= JSONObject()
            val data= JSONObject()
            data.put(Constant.REMOTE_MSG_TYPE,Constant.REMOTE_MSG_INVITATION)
            data.put(Constant.REMOTE_MSG_INVITATION_RESPONSE,Constant.REMOTE_MSG_INVITATION_CANCELED)
            body.put(Constant.REMOTE_MSG_DATA,data)
            body.put(Constant.REMOTE_MSG_IDS,tokens)
            SendReMoteMessage(body.toString(),Constant.REMOTE_MSG_INVITATION_RESPONSE)
        }
        catch (e:Exception){
            UsingPublic().ShowToast(applicationContext,e.message)
        }
    }

    private fun InitMeeting(meettingtype:String, tokenizers: String) {
        try {
            var tokens= JSONArray()
            tokens.put(tokenizers)
            val body=JSONObject()
            val data=JSONObject()
            data.put(Constant.REMOTE_MSG_TYPE,Constant.REMOTE_MSG_INVITATION)
            data.put(Constant.REMOTE_MSG_MEETING_TYPE,meettingtype)
            data.put(Constant.USER_NAME,preferenceManager.GetString(Constant.USER_NAME))
            data.put(Constant.USER_URLIMG,preferenceManager.GetString(Constant.USER_URLIMG))
            data.put(Constant.REMOTE_MSG_INVITER_TOKEN,inventtoken)

            mettingroom=preferenceManager.GetString(Constant.USER_ID)+"_"+UUID.randomUUID().toString().substring(0,5)
            data.put(Constant.REMOTE_MSG_MEETING_ROOM,mettingroom)

            body.put(Constant.REMOTE_MSG_DATA,data)
            body.put(Constant.REMOTE_MSG_IDS,tokens)
            SendReMoteMessage(body.toString(),Constant.REMOTE_MSG_INVITATION)
        } catch (ex: Exception) {
            UsingPublic().ShowToast(applicationContext, ex.message)
        }
    }

    fun SendReMoteMessage(remotemess: String, type: String) {
        ApiClient().GetClient().create(ApiService::class.java)
            .sendMess(Constant.GetRemoteMSGHeader(), remotemess)
            ?.enqueue(object : Callback<String?> {
                override fun onResponse(call: Call<String?>, response: Response<String?>) {
                    if(response.isSuccessful){
                        if(type.equals(Constant.REMOTE_MSG_INVITATION)){
                            UsingPublic().ShowToast(applicationContext,"Invitation success")

                        }
                        else if(type.equals(Constant.REMOTE_MSG_INVITATION_RESPONSE)){
                         finish()
                        }
                    }

                    else{
                        UsingPublic().ShowToast(applicationContext,response.message())
                        finish()
                    }
                }
                override fun onFailure(call: Call<String?>, t: Throwable) {
                    UsingPublic().ShowToast(this@InvitationOutGoingActivity, t.message)
                    finish()
                }
            })
    }

    val InvitationResponseReceive:BroadcastReceiver=object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val type=intent!!.getStringExtra(Constant.REMOTE_MSG_INVITATION_RESPONSE)
            if(type!=null){
                if (type.equals(Constant.REMOTE_MSG_INVITATION_ACCEPTED)){
                    try {
                        val server = URL("https://meet.jit.si")
                        val defaultOptions = JitsiMeetConferenceOptions.Builder()
                            .setServerURL(server)
                            .setWelcomePageEnabled(false)
                            .setRoom(null)
                            .build()
                        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

                        val options = JitsiMeetConferenceOptions.Builder()
                            .setRoom("123")
                            .build()


                        val intent = Intent(this@InvitationOutGoingActivity, JitsiMeetActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        JitsiMeetActivity.launch(this@InvitationOutGoingActivity, options)
                        finish()

                    } catch (e: java.lang.Exception) {
                        UsingPublic().ShowToast(applicationContext, e.message  )
                    }
                }
                else if (type.equals(Constant.REMOTE_MSG_INVITATION_REJECTED)){
                    UsingPublic().ShowToast(applicationContext,"Reject")
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(applicationContext)
            .registerReceiver(
                InvitationResponseReceive,
                IntentFilter(Constant.REMOTE_MSG_INVITATION_RESPONSE)
            )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(applicationContext)
            .unregisterReceiver(
                InvitationResponseReceive
            )
    }


    private fun LoadInfoUser() {
        binding.imgUsers.setImageBitmap(BitmapUtils.StringToBitmap(userrcev.USER_IMAGE))
        binding.txtusername.text = userrcev.USER_NAME
    }
}