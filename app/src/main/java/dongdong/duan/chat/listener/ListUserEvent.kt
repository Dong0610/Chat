package dongdong.duan.chat.listener

import dongdong.duan.chat.mode.Friend
import dongdong.duan.chat.mode.Users

interface ListUserEvent {
    fun StatChat(users: Users?)
    fun AddFriend(users: Users?,isadd:Int)
    fun RejectFriend(users: Users?,isadd:Int)
    fun AccetpFriend(users: Users?,isadd:Int)
    fun ViewInfo(iduser:String)
}