package dongdong.duan.chat.mode
import java.util.*

class Chat:java.io.Serializable {
    lateinit var CHAT_ID:String
    lateinit var CHAT_CONTENT:String
    lateinit var CHAT_SENDER_NAME:String
    lateinit var CHAT_SENDER_ID:String
    lateinit var CHAT_RECEIVE_ID:String
    lateinit var CHAT_RECEIVE_NAME:String
    lateinit var CHAT_TIME:String
    lateinit var CHAT_DATE:Date
    lateinit var CHAT_IMGURL:String
    lateinit var CHAT_TYPE:String


    constructor()
    constructor(
        CHAT_ID: String,
        CHAT_CONTENT: String,
        CHAT_SENDER_NAME: String,
        CHAT_SENDER_ID: String,
        CHAT_RECEIVE_ID: String,
        CHAT_RECEIVE_NAME: String,
        CHAT_TIME: String,
        CHAT_DATE: Date,
        CHAT_IMGURL: String,
        CHAT_TYPE: String
    ) {
        this.CHAT_ID = CHAT_ID
        this.CHAT_CONTENT = CHAT_CONTENT
        this.CHAT_SENDER_NAME = CHAT_SENDER_NAME
        this.CHAT_SENDER_ID = CHAT_SENDER_ID
        this.CHAT_RECEIVE_ID = CHAT_RECEIVE_ID
        this.CHAT_RECEIVE_NAME = CHAT_RECEIVE_NAME
        this.CHAT_TIME = CHAT_TIME
        this.CHAT_DATE = CHAT_DATE
        this.CHAT_IMGURL = CHAT_IMGURL
        this.CHAT_TYPE = CHAT_TYPE
    }
}