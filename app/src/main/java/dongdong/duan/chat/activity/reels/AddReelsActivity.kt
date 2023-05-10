package dongdong.duan.chat.activity.reels

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants
import dongdong.duan.chat.R
import dongdong.duan.chat.adapter.ImageReelsAdapter
import dongdong.duan.chat.databinding.ActivityAddReelsBinding
import dongdong.duan.chat.listener.ImageReelsListener
import dongdong.duan.chat.mode.ImageData


@Suppress("DEPRECATION")
class AddReelsActivity : AppCompatActivity(),ImageReelsListener {
    lateinit var binding: ActivityAddReelsBinding
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReelsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SetOnclickEv()
        CheckPermission()

    }

    private fun CheckPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1000
            )
        } else {
            val images = getImages()
            val recyclerView = findViewById<RecyclerView>(R.id.recycleviewimg)

            recyclerView.layoutManager = GridLayoutManager(this, 3)
            recyclerView.adapter = images.let { ImageReelsAdapter(this, it,this) }
        }
    }


    private fun SetOnclickEv() {
        binding.icclosed.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getImages(): List<ImageData> {
        binding.progressload.visibility=View.VISIBLE
        val images = mutableListOf<ImageData>()
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.SIZE)
        val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null,  MediaStore.Images.Media.DATE_ADDED + " DESC")

        cursor?.let {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val size = cursor.getLong(sizeColumn)
                val path = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id).toString()

                images.add(ImageData(id, name, path, size))
            }
        }

        cursor?.close()
        binding.progressload.visibility=View.GONE
        return images
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val images = getImages()
                val recyclerView = findViewById<RecyclerView>(R.id.recycleviewimg)

                recyclerView.layoutManager = GridLayoutManager(this, 3)
                recyclerView.adapter = images.let { ImageReelsAdapter(this, it,this) }
            }
        } else {
            // Permission is denied, show a message to the user
            Toast.makeText(
                this,
                "Permission to access external storage is required to use this feature",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val saveImageActivityResultLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult(),
    ActivityResultCallback<ActivityResult>() {

        if (it.resultCode == RESULT_OK) {

            //handle the result uri ,by display it in an imageView
            val uri: Uri? = it.data?.data
            val intent= Intent(applicationContext,ReelViewActivity::class.java)
            intent.data=uri
            startActivity(intent)
            Toast.makeText(this, "Photo Saved", Toast.LENGTH_SHORT).show()
        }
    })
    @SuppressLint("ResourceAsColor")
    override fun ImageClick(image: ImageData, ichoose:Boolean) {
            binding.btncontinue.setBackgroundResource((if (ichoose) R.drawable.btn_click_enable else R.drawable.btn_click_disable))
        binding.btncontinue.setOnClickListener {

            val uri: Uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image.id)

            val dsPhotoEditorIntent = Intent(this, DsPhotoEditorActivity::class.java)
            dsPhotoEditorIntent.data = uri
      dsPhotoEditorIntent.putExtra(
                DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY,
                "Images"
            )

            val toolsToHide = intArrayOf(
                DsPhotoEditorActivity.TOOL_WARMTH,
                DsPhotoEditorActivity.TOOL_SATURATION,
                DsPhotoEditorActivity.TOOL_VIGNETTE,
                DsPhotoEditorActivity.TOOL_EXPOSURE
            )

            // if you don't want some of the tools to show up.
            // Just simply pass in the tools to hide in the UI.
            dsPhotoEditorIntent.putExtra(
                DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE,
                toolsToHide
            )

            saveImageActivityResultLauncher?.launch(dsPhotoEditorIntent)
        }

    }


}