package dongdong.duan.chat.mode

import java.util.*
import kotlin.properties.Delegates


class Users:java.io.Serializable {
    lateinit var USER_ID:String
    lateinit var USER_NAME:String
    lateinit var USER_EMAIL:String
    lateinit var USER_PASS:String
    lateinit var USER_TOKEN:String
    lateinit var USER_IMAGE:String
    lateinit var USER_URLIMG:String
    lateinit var USER_ANHBIA:String
    lateinit var USER_TIME: Date
    var IS_FRIEND:Int?=null

    constructor()
    constructor(
        USER_ID: String,
        USER_NAME: String,
        USER_EMAIL: String,
        USER_PASS: String,
        USER_TOKEN: String,
        USER_IMAGE: String,
        USER_URLIMG: String,
        USER_ANHBIA: String,
        USER_TIME: Date,
        IS_FRIEND: Int?
    ) {
        this.USER_ID = USER_ID
        this.USER_NAME = USER_NAME
        this.USER_EMAIL = USER_EMAIL
        this.USER_PASS = USER_PASS
        this.USER_TOKEN = USER_TOKEN
        this.USER_IMAGE = USER_IMAGE
        this.USER_URLIMG = USER_URLIMG
        this.USER_ANHBIA = USER_ANHBIA
        this.USER_TIME = USER_TIME
        this.IS_FRIEND = IS_FRIEND
    }

}