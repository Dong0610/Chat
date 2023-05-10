package dongdong.duan.chat.mode

class Comment{
    lateinit var Post_ID:String
    lateinit var Post_UsId:String
    lateinit var PostUS_Name:String
    lateinit var Post_UsImg:String
    lateinit var Post_Content:String
    lateinit var Post_Time:String
    constructor()
    constructor(
        Post_ID: String,
        Post_UsId: String,
        PostUS_Name: String,
        Post_UsImg: String,
        Post_Content: String,
        Post_Time: String
    ) {
        this.Post_ID = Post_ID
        this.Post_UsId = Post_UsId
        this.PostUS_Name = PostUS_Name
        this.Post_UsImg = Post_UsImg
        this.Post_Content = Post_Content
        this.Post_Time = Post_Time
    }


}