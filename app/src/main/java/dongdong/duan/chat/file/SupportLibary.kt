package dongdong.duan.chat.file

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import dongdong.duan.chat.R
import dongdong.duan.chat.mode.Users
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.PreferenceManager
import dongdong.duan.chat.utility.UsingPublic
import java.text.SimpleDateFormat
import java.util.*

object SupportLibary {
    @SuppressLint("StaticFieldLeak")
    private var database = FirebaseFirestore.getInstance()
    public fun GetThemeType(context: Context?): Int {
        val preferenceManager = PreferenceManager(context!!);
        val themetype = preferenceManager.GetString(Constant.KEY_THEME_APP)!!.toInt()
        return themetype;
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    public fun ShowDialog(message: Any, notitype: Int, fragmen: Fragment) {
        val dialogbinding = fragmen.layoutInflater.inflate(R.layout.custom_dialog_error, null)
        var mydialog = Dialog(fragmen.requireContext())
        mydialog.setContentView(dialogbinding)
        mydialog.setCancelable(false)
        dialogbinding.findViewById<TextView>(R.id.messageSEV).text = message.toString()
        if (notitype == 1) {
            dialogbinding.findViewById<TextView>(R.id.txtsweSEW).text = "Success"
            dialogbinding.findViewById<ImageView>(R.id.iconswe)
                .setImageResource(R.drawable.ic_success_baseline)
            dialogbinding.findViewById<Button>(R.id.btncancelSEW).background =
                fragmen.requireContext().getDrawable(R.drawable.cus_bg_success)
        } else if (notitype == 2) {
            dialogbinding.findViewById<TextView>(R.id.txtsweSEW).text = "Warning"
            dialogbinding.findViewById<ImageView>(R.id.iconswe)
                .setImageResource(R.drawable.ic_warning_baseline)
            dialogbinding.findViewById<Button>(R.id.btncancelSEW).background =
                fragmen.requireContext().getDrawable(R.drawable.cus_bg_warning)
        } else if (notitype == 3) {
            dialogbinding.findViewById<TextView>(R.id.txtsweSEW).text = "Error"
            dialogbinding.findViewById<ImageView>(R.id.iconswe)
                .setImageResource(R.drawable.ic_error_baseline)
            dialogbinding.findViewById<Button>(R.id.btncancelSEW).background =
                fragmen.requireContext().getDrawable(R.drawable.cus_bg_error)
        }
        mydialog.show()
        dialogbinding.findViewById<Button>(R.id.btncancelSEW).setOnClickListener {
            mydialog.dismiss()
        }
    }

    @SuppressLint("WrongConstant")
    fun ShowToast(context: Context, mess:Any?){
        Toast.makeText(context,mess.toString(),2000).show()
    }

    fun FileExTension(uri: Uri, getContentResolver: ContentResolver): String? {
        val cr: ContentResolver = getContentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(uri))
    }


     fun GetRealDateTime(date: Date): String? {
        return SimpleDateFormat("MM-dd-yyyy hh:mm:ss", Locale.getDefault()).format(date)
    }
    fun GetUserByID(iduser: String, callback: (Users?) -> Unit) {
        database.collection(Constant.SYS_USERS)
            .document(iduser)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val document = task.result
                    val user = Users()
                    user.USER_ID = document.id
                    user.USER_EMAIL = document.getString(Constant.USER_EMAIL).toString()
                    user.USER_NAME = document.getString(Constant.USER_NAME).toString()
                    user.USER_PASS = document.getString(Constant.USER_PASS).toString()
                    user.USER_IMAGE = document.getString(Constant.USER_IMAGE).toString()
                    user.USER_ANHBIA = document.getString(Constant.USER_ANHBIA).toString()
                    user.USER_URLIMG = document.getString(Constant.USER_URLIMG).toString()
                    callback(user)
                } else {
                    callback(null)
                }
            }
    }



    fun ScreenWidth(context: Context?):Int{
        val displayMetrics = context!!.resources.displayMetrics
       return displayMetrics.widthPixels
    }
    fun ScreenHeight(context: Context?):Int{
        val displayMetrics = context!!.resources.displayMetrics
        return displayMetrics.heightPixels
    }

}