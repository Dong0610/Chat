package dongdong.duan.chat.activity.chat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.internal.ViewUtils
import com.google.firebase.storage.FirebaseStorage
import dongdong.duan.chat.databinding.ActivityImageShowBinding
import dongdong.duan.chat.file.SupportLibary
import dongdong.duan.chat.utility.UsingPublic
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


@Suppress("DEPRECATION")
class ImageShow : AppCompatActivity() {
    lateinit var binding:ActivityImageShowBinding
    var storage=FirebaseStorage.getInstance()
    lateinit var bitmap:Bitmap
    @SuppressLint("WrongThread")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityImageShowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var imgurl=intent.getStringExtra("urlimg")
        var username=intent.getStringExtra("name")
        binding.txtusername.text=username
        binding.icbackpress.setOnClickListener {
            onBackPressed()
        }
        ShowImage(applicationContext, imgurl!!, binding.imgaeviewshow)
        binding.icdownload.setOnClickListener {
            bitmap = (binding.imgaeviewshow.drawable as BitmapDrawable).bitmap
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val byteArray = outputStream.toByteArray()
            val fileName = getFilenameFromUrl(imgurl)
            try {
                val file = File(applicationContext.getExternalFilesDir(null), fileName)
                val fileOutputStream = FileOutputStream(file)
                fileOutputStream.write(byteArray)
                fileOutputStream.close()
                Toast.makeText(applicationContext, "Image saved successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(applicationContext, "Error saving image", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun getFilenameFromUrl(urlString: String): String? {
        val uri = Uri.parse(urlString)
        val path = uri.path
        return path?.substringAfterLast("/")
    }

    fun ShowImage(applicationContext: Context,url:Any,view: ImageView){
        val widthimg= SupportLibary.ScreenWidth(applicationContext)
        Glide.with(applicationContext)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap?>() {
                @SuppressLint("RestrictedApi")
                override fun onResourceReady(
                    @NonNull resource: Bitmap,
                    @Nullable transition: Transition<in Bitmap?>?
                ) {
                    var scaleFactor = 1f
                    val width = resource.width
                    if (width > 0 && width < ViewUtils.dpToPx(applicationContext, widthimg)) {
                        scaleFactor = ViewUtils.dpToPx(applicationContext, width) / width
                    } else if (width > ViewUtils.dpToPx(applicationContext, widthimg)) {
                        scaleFactor = ViewUtils.dpToPx(applicationContext, widthimg) / width
                    }
                    view.scaleType = ImageView.ScaleType.FIT_CENTER
                    view.setImageBitmap(
                        Bitmap.createScaledBitmap(
                            resource,
                            (width * scaleFactor).toInt(),
                            (resource.height * scaleFactor).toInt(),
                            false
                        )
                    )
                }
                override fun onLoadCleared(@Nullable placeholder: Drawable?) {
                    UsingPublic().ShowToast(applicationContext,"Can't not load image!")
                }
            })
    }
}