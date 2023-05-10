package dongdong.duan.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dongdong.duan.chat.activity.fragment.HomeFragment
import dongdong.duan.chat.databinding.ItemlistReelsViewBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.listener.PostListener
import dongdong.duan.chat.mode.Reels

class ReelsAdapter(
    var listreels:ArrayList<Reels>,
    var context:Context,
    var reelsListener: PostListener
) :RecyclerView.Adapter<ReelsAdapter.ReelsViewHolder>(){
    inner class ReelsViewHolder(private  var binding:ItemlistReelsViewBinding):RecyclerView.ViewHolder(binding.root){
        fun SetData(reels: Reels)
        {
            BitmapUtils.ShowImage(context,reels.REELS_URL_IMG,binding.imgreels)
            binding.imgUser.setImageBitmap(BitmapUtils.StringToBitmap(reels.REELS_US_IMG))
            binding.txttextname.text=reels.REELS_US_NAME
            binding.root.setOnClickListener {
                reelsListener.ReelsClick(reels)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelsViewHolder {
        return ReelsViewHolder(ItemlistReelsViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return listreels.size
    }

    override fun onBindViewHolder(holder: ReelsViewHolder, position: Int) {
        holder.SetData(listreels.get(position))
    }
}