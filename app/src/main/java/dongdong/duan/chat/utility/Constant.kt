package dongdong.duan.chat.utility

import android.annotation.SuppressLint

class Constant {
    companion object {

        var KEY_THEME_APP = "KEY_THEME_APP"

        var KEY_IS_SIGN = "KEY_IS_SIGN"

        var SYS_USERS = "Chat_Users"
        var USER_ID = "User_ID"
        var USER_NAME = "User_Name"
        var USER_EMAIL = "User_Email"
        var USER_PASS = "User_Pass"
        var USER_TOKEN = "User_Token"
        var USER_IMAGE = "User_Image"
        var USER_URLIMG = "User_UrlImg"
        var USER_ANHBIA = "User_AnhBia"
        var USER_TIME = "User_Time"
        var USER_ONLINE = "User_Online"

        var SYS_CHAT = "Chat_Sys"
        var CHAT_CONTENT = "Chat_Content"
        var CHAT_SENDER_ID = "Chat_Sender_Id"
        var CHAT_RECEIVE_ID = "Chat_Receive_ID"
        var CHAT_TIME = "ChatTime"
        var CHAT_IMGURL = "Chat_UrlImg"
        var CHAT_TYPE = "Chat_Type"


        var CONVERSION_SYS = "Conversion_Sys"
        var CHAT_CONVERSION_ID = "ChatCV_ID"
        var CHATCV_SENDER_NAME = "ChatCV_SenderName"
        var CHATCV_RECEIVE_NAME = "ChatCV_Receive_Name"
        var CHATCV_TIME = "ChatCV_Time"
        var CHATCV_SENDER_IMG = "ChatCV_Sender_Img"
        var CHATCV_SENDER_ID = "ChatCV_SenderID"
        var CHATCV_RECEIVE_ID = "ChatCV_Receive_ID"
        var CHATCV_LAST_MESSAGE = "ChatCV_Last_Message"
        var CHATCV_RECEIVE_IMAGE = "ChatCV_Receive_Img"

        var RL_CHAT_LIST = "RlChatMessage"
        var SYS_POST = "Post_System"
        var POST_USERID = "PostUsID"
        var POST_USERNAME = "Post_UsName"
        var POST_USEIMG = "Post_UsImg"
        var POST_MUSIC_URL="Post_Music"
        var POST_STATUS = "Post_Status"
        var POST_URLIMG = "Post_Img"
        var POST_TIME = "Post_Time"
        var POST_SECURYTY = "Post_Secutity"
        var POST_LIKE = "Post_Like"
        var POST_VIEW = "Post_View"
        var POST_COMMENT = "Post_Comment"
        var POST_SOF = "Post_SOF"


        var SYS_COMMENT_POST="Post_Comment_Sys"
        var COMMENT_USERID="Comment_UsID"
        var COMMENT_USENAME="Comment_UsName"
        var COMMENT_USERIMG="Comment_UsImg"
        var COMMENT_CONTENT="Comment_Content"
        var COMMENT_TIME="Comment_Time"

        var SYS_NOTIFICATION="SYS_Notification"
        var NOTI_ID_VIEW="Noti_ID_View"
        var NOTI_USER_ID="Noti_UserID"
        var NOTI_USER_IMG="Noti_UserImg"
        var NOTI_USER_NAME="Noti_UserName"
        var NOTI_STATUS="Noti_Status"
        var NOTI_DATE="Noti_Name"
        val NOTY_TYPE="Noty_Type"



        var SYS_FRIEND="Friend_User_Sys"
        var FRIEND_ID="Friend_Id"
        var FRIEND_NAME="Friend_Name"
        var FRIEND_URLIMG="Friend_UrlImg"
        var FRIEND_TYPE="Friend_Type"

        var SYS_REELS="Reels_Sys"
        var REELS_ID="Reels_ID"
        var REELS_US_ID="Reels_User_Id"
        var REELS_US_NAME="Reels_User_Name"
        var REELS_US_IMG="Reels_User_Img"
        var REELS_TIME="Reels_Time"
        var REELS_URL_IMG="Reels_Url"
        var REELS_URL_MUSIC="Reels_Url_Music"
        var REELS_SECURITY="Reels_Security"


        var REMOTE_MSG_AUTHORIZATION="Authorization"
        var REMOTE_MSG_CONTENT_TYPE= "Content-Type"
         var REMOTE_MSG_TYPE = "Type"
        var REMOTE_MSG_INVITATION="Invitation"
        var REMOTE_MSG_MEETING_TYPE="MeetingType"
        var REMOTE_MSG_INVITER_TOKEN="InvitationToken"
        var REMOTE_MSG_DATA="data"
        var REMOTE_MSG_IDS = "registration_ids"


        var REMOTE_MSG_INVITATION_RESPONSE="InsResponse"
        var REMOTE_MSG_INVITATION_ACCEPTED="InsAccecptd"
        var REMOTE_MSG_INVITATION_REJECTED="InsRejected"
        var REMOTE_MSG_INVITATION_CANCELED="InsCanceled"

        var REMOTE_MSG_MEETING_ROOM="MeetingRoom"
        @SuppressLint("SuspiciousIndentation")
        fun GetRemoteMSGHeader(): HashMap<String, String>{
            val header=HashMap<String,String>()
                header.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAACMdQ6e0:APA91bG5XuwfZ1rEwRNA3Sc1kmrckJU-ChRw8olIOfp1YWmidkKy4HteYxlzKFGNXVJisxp3vyF4MrfVhAIMRAUPu9uDGGpYLweYK6bj03EzZznSXr7LG9T_ZW5x5e2KIlU7ZwKtOMii"
                )
                header.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
                )
            return header
        }
    }
}


















