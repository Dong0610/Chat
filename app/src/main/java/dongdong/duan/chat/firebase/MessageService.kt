package dongdong.duan.chat.firebase

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dongdong.duan.chat.activity.chat.InvitationCommingActivity
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.UsingPublic

class MessageService : FirebaseMessagingService() {
    @SuppressLint("LogNotTimber")
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("token", "TokenID is: ${token.toString()}")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val type = remoteMessage.data.get(Constant.REMOTE_MSG_TYPE)
        if (type != null) {
            if (type.toString().equals(Constant.REMOTE_MSG_INVITATION)) {
                val intent = Intent(applicationContext, InvitationCommingActivity::class.java)
                intent.putExtra(
                    Constant.REMOTE_MSG_MEETING_TYPE,
                    remoteMessage.data.get(Constant.REMOTE_MSG_MEETING_TYPE)
                )
                intent.putExtra(Constant.USER_NAME, remoteMessage.data.get(Constant.USER_NAME))
                intent.putExtra(Constant.USER_URLIMG, remoteMessage.data.get(Constant.USER_URLIMG))
                intent.putExtra(
                    Constant.REMOTE_MSG_MEETING_ROOM,
                    remoteMessage.data.get(Constant.REMOTE_MSG_MEETING_ROOM).toString()
                )
                intent.putExtra(
                    Constant.REMOTE_MSG_INVITER_TOKEN,
                    remoteMessage.data.get(Constant.REMOTE_MSG_INVITER_TOKEN)
                )
                intent.putExtra(
                    Constant.REMOTE_MSG_INVITER_TOKEN,
                    remoteMessage.data.get(Constant.REMOTE_MSG_INVITER_TOKEN)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            } else if(type.equals(Constant.REMOTE_MSG_INVITATION_RESPONSE)) {
                val intent=Intent(Constant.REMOTE_MSG_INVITATION_RESPONSE)

                intent.putExtra(Constant.REMOTE_MSG_INVITATION_RESPONSE,
                remoteMessage.data.get(Constant.REMOTE_MSG_INVITATION_RESPONSE))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        }
        else{
            UsingPublic().ShowToast(applicationContext,"Type is null");
        }
    }
}