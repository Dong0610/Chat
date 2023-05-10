package dongdong.duan.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dongdong.duan.chat.databinding.ItemCommentViewBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.listener.CommentListener
import dongdong.duan.chat.mode.Comment

class CommentAdapter(var listcomment: ArrayList<Comment>, var commentListener: CommentListener) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    inner class CommentViewHolder(var binding: ItemCommentViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun SetData(comment: Comment) {
            binding.txtcontent.text = comment.Post_Content
            binding.txtusername.text = comment.PostUS_Name
            binding.imgicusers.setImageBitmap(BitmapUtils.StringToBitmap(comment.Post_UsImg))
            binding.txttimeuse.text = comment.Post_Time
            binding.txtusername.setOnClickListener {
                commentListener.UserViewClick(comment.Post_UsId)
            }
            binding.imgicusers.setOnClickListener {
                commentListener.UserViewClick(comment.Post_UsId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            ItemCommentViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return listcomment.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.SetData(listcomment.get(position))
    }
}