package dongdong.duan.chat.activity.chat

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dongdong.duan.chat.databinding.ActivityComingInvitationBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.network.ApiClient
import dongdong.duan.chat.network.ApiService
import dongdong.duan.chat.utility.Constant
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

class InvitationCommingActivity : AppCompatActivity() {
    lateinit var binding: ActivityComingInvitationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComingInvitationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.icaccepted.setOnClickListener {
            SendInventionResPonse(
                Constant.REMOTE_MSG_INVITATION_ACCEPTED,
                intent.getStringExtra(Constant.REMOTE_MSG_INVITER_TOKEN).toString()
            )
        }
        binding.icclosed.setOnClickListener {
            SendInventionResPonse(
                Constant.REMOTE_MSG_INVITATION_REJECTED,
                intent.getStringExtra(Constant.REMOTE_MSG_INVITER_TOKEN).toString()
            )
        }

        LoadInfoUser()
    }

    private val InvitationResponseReceive: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val type = intent!!.getStringExtra(Constant.REMOTE_MSG_INVITATION_RESPONSE)
            if (type != null) {
                if (type.equals(Constant.REMOTE_MSG_INVITATION_CANCELED)) {
                    finish()
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

    fun SendInventionResPonse(type: String, recvietoken: String) {
        try {
            var tokens = JSONArray()
            tokens.put(recvietoken)
            val body = JSONObject()
            val data = JSONObject()
            data.put(Constant.REMOTE_MSG_TYPE, Constant.REMOTE_MSG_INVITATION_RESPONSE)
            data.put(Constant.REMOTE_MSG_INVITATION_RESPONSE, type)
            body.put(Constant.REMOTE_MSG_DATA, data)
            body.put(Constant.REMOTE_MSG_IDS, tokens)
            SendReMoteMessage(body.toString(), type)
        } catch (e: Exception) {
            UsingPublic().ShowToast(applicationContext, e.message)
        }
    }

    fun SendReMoteMessage(remotemess: String, type: String) {
        ApiClient().GetClient().create(ApiService::class.java)
            .sendMess(Constant.GetRemoteMSGHeader(), remotemess)
            ?.enqueue(object : Callback<String?> {
                @SuppressLint("SuspiciousIndentation")
                override fun onResponse(call: Call<String?>, response: Response<String?>) {
                    if (response.isSuccessful) {
                        if (type.equals(Constant.REMOTE_MSG_INVITATION_ACCEPTED)) {
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

                                val intent = Intent(this@InvitationCommingActivity, JitsiMeetActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                JitsiMeetActivity.launch(this@InvitationCommingActivity, options)
                                finish()

                            } catch (e: java.lang.Exception) {
                                UsingPublic().ShowToast(applicationContext, e.message )
                            }
                        } else {
                            UsingPublic().ShowToast(applicationContext,"Reject")
                            finish()
                        }
                    } else {
                        UsingPublic().ShowToast(this@InvitationCommingActivity, response.message())
                        finish()
                    }
                }

                override fun onFailure(call: Call<String?>, t: Throwable) {
                    UsingPublic().ShowToast(this@InvitationCommingActivity, t.message)
                    finish()
                }

            })

    }

    private fun LoadInfoUser() {
        BitmapUtils.ShowImage(
            applicationContext,
            intent.getStringExtra(Constant.USER_URLIMG).toString(),
            binding.imgUsers
        )
        binding.txtusername.text = intent.getStringExtra(Constant.USER_NAME).toString()
    }
}