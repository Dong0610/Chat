package dongdong.duan.chat.mode

import com.google.firebase.database.DatabaseReference

class VideoCall(
    val callerId: String,
    val calleeId: String,
    var status: String,
    val roomId: String
):java.io.Serializable