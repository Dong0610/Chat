package dongdong.duan.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dongdong.duan.chat.databinding.ItemFriendViewBinding
import dongdong.duan.chat.listener.FriendListener
import dongdong.duan.chat.mode.Friend

class FriendAdapter(
    var listFriend: ArrayList<Friend>,
    var context: Context,
    var listener: FriendListener
) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(var binding: ItemFriendViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun SetData(friend: Friend) {
            Glide.with(context).load(friend.userurl)
                .into(binding.imgusers)
            binding.username.text = friend.userName
            binding.btnchat.setOnClickListener {
                listener.StartChat(friend)
            }
            binding.btnreject.setOnClickListener {
                listener.RemoveFriend(friend)
            }
            binding.root.setOnClickListener {
                listener.ViewInfo(friend)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        return FriendViewHolder(ItemFriendViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.SetData(listFriend.get(position))
    }


    override fun getItemCount(): Int {
        return listFriend.size
    }

}