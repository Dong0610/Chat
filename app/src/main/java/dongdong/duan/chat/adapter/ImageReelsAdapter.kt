package dongdong.duan.chat.adapter

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dongdong.duan.chat.databinding.ItemReelsImageViewBinding
import dongdong.duan.chat.listener.ImageReelsListener
import dongdong.duan.chat.mode.ImageData

class ImageReelsAdapter(private val context: Context, private val images: List<ImageData>,var imageReelsListener: ImageReelsListener) : RecyclerView.Adapter<ImageReelsAdapter.ImageViewHolder>() {
   inner class ImageViewHolder(var binding: ItemReelsImageViewBinding) : RecyclerView.ViewHolder(binding.root) {
      fun SetData(image:ImageData){
          var isclick=true
          val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image.id)
          Glide.with(context)
              .load(uri)
              .into(binding.imageviewreels)
          binding.root.setOnClickListener {
              imageReelsListener.ImageClick(image,isclick)
              binding.iconischeck.isChecked= if(isclick) true else false
              isclick = if (isclick) false else true
          }
      }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view =ItemReelsImageViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
       holder.SetData(images.get(position))
    }

    override fun getItemCount(): Int {
        return images.size
    }
}
