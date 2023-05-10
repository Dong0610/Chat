package dongdong.duan.chat.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dongdong.duan.chat.R
import dongdong.duan.chat.databinding.ItemlistNotificationViewBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.listener.NotifyListener
import dongdong.duan.chat.mode.Notification

class NotificationAdapter(val listnotify:ArrayList<Notification>,var context: Context,var notifiln:NotifyListener,var themeTyPe:Int)
    :RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(private var binding:ItemlistNotificationViewBinding):RecyclerView.ViewHolder(binding.root){
        fun SetData(nofify:Notification){
            if(themeTyPe==1){
                binding.root.background= context.getDrawable(R.drawable.itemview_bg_listpost_light)
                binding.txtcontent.setTextColor(Color.BLACK)
                binding.txttimenotf.setTextColor(Color.GRAY)

                // binding.iclike.setImageDrawable(context.getDrawable(R.drawable.ic_round_like_light))

            }
            binding.imguserapp.setImageBitmap(BitmapUtils.StringToBitmap(nofify.NOTI_USER_IMG))
            binding.txtcontent.text=nofify.NOTI_STATUS
            binding.txttimenotf.text= nofify.NOTI_DATE
            binding.root.setOnClickListener {
                notifiln.StartNotiify(nofify)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return  NotificationViewHolder(ItemlistNotificationViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return  listnotify.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.SetData(listnotify.get(position))
    }
}