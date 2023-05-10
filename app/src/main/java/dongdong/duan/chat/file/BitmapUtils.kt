package dongdong.duan.chat.file

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Base64
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.internal.ViewUtils
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import dongdong.duan.chat.R
import dongdong.duan.chat.utility.UsingPublic
import java.io.ByteArrayOutputStream
import java.util.*

object BitmapUtils {
    fun BitmapFromUriCusSize(contex:Context,uri: Uri?, targetWidth: Int): Bitmap? {
        val inputStream =contex.contentResolver.openInputStream(uri!!)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream?.close()
        var scaleFactor = 1
        if (options.outWidth > targetWidth) {
            val widthScale = Math.round(options.outWidth.toFloat() / targetWidth.toFloat())
            scaleFactor = widthScale
        }
        options.inSampleSize = scaleFactor
        options.inJustDecodeBounds = false
        val scaledBitmap = BitmapFactory.decodeStream(contex.contentResolver.openInputStream(uri), null, options)
        return getRoundedBitmap(scaledBitmap)
    }

     fun EncoderImage(bitmap: Bitmap): String {
        val preWith = 200
        val preHeight = bitmap.height * preWith / bitmap.width
        val prevBitMap = Bitmap.createScaledBitmap(bitmap, preWith, preHeight, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        prevBitMap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    fun BitmapToString(bitmap: Bitmap?): String {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bytes = stream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    fun StringToBitmap(encodedImage: String?): Bitmap {
        val decodedBytes = Base64.decode(encodedImage!!, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    private fun getRoundedBitmap(bitmap: Bitmap?): Bitmap? {
        if (bitmap == null) {
            return null
        }
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val color = Color.RED
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawCircle(bitmap.width / 2f, bitmap.height / 2f, bitmap.width / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    fun ShowImage(applicationContext: Context,url:Any,view: ImageView){
        val widthimg=SupportLibary.ScreenWidth(applicationContext)
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
    @SuppressLint("RestrictedApi")
    fun ShowImageBitmap(applicationContext: Context, bitmap: Bitmap, widthimg:Int, view: ImageView) {
        var scaleFactor = 1f
        val width = bitmap.width
        if (width > 0 && width < ViewUtils.dpToPx(applicationContext, widthimg)) {
            // Image width is less than 220dp, scale to actual size
            scaleFactor = ViewUtils.dpToPx(applicationContext, width) / width
        } else if (width > ViewUtils.dpToPx(applicationContext, widthimg)) {
            // Image width is greater than 220dp, scale to 220dp
            scaleFactor = ViewUtils.dpToPx(applicationContext, widthimg) / width
        }
        view.setScaleType(ImageView.ScaleType.CENTER_INSIDE)
        view.setImageBitmap(
            Bitmap.createScaledBitmap(
                bitmap,
                (width * scaleFactor).toInt(),
                (bitmap.height * scaleFactor).toInt(),
                false
            )
        )
    }

    fun MyQrCodeUser(obj: Any, width: Int): Bitmap? {
        val gson = Gson()
        val jsonString = gson.toJson(obj)
        try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(jsonString, BarcodeFormat.QR_CODE, width, width, hints)
            val bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
            for (x in 0 until width) {
                for (y in 0 until width) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }


}