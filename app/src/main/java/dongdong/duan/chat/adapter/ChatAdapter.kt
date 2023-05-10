package dongdong.duan.chat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.internal.ViewUtils.dpToPx
import dongdong.duan.chat.R
import dongdong.duan.chat.databinding.ItemMessageReceiveViewBinding
import dongdong.duan.chat.databinding.ItemMessageSenderViewBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.listener.ChatListener
import dongdong.duan.chat.mode.Chat
import dongdong.duan.chat.utility.UsingPublic


class ChatAdapter(var listmessage:ArrayList<Chat>, var usersID: String, var viewtheme: Int, var context: Context, var rcvImg: String,var chatListener: ChatListener): RecyclerView.Adapter<RecyclerView.ViewHolder> (){

    inner class ReceiveMessViewHolder(var binding: ItemMessageReceiveViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun SetData(chat: Chat) {
            SetGD()

            if (chat.CHAT_TYPE.equals("1")) {
                binding.layoutimg.visibility = View.VISIBLE
              BitmapUtils.ShowImage(context,chat.CHAT_IMGURL,binding.imganh)
                binding.textreceive.visibility = View.GONE
            } else {
                binding.textreceive.text = chat.CHAT_CONTENT
                binding.textreceive.visibility = View.VISIBLE
            }
            binding.imganh.setOnClickListener {
                chatListener.ShowImage(chat.CHAT_IMGURL)
            }
            binding.txttimeuse.text = chat.CHAT_TIME
            binding.imgicusers.setImageBitmap(UsingPublic().StringToBitmap(rcvImg))
            binding.textreceive.setOnClickListener {
                binding.txttimeuse.visibility = View.VISIBLE
                Handler().postDelayed({
                    binding.txttimeuse.visibility = View.GONE
                }, 1500)
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        fun SetGD() {
            binding.textreceive.setTextColor(Color.BLACK)
            if (viewtheme==1) {
                binding.textreceive.background =
                    context.getDrawable(R.drawable.item_message_view_light)
                binding.textreceive.setPadding(30, 10, 30, 10)
                binding.textreceive.setTextColor(Color.BLACK)}
            else {
                binding.textreceive.setPadding(30, 10, 30, 10)
                binding.textreceive.setTextColor(Color.WHITE)
            }

        }
    }

    inner class SenderMessViewHolder(var binding: ItemMessageSenderViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun SetData(chat: Chat) {
            SetGD()


            if (chat.CHAT_TYPE.equals("1")) {
                binding.imganh.visibility = View.VISIBLE
                BitmapUtils.ShowImage(context,chat.CHAT_IMGURL,binding.imganh)
                binding.txtmessage.visibility = View.GONE
            } else {
                binding.txtmessage.text = chat.CHAT_CONTENT
                binding.txtmessage.visibility = View.VISIBLE
            }
            binding.imganh.setOnClickListener {
                chatListener.ShowImage(chat.CHAT_IMGURL)
            }
            binding.txttimeuse.text = chat.CHAT_TIME
            binding.txtmessage.setOnClickListener {
                binding.txttimeuse.visibility = View.VISIBLE
                Handler().postDelayed({
                    binding.txttimeuse.visibility = View.GONE
                }, 1500)
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private fun SetGD() {
            if (viewtheme.toString().equals("1")) {
                binding.txtmessage.background =
                    context.getDrawable(R.drawable.item_message_view_light)
                binding.txtmessage.setTextColor(Color.BLACK)
                binding.txtmessage.setPadding(30, 10, 30, 10)
//            } else if (viewtheme.equals("giaodienxanh")) {
//                binding.txtmessage.background =
//                    context.getDrawable(R.drawable.item_message_view_blue)
//                binding.txtmessage.setPadding(30, 10, 30, 10)
//                binding.txtmessage.setTextColor(Color.BLACK)
       } else {
                binding.txtmessage.setPadding(30, 10, 30, 10)
                binding.txtmessage.setTextColor(Color.WHITE)
            }
        }
    }

    private var VIEW_TYPE_SEND = 1
    private var VIEW_TYPE_RECEIVE = 2
    private var lastPosition = -1
    override fun getItemViewType(position: Int): Int {
        if (listmessage.get(position).CHAT_SENDER_ID.equals(usersID)) {
            return VIEW_TYPE_SEND
        } else {
            return VIEW_TYPE_RECEIVE
        }
    }

    private fun setAnimationSend(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation: Animation =
                AnimationUtils.loadAnimation(context, R.anim.mess_sender_view)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    private fun setAnimationRcev(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation: Animation =
                AnimationUtils.loadAnimation(context, R.anim.mess_receive_view)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_SEND) {
            return SenderMessViewHolder(
                ItemMessageSenderViewBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        } else {
            return ReceiveMessViewHolder(
                ItemMessageReceiveViewBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_SEND) {
            (holder as SenderMessViewHolder).SetData(listmessage.get(position))
            setAnimationSend(holder.binding.root, position)
        } else {
            (holder as ReceiveMessViewHolder).SetData(listmessage.get(position))
            setAnimationRcev(holder.binding.root, position)
        }
    }

    override fun getItemCount(): Int {
        return listmessage.size
    }
}