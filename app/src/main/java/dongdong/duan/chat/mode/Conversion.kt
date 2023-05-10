package dongdong.duan.chat.mode

import java.util.*
import kotlin.properties.Delegates

class Conversion : java.io.Serializable {
    lateinit var chatcv_id: String
    lateinit var chatcv_sender_id: String
    lateinit var chatcv_receive_id: String
    lateinit var chatcv_sender_name: String
    lateinit var chatcv_receive_name: String
    lateinit var chatcv_receive_img: String
    lateinit var chatcv_last_message: String
    lateinit var chatcv_last_message_time :Date

    constructor() {}
    constructor(
        chatcv_id: String,
        chatcv_sender_id: String,
        chatcv_receive_id: String,
        chatcv_sender_name: String,
        chatcv_receive_name: String,
        chatcv_receive_img: String,
        chatcv_last_message: String,
        chatcv_last_message_time: Date
    ) {
        this.chatcv_id = chatcv_id
        this.chatcv_sender_id = chatcv_sender_id
        this.chatcv_receive_id = chatcv_receive_id
        this.chatcv_sender_name = chatcv_sender_name
        this.chatcv_receive_name = chatcv_receive_name
        this.chatcv_receive_img = chatcv_receive_img
        this.chatcv_last_message = chatcv_last_message
        this.chatcv_last_message_time = chatcv_last_message_time
    }

}
