package dongdong.duan.chat.utility

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import dongdong.duan.chat.R

class CustomDialog (var activity: Activity){
    fun CreateDialog(): AlertDialog {
        val alertdialog: AlertDialog
        var inflater= activity.layoutInflater
        var builder= AlertDialog.Builder(activity)
        val dialogLayout = inflater.inflate(R.layout.custom_dialog_load, null)
        dialogLayout.setBackgroundColor(Color.TRANSPARENT) // set background color
        builder.setView(dialogLayout)
        builder.setCancelable(false)
        alertdialog= builder.create()
        return alertdialog
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    fun ShowDialog(message: Any, notitype:Int){
            val dialogbinding=this.activity.layoutInflater.inflate(R.layout.custom_dialog_error,null)
            var mydialog= Dialog(this.activity)
            mydialog.setContentView(dialogbinding)
            mydialog.setCancelable(false)
            dialogbinding.findViewById<TextView>(R.id.messageSEV).text=message.toString()
            if(notitype==1){
                dialogbinding.findViewById<TextView>(R.id.txtsweSEW).text="Success"
                dialogbinding.findViewById<ImageView>(R.id.iconswe).setImageResource(R.drawable.ic_success_baseline)
                dialogbinding.findViewById<Button>(R.id.btncancelSEW).background=activity.getDrawable(R.drawable.cus_bg_success)
            }
            else if(notitype==2){
                dialogbinding.findViewById<TextView>(R.id.txtsweSEW).text="Warning"
                dialogbinding.findViewById<ImageView>(R.id.iconswe).setImageResource(R.drawable.ic_warning_baseline)
                dialogbinding.findViewById<Button>(R.id.btncancelSEW).background=activity.getDrawable(R.drawable.cus_bg_warning)
            }
            else if(notitype==3){
                dialogbinding.findViewById<TextView>(R.id.txtsweSEW).text="Error"
                dialogbinding.findViewById<ImageView>(R.id.iconswe).setImageResource(R.drawable.ic_error_baseline)
                dialogbinding.findViewById<Button>(R.id.btncancelSEW).background=activity.getDrawable(R.drawable.cus_bg_error)
            }
            mydialog.show()
            dialogbinding.findViewById<Button>(R.id.btncancelSEW).setOnClickListener {
                mydialog.dismiss()
            }
        }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "MissingInflatedId")
    fun ConfirmDialog(message: Any,function: () -> Unit){
        val dialogbinding=this.activity.layoutInflater.inflate(R.layout.custom_dialog_confirm,null)
        var mydialog= Dialog(this.activity)
        mydialog.setContentView(dialogbinding)
        mydialog.setCancelable(false)
        dialogbinding.findViewById<TextView>(R.id.messageSEV).text=message.toString()

        // Set the dialog width to match parent
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        mydialog.window?.setLayout(width, height)

        mydialog.show()
        dialogbinding.findViewById<Button>(R.id.btncancelSEW).setOnClickListener {
            mydialog.dismiss()
        }
        dialogbinding.findViewById<Button>(R.id.btnconfirmSEW).setOnClickListener {
            function()
            mydialog.dismiss()
        }
    }


}