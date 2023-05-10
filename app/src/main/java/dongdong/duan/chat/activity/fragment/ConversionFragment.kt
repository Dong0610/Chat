package dongdong.duan.chat.activity.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import dongdong.duan.chat.R
import dongdong.duan.chat.activity.chat.ChatActivity
import dongdong.duan.chat.adapter.ConversionAdapter
import dongdong.duan.chat.adapter.UserAdapter
import dongdong.duan.chat.databinding.FragmentConversionBinding
import dongdong.duan.chat.file.SupportLibary
import dongdong.duan.chat.listener.ConversionListener
import dongdong.duan.chat.mode.Conversion
import dongdong.duan.chat.mode.Users
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.PreferenceManager
import dongdong.duan.chat.utility.UsingPublic
import java.util.*
import kotlin.collections.ArrayList

class ConversionFragment : Fragment(), ConversionListener {
    lateinit var binding: FragmentConversionBinding
    lateinit var preferenceManager: PreferenceManager
    val reldatabase = FirebaseDatabase.getInstance().getReference()
    lateinit var conversionAdapter: ConversionAdapter
    val database = FirebaseFirestore.getInstance()
    lateinit var conversionlist: ArrayList<Conversion>

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConversionBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager(context)
        CheckListMessConver()
        LoadGiaoDien()
        conversionlist = ArrayList()
        conversionAdapter = ConversionAdapter(
            conversionlist,
            this,
            requireContext(),
            SupportLibary.GetThemeType(requireContext())
        )
        binding.rclistconersion.adapter = conversionAdapter
        binding.iconsreach.setOnClickListener {
            Find()
        }

        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun CheckListMessConver() {
        binding.progressload.visibility = View.VISIBLE
        database.collection(Constant.CONVERSION_SYS)
            .addSnapshotListener(listenerConversion)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun ShowUsers(conversionlist:ArrayList<Conversion>){
        Collections.sort(conversionlist, { obj1: Conversion, obj2: Conversion ->
            obj2.chatcv_last_message_time.compareTo(obj1.chatcv_last_message_time)
        })
        if (conversionlist.size == 0) {
            conversionAdapter.notifyDataSetChanged()
        } else {
            conversionAdapter.notifyItemRangeChanged(
                conversionlist.size,
                conversionlist.size
            )
            binding.rclistconersion.scrollToPosition(conversionlist.size - 1)
            binding.progressload.visibility = View.GONE
            binding.rclistconersion.adapter = conversionAdapter

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private val listenerConversion =
        com.google.firebase.firestore.EventListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
            if (error != null) {
                return@EventListener
            }
            if (value != null) {
                binding.progressload.visibility = View.GONE
                for (documentChange in value.documentChanges) {
                    if (documentChange.type == DocumentChange.Type.ADDED) {

                        val conversion = Conversion().apply {
                            chatcv_id = documentChange.document.id
                            chatcv_sender_id =
                                documentChange.document.getString(Constant.CHATCV_SENDER_ID)
                                    .toString()
                            chatcv_sender_name =
                                documentChange.document.getString(Constant.CHATCV_SENDER_NAME)
                                    .toString()
                            chatcv_receive_id =
                                documentChange.document.getString(Constant.CHATCV_RECEIVE_ID)
                                    .toString()
                            chatcv_receive_name =
                                documentChange.document.getString(Constant.CHATCV_RECEIVE_NAME)
                                    .toString()
                            chatcv_receive_img =
                                documentChange.document.getString(Constant.CHATCV_RECEIVE_IMAGE)
                                    .toString()
                            chatcv_last_message =
                                documentChange.document.getString(Constant.CHATCV_LAST_MESSAGE)
                                    .toString()
                            chatcv_last_message_time =
                                documentChange.document.getDate(Constant.CHATCV_TIME) as Date

                        }
                        if(conversion.chatcv_sender_id.equals(preferenceManager.GetString(Constant.USER_ID))
                            || conversion.chatcv_receive_id.equals(preferenceManager.GetString(Constant.USER_ID))
                        ){
                            conversionlist.add(conversion)
                        }

                    }
                }
                ShowUsers(conversionlist)
            }
        }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun LoadGiaoDien() {
        val themetype = SupportLibary.GetThemeType(this.context)
        if (themetype == 1) {
            binding.txtsreach.background = context?.getDrawable(R.drawable.input_bg_edt_lighr)
//            binding.txtsreach.textCursorDrawable =
//                context?.getDrawable(R.drawable.cusor_color_bg_light)
           binding.txtsreach.setTextColor(Color.BLACK)
            binding.iconsreach.setImageResource(R.drawable.icon_sreach_light)
        }
    }
    private fun Find (){
        var stringsr= binding.txtsreach.text.toString()
        if(!stringsr.equals("")){
            val listconver=kotlin.collections.ArrayList<Conversion>()
            conversionlist.forEach { posts ->
                if(posts.chatcv_receive_name.lowercase(Locale.getDefault())
                        .contains(stringsr.lowercase(Locale.getDefault())) ){
                    listconver.add(posts)
                }
            }
            conversionAdapter = ConversionAdapter(
                listconver,
                this,
                requireContext(),
                SupportLibary.GetThemeType(requireContext())
            )
            binding.rclistconersion.adapter = conversionAdapter
        }
        binding.txtsreach.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(count==0){
                    conversionAdapter = ConversionAdapter(
                        conversionlist,
                        this@ConversionFragment,
                        requireContext(),
                        SupportLibary.GetThemeType(requireContext())
                    )
                    binding.rclistconersion.adapter = conversionAdapter
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    override fun StartChat(id: String, idreceiveUs:String) {
        SupportLibary.GetUserByID(idreceiveUs){
            users ->
            if (users==null){
                UsingPublic().ShowToast(requireContext(),"Hệ thống đang lỗi thử lại sau")
            }
            else{
                val intent = Intent(this.requireContext(), ChatActivity::class.java)
                intent.putExtra("user", users)
                intent.putExtra("idcv", id)
                startActivity(intent)
            }
        }
    }
}