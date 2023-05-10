package dongdong.duan.chat.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface ApiService {
    @POST("send")
    fun sendMess(
        @HeaderMap header: HashMap<String, String>,
        @Body messbody: String?
    ): Call<String?>?
}