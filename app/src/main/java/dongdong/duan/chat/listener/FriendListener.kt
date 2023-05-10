package dongdong.duan.chat.listener

import dongdong.duan.chat.mode.Friend
import dongdong.duan.chat.mode.Users

interface FriendListener {
    fun RemoveFriend(friend:Friend)
    fun StartChat(friend:Friend)
    fun ViewInfo(friend: Friend)
}