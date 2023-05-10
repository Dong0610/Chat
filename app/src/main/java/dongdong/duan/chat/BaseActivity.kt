package dongdong.duan.chat

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.PreferenceManager
import dongdong.duan.chat.utility.UsingPublic


open class BaseActivity: AppCompatActivity() {
    private val REQUEST_CODE_PERMISSION = 123
    var mAuth = FirebaseAuth.getInstance()
   open lateinit var  preferenceManager:PreferenceManager
    open val database = FirebaseFirestore.getInstance()
    var documentReference: DocumentReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         preferenceManager = PreferenceManager(applicationContext)
        documentReference = database.collection(Constant.SYS_USERS)
            .document(preferenceManager.GetString(Constant.USER_ID).toString())


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
   override fun onPause() {
        super.onPause()
        documentReference!!.update(Constant.USER_ONLINE, 0)
    }

    override fun onResume() {
        super.onResume()
        documentReference!!.update(Constant.USER_ONLINE, 1)
    }
}


