package dongdong.duan.chat.activity.chat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dongdong.duan.chat.databinding.ActivityVideoCallBinding
import dongdong.duan.chat.mode.VideoCall

class VideoCallActivity : AppCompatActivity() {
    lateinit var binding:ActivityVideoCallBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CallUser()
    }

    private fun CallUser() {
        // Get the room ID from the intent
        val roomId = intent.getStringExtra("roomId")

        // Get a reference to the Firebase Realtime Database
        val database = FirebaseDatabase.getInstance().reference

        // Listen for changes to the video call status
        val videoCallListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val videoCall = dataSnapshot.getValue(VideoCall::class.java)

                // Check if the call has been accepted or rejected
                if (videoCall?.status == "accepted") {
                    // Start the video call
                    startVideoStream(roomId)
                } else if (videoCall?.status == "rejected") {
                    // Show a message that the call was rejected
                    Toast.makeText(this@VideoCallActivity, "The call was rejected", Toast.LENGTH_SHORT).show()

                    // Finish the activity
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        }

        if (roomId != null) {
            database.child("videoCalls").child(roomId).addValueEventListener(videoCallListener)
        }
    }

    private fun startVideoStream(roomId: String?) {

    }

}