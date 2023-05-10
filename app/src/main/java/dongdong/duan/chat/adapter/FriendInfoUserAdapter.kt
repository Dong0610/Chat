package dongdong.duan.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dongdong.duan.chat.databinding.ItemFriendInfoUsBinding
import dongdong.duan.chat.listener.ListUserEvent
import dongdong.duan.chat.mode.Friend
import dongdong.duan.chat.mode.Users

class FriendInfoUserAdapter(var listuser:ArrayList<Friend>, var context:Context,var listener:ListUserEvent):RecyclerView.Adapter<FriendInfoUserAdapter.FriendInfoViewHolder>() {
    inner class FriendInfoViewHolder(private var binding:ItemFriendInfoUsBinding):RecyclerView.ViewHolder(binding.root){
        fun SetData(users: Friend){
            Glide.with(context).load(users.userurl).into(binding.imgreels)
            binding.txttextname.text=users.userName
            binding.root.setOnClickListener {
                listener.ViewInfo(users.userID)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendInfoViewHolder {
        return FriendInfoViewHolder(ItemFriendInfoUsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
    return  listuser.size
    }

    override fun onBindViewHolder(holder: FriendInfoViewHolder, position: Int) {
        holder.SetData(listuser.get(position))
    }
}