package dongdong.duan.chat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dongdong.duan.chat.R
import dongdong.duan.chat.databinding.ItemusersListViewBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.listener.ListUserEvent
import dongdong.duan.chat.mode.Users


class UserAdapter(var theme:Int,var userlist:List<Users>,var context:Context?,var userid:String,var listener:ListUserEvent):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    inner class UserViewHolder(var binding:ItemusersListViewBinding):RecyclerView.ViewHolder(binding.root){
        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        fun SetData(user:Users){
            if(theme==1){
                binding.btnchat.background=context!!.getDrawable(R.drawable.itemview_user_btn_light)
                binding.btnaddfr.background=context!!.getDrawable(R.drawable.itemview_user_btn_light)
                binding.bgroundapp.background=context!!.getDrawable(R.drawable.itemview_bg_listuser_light)
                binding.btnchat.setTextColor(Color.BLACK)
                binding.btnaddfr.setTextColor(Color.BLACK)
                binding.username.setTextColor(Color.BLACK)
            }
            binding.imgusers.setImageBitmap(BitmapUtils.StringToBitmap(user.USER_IMAGE))
            binding.username.text=user.USER_NAME
            binding.btnchat.setOnClickListener {
                listener.StatChat(user)
            }

            binding.root.setOnClickListener {
                listener.ViewInfo(user.USER_ID)
            }
             if(user.IS_FRIEND==0){
                 binding.btnaddfr.visibility=View.VISIBLE
                 binding.btnreject.visibility=View.GONE
                 binding.btnaccepted.visibility=View.GONE
             }else if(user.IS_FRIEND==1){
                 binding.btnreject.text="Hủy"
                 binding.btnaddfr.visibility=View.GONE
                 binding.btnreject.visibility=View.VISIBLE
                 binding.btnaccepted.visibility=View.GONE
             } else if(user.IS_FRIEND==2){
                 binding.btnaddfr.visibility=View.GONE
                 binding.btnreject.visibility=View.GONE
                 binding.btnaccepted.visibility=View.VISIBLE
                 binding.btnaccepted.text="Đã gửi"
             }
            binding.btnaddfr.setOnClickListener {
                user.IS_FRIEND?.let { it1 -> listener.AddFriend(user, isadd = it1) }
                binding.btnaddfr.text="Đã gửi"
            }
            binding.btnaccepted.setOnClickListener {
                user.IS_FRIEND?.let { it1 -> listener.AccetpFriend(user, isadd = it1) }
                binding.btnaccepted.text="OK"
            }
            binding.btnreject.setOnClickListener {
                user.IS_FRIEND?.let { it1 -> listener.RejectFriend(user, isadd = it1) }
                binding.btnreject.text="Hủy"
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(ItemusersListViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return userlist.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.SetData(userlist.get(position))
    }
}