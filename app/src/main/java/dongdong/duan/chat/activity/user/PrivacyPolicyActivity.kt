package dongdong.duan.chat.activity.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import dongdong.duan.chat.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyActivity : AppCompatActivity() {
    lateinit var binding:ActivityPrivacyPolicyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val baseUrl = "https://policies.google.com/privacy"

        binding.mywebview.loadUrl(baseUrl)
        Setlistener()
    }

    private fun Setlistener() {
        binding.icbackpress.setOnClickListener {
            onBackPressed()
        }
    }
}