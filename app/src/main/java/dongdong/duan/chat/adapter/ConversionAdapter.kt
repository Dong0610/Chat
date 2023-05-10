package dongdong.duan.chat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dongdong.duan.chat.R
import dongdong.duan.chat.databinding.ItemlistMessconversionViewBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.listener.ConversionListener
import dongdong.duan.chat.mode.Conversion
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.PreferenceManager

class ConversionAdapter(var listposts:ArrayList<Conversion>, var conversionListener: ConversionListener, var context: Context, var themetp:Int): RecyclerView.Adapter<ConversionAdapter.ConversionViewHolder>() {
    inner class ConversionViewHolder(var binding: ItemlistMessconversionViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        fun SetData(chatmees:Conversion) {
            var userID=PreferenceManager(context).GetString(Constant.USER_ID)
            if(themetp==1){
                binding.bgroundapp.background=context.getDrawable(R.drawable.item_conversion_bg_light)
                binding.txtusername.setTextColor(Color.BLACK)
            }
            if(chatmees.chatcv_sender_id.equals(userID)){
                binding.root.setOnClickListener{
                    conversionListener.StartChat(chatmees.chatcv_id,chatmees.chatcv_receive_id)
                }
                binding.imguserapp.setImageBitmap(BitmapUtils.StringToBitmap(chatmees.chatcv_receive_img))
                binding.txtusername.text=chatmees.chatcv_receive_name
                if(chatmees.chatcv_last_message.equals("")){
                    binding.txtmessage.text="Bạn: "+": Đã gửi ảnh!"
                }
                else{
                    binding.txtmessage.text="Bạn: "+chatmees.chatcv_last_message
                }
            }
            else{
                binding.root.setOnClickListener{
                    conversionListener.StartChat(chatmees.chatcv_id,chatmees.chatcv_sender_id)
                }
                binding.imguserapp.setImageBitmap(BitmapUtils.StringToBitmap(chatmees.chatcv_receive_img))
                binding.txtusername.text=chatmees.chatcv_sender_name
                if(chatmees.chatcv_last_message.equals("")){
                    binding.txtmessage.text=chatmees.chatcv_sender_name.substringAfterLast(" ")+": Đã gửi ảnh!"
                }
                else{
                    binding.txtmessage.text=chatmees.chatcv_sender_name.substringAfterLast(" ")+": "+chatmees.chatcv_last_message
                }

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionViewHolder {
        return ConversionViewHolder(
            ItemlistMessconversionViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ConversionViewHolder, position: Int) {
        holder.SetData(listposts.get(position))
    }

    override fun getItemCount(): Int {
        return listposts.size
    }
}