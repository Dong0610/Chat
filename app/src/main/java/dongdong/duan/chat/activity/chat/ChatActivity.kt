package dongdong.duan.chat.activity.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dongdong.duan.chat.BaseActivity
import dongdong.duan.chat.R
import dongdong.duan.chat.adapter.ChatAdapter
import dongdong.duan.chat.databinding.ActivityChatBinding
import dongdong.duan.chat.file.BitmapUtils
import dongdong.duan.chat.file.GetIdConversion
import dongdong.duan.chat.file.SupportLibary
import dongdong.duan.chat.listener.ChatListener
import dongdong.duan.chat.mode.Chat
import dongdong.duan.chat.mode.Users
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.PreferenceManager
import dongdong.duan.chat.utility.UsingPublic
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class ChatActivity : BaseActivity(), ChatListener {

    lateinit var binding: ActivityChatBinding
    override lateinit var preferenceManager: PreferenceManager
    lateinit var usersreceive: Users
    var convesionId: String? = null
    var useravaiable = 0
    var uriImg: Uri? = null
    override val database = FirebaseFirestore.getInstance()
    var messlist = kotlin.collections.ArrayList<Chat>()
    lateinit var chatAdapter: ChatAdapter
    var recvusetoken = ""
    var reldatabase = FirebaseDatabase.getInstance()
    private val refrence: FirebaseStorage = FirebaseStorage.getInstance()
    private val REQUEST_CODE_IMAGE = 456
    var imageclick = 0

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        usersreceive = intent.getSerializableExtra("user") as Users
        convesionId = intent.getStringExtra("idcv");
        GetTokenUser(usersreceive.USER_ID) {
            recvusetoken = it
            usersreceive.USER_TOKEN = it
        }
        LoadEvenClick()
        LoadUserFinfo()
        val theme = SupportLibary.GetThemeType(applicationContext)
        LoadThemeType(theme)
        ListenerMessage()
        ListenerOnline()
        chatAdapter = ChatAdapter(
            messlist,
            preferenceManager.GetString(Constant.USER_ID).toString(),
            SupportLibary.GetThemeType(applicationContext),
            applicationContext,
            usersreceive.USER_IMAGE, this
        )
        binding.rcvmessageview.adapter = chatAdapter;
    }

    fun GetTokenUser(userID: String, calback: (String) -> Unit) {
        database.collection(Constant.SYS_USERS)
            .document(usersreceive.USER_ID)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result.exists()) {
                    calback(task.result.getString(Constant.USER_TOKEN).toString())
                } else {
                    calback("")
                }
            }
            .addOnFailureListener {
                calback("")
            }
    }

    private fun ListenerOnline() {
        database.collection(Constant.SYS_USERS)
            .document(usersreceive.USER_ID)
            .addSnapshotListener(this@ChatActivity) { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    if (value.getLong(Constant.USER_ONLINE) != null) {
                        val avaiable: Int = Objects.requireNonNull(
                            value.getLong(Constant.USER_ONLINE)
                        )?.toInt() ?: 1
                        useravaiable = avaiable
                    } else {
                        UsingPublic().ShowToast(applicationContext, "This is null")
                        useravaiable = 0
                    }
                } else {
                    UsingPublic().ShowToast(applicationContext, "Can not get User")
                }
                binding.txtonline.text = if (useravaiable == 1) "Online" else "Offline"
            }
    }

    val valueEventListener = object : ValueEventListener {
        @SuppressLint("NotifyDataSetChanged")
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            messlist = ArrayList<Chat>()
            for (chatSnapshot in dataSnapshot.children) {
                val chat = Chat().apply {
                    CHAT_ID = chatSnapshot.key.toString()
                    CHAT_SENDER_ID =
                        chatSnapshot.child(Constant.CHAT_SENDER_ID).value.toString()
                    CHAT_RECEIVE_ID =
                        chatSnapshot.child(Constant.CHAT_RECEIVE_ID).value.toString()
                    CHAT_CONTENT = chatSnapshot.child(Constant.CHAT_CONTENT).value.toString()
                    CHAT_TYPE = chatSnapshot.child(Constant.CHAT_TYPE).value.toString()
                    CHAT_IMGURL = chatSnapshot.child(Constant.CHAT_IMGURL).value.toString()
                    CHAT_DATE = chatSnapshot.child(Constant.CHAT_TIME)
                        .getValue(Date::class.java) as Date
                    CHAT_TIME = UsingPublic().GetRealDateTime(
                        chatSnapshot.child(Constant.CHAT_TIME)
                            .getValue(Date::class.java) as Date
                    ).toString()
                }
                messlist.add(chat)
            }
            if (messlist.size == 0) {
                chatAdapter.notifyDataSetChanged()
            } else {
                chatAdapter.notifyItemRangeInserted(messlist.size, messlist.size)
                binding.rcvmessageview.smoothScrollToPosition(messlist.size - 1)
                chatAdapter = ChatAdapter(
                    messlist,
                    preferenceManager.GetString(Constant.USER_ID).toString(),
                    SupportLibary.GetThemeType(applicationContext),
                    applicationContext,
                    usersreceive.USER_IMAGE, this@ChatActivity
                )
                binding.rcvmessageview.adapter = chatAdapter;
            }
        }

        override fun onCancelled(error: DatabaseError) {
            UsingPublic().ShowToast(applicationContext, error.message)
        }
    }

    private fun ListenerMessage() {
        val database =
            FirebaseDatabase.getInstance().getReference(Constant.SYS_CHAT + "/" + convesionId)
        database.addValueEventListener(valueEventListener)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun SendMess(ulr: String?) {
        val mess = HashMap<String, Any?>()
        var txtMessage = binding.edtinputmess.text.toString()

        if (imageclick == 1) {
            mess[Constant.CHAT_SENDER_ID] = preferenceManager.GetString(Constant.USER_ID)
            mess[Constant.CHAT_RECEIVE_ID] = usersreceive.USER_ID
            mess[Constant.CHAT_CONTENT] = "Đã gửi ảnh"
            mess[Constant.CHAT_IMGURL] = ulr;
            mess[Constant.CHAT_TYPE] = 1
            mess[Constant.CHAT_TIME] = Date()
        } else {
            mess[Constant.CHAT_SENDER_ID] = preferenceManager.GetString(Constant.USER_ID)
            mess[Constant.CHAT_RECEIVE_ID] = usersreceive.USER_ID
            mess[Constant.CHAT_CONTENT] = txtMessage.trim()
            mess[Constant.CHAT_IMGURL] = "";
            mess[Constant.CHAT_TYPE] = 0
            mess[Constant.CHAT_TIME] = Date()
        }
        reldatabase.getReference(Constant.SYS_CHAT).child(convesionId.toString()).push()
            .setValue(mess)
        GetIdConversion.UpdateConversion(
            binding.edtinputmess.text.toString(),
            convesionId.toString()
        )
        binding.edtinputmess.setText("");
        binding.layoutimg.visibility = View.VISIBLE
        binding.icright.visibility = View.GONE
        imageclick = 0
        binding.layoytchooseimg.visibility = View.GONE

    }

    private fun LoadUserFinfo() {
        database.collection(Constant.CONVERSION_SYS)
            .document(convesionId.toString()).get()
            .addOnCompleteListener {
                binding.txtusername.text =
                    it.result.getString(Constant.CHATCV_RECEIVE_NAME).toString()
                usersreceive.USER_NAME =
                    it.result.getString(Constant.CHATCV_RECEIVE_NAME).toString()
            }
        binding.imgUsers.setImageBitmap(BitmapUtils.StringToBitmap(usersreceive.USER_IMAGE))
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imageclick = 1
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                try {
                    uriImg = data.data!!
                    binding.layoytchooseimg.visibility = View.VISIBLE
                    binding.imgchoose.setImageURI(uriImg)

                } catch (ex: Exception) {
                    ex.printStackTrace()
                    SupportLibary.ShowToast(applicationContext, ex.message)
                }
            }
        }
    }

    private fun LoadEvenClick() {
        binding.iccallusing.setOnClickListener {
            if (recvusetoken == "") {
                UsingPublic().ShowToast(applicationContext, "User not available")
            } else {
                var intent = Intent(applicationContext, InvitationOutGoingActivity::class.java)
                intent.putExtra("user", usersreceive);
                startActivity(intent)
            }
        }
        binding.iccameracap.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "dongdong.duan.chat",
                    it
                )
                uriImg = photoURI
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE)
            }
        }
        binding.icimgsettingchat.setOnClickListener {
            var it = Intent(applicationContext, ChatSettingActivity::class.java)
            it.putExtra("userreceive", usersreceive)
            it.putExtra("idcv", convesionId)
            startActivity(it)
        }

        binding.icimagech.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(
                Intent.createChooser(intent, "Select Images"),
                REQUEST_CODE_IMAGE
            )
        }
        binding.icsendmess.setOnClickListener {
            if (uriImg != null) {
                val fileRef: StorageReference = refrence.reference.child(
                    System.currentTimeMillis().toString() + "." + FileExtension(uriImg!!)
                )
                fileRef.putFile(uriImg!!).addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        SendMess(uri.toString())
                    }.addOnFailureListener { ex ->
                        UsingPublic().ShowToast(applicationContext, ex.message)
                    }
                }
                    .addOnFailureListener { ex ->
                        UsingPublic().ShowToast(applicationContext, ex.message)
                    }
            } else {
                SendMess(null)
            }
        }

        val handler = Handler()
        binding.edtinputmess.setOnClickListener {
            handler.postDelayed({
                binding.layoutimg.visibility = View.GONE
                binding.icright.visibility = View.VISIBLE
            }, 200)
        }
        binding.edtinputmess.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                return@setOnEditorActionListener true
            }
            false
        }
        binding.edtinputmess.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                handler.postDelayed({
                    binding.layoutimg.visibility = View.GONE
                    binding.icright.visibility = View.VISIBLE
                }, 200)
            }
        }
        binding.icright.setOnClickListener {
            binding.layoutimg.visibility = View.VISIBLE
            binding.icright.visibility = View.GONE
        }
        binding.icbackpress.setOnClickListener {
            onBackPressed()
        }
    }

    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            val currentPhotoPath = absolutePath
        }
    }


    private fun FileExtension(uriImg: Uri): Any? {
        val cr = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(uriImg))
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun LoadThemeType(viewtheme: Int) {
        if (viewtheme.toString().equals("1")) {
            binding.icbackpress.setImageResource(R.drawable.ic_baseline_arrow_back_light)
            binding.txtusername.setTextColor(Color.BLACK)
            binding.edtinputmess.background = getDrawable(R.drawable.bg_edt_ip_mess_light)
            binding.layoutedt.background = getDrawable(R.drawable.bg_input_chatmess_light)
            binding.maugiaodien.setBackgroundColor(Color.WHITE)
            binding.icimagech.setImageResource(R.drawable.ic_image_baseline_white)
            binding.edtinputmess.setTextColor(Color.BLACK)
            binding.iccallusing.setImageResource(R.drawable.baseline_video_call_24)
            binding.edtinputmess.setTextCursorDrawable(R.drawable.cusor_color_bg_light)
            binding.icimgsettingchat.setImageResource(R.drawable.ic_tree_baseline_black)
        }
    }

    override fun ShowImage(url: String) {
        var intent = Intent(applicationContext, ImageShow::class.java)
        intent.putExtra("urlimg", url)
        intent.putExtra("name", usersreceive.USER_NAME)
        startActivity(intent)
    }

}