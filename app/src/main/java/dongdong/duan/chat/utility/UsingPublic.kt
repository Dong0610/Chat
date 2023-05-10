package dongdong.duan.chat.utility

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat
import java.util.*


open class UsingPublic {
    @SuppressLint("WrongConstant")
    fun ShowToast(context: Context, mess:Any?){
        Toast.makeText(context,mess.toString(),2000).show()
    }



     open fun UpdateToken(userid:String) {
         val database = FirebaseFirestore.getInstance()

         FirebaseMessaging.getInstance().token.addOnSuccessListener { token: String ->
             val documentSnapshot = database.collection(Constant.SYS_USERS)
                 .document(userid)
             documentSnapshot.update(Constant.USER_TOKEN, token)
                 .addOnFailureListener({ e: Exception? ->
                     Log.d("error",e!!.message.toString())
                 })
         }
    }

     open fun GetRealDateTime(date: Date): String? {
        return SimpleDateFormat("MM-dd-yyyy hh:mm:ss", Locale.getDefault()).format(date)
    }
     open fun StringToBitmap(encoder: String?): Bitmap? {
        return if (encoder != null) {
            val bytes = Base64.decode(encoder, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } else {
            null
        }
    }

}