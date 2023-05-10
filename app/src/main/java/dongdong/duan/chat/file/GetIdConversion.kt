@file:Suppress("NAME_SHADOWING")

package dongdong.duan.chat.file

import android.annotation.SuppressLint
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dongdong.duan.chat.mode.Users
import dongdong.duan.chat.utility.Constant
import dongdong.duan.chat.utility.PreferenceManager
import java.util.*
import kotlin.collections.HashMap

@SuppressLint("StaticFieldLeak")
object GetIdConversion {
    private var convesionId: String = ""
    private val database:FirebaseFirestore= FirebaseFirestore.getInstance()
    private val reldatabase= FirebaseDatabase.getInstance()
    fun CheckConVerRemote(receiveId: String,conersion:Any, senderId: String, callback: (String?) -> Unit) {
        val chatListRef = reldatabase.getReference(Constant.RL_CHAT_LIST).child(senderId)
        chatListRef.orderByChild(Constant.CHATCV_RECEIVE_ID).equalTo(receiveId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val conversion = childSnapshot.getValue(HashMap::class.java)
                        if (conversion != null && conversion[Constant.CHATCV_RECEIVE_ID] == receiveId) {
                            // Conversation already exists, return the key
                            callback(childSnapshot.key)
                            return
                        }
                    }
                    // Conversation does not exist, create a new one
                    val newConversionRef = chatListRef.push()
                    val newConversionKey = newConversionRef.key
                    if (newConversionKey != null) {
                        newConversionRef.setValue(conersion)
                        callback(newConversionKey)
                    } else {
                        callback(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

     fun CheckConversation(receiveId:String, conversion: Any, senderId: String,reldatabase:FirebaseDatabase, callback: (String) -> Unit) {
        CheckConversationRemotely(senderId, conversion, receiveId) { conversationId ->
            if (conversationId != null) {
                callback(conversationId)
            } else {
                CheckConversationRemotely(receiveId, conversion, senderId) { conversationId ->
                    if (conversationId != null) {
                        callback(conversationId)
                    } else {
                        AddConversation(conversion) { conversationId ->
                            reldatabase.getReference(Constant.RL_CHAT_LIST).child(senderId).child(conversationId).setValue(conversion)
                            callback(conversationId)
                        }
                    }
                }
            }
        }
    }




    private fun CheckConversationRemotely(senderId: String, conversation:Any, receiveId: String, callback: (String?) -> Unit) {
        database.collection(Constant.CONVERSION_SYS)
            .whereEqualTo(Constant.CHATCV_SENDER_ID, senderId)
            .whereEqualTo(Constant.CHATCV_RECEIVE_ID, receiveId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null && !task.result!!.documents.isEmpty()) {
                    val snapshot = task.result!!.documents[0]
                    val conversationId = snapshot.id
                    callback(conversationId)
                } else {
                    callback(null)
                }
            }
    }

    private fun AddConversation(conversation: Any, callback: (String) -> Unit) {
        database.collection(Constant.CONVERSION_SYS)
            .add(conversation)
            .addOnSuccessListener { documentReference ->
                val conversationId = documentReference.id
                callback(conversationId)
            }
    }
    fun UpdateConversion(message: String,conversionId:String) {
        val documentReference: DocumentReference
        documentReference =
            database.collection(Constant.CONVERSION_SYS).document(conversionId)
        documentReference.update(
            Constant.CHATCV_LAST_MESSAGE,
            message,
            Constant.CHATCV_TIME, Date()
        )
    }




}













