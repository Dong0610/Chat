package dongdong.duan.chat.listener

import dongdong.duan.chat.mode.Posts
import dongdong.duan.chat.mode.Reels

interface PostListener {
    fun IncreaseView(postID:String)
    fun IncreaseLike(postID:String,islike:Int)
    fun Comment(PosIS:String,islike:Int)
    fun PlayMusic(urlms:String,play:Int)
    fun ShowImage(urlimg: String,name:String)
    fun ReelsClick(reels: Reels)
    fun PostClick(posts: Posts)
}