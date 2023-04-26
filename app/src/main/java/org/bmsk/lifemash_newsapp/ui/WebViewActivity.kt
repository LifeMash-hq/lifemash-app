package org.bmsk.lifemash_newsapp.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import android.widget.Toast
import org.bmsk.lifemash_newsapp.PUT_EXTRA_KEY_URL
import org.bmsk.lifemash_newsapp.R
import org.bmsk.lifemash_newsapp.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra(PUT_EXTRA_KEY_URL)
        binding.newsWebView.webViewClient = WebViewClient()
        binding.newsWebView.settings.javaScriptEnabled = true

        if(url.isNullOrEmpty()) {
            Toast.makeText(this, R.string.invalid_url, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            binding.newsWebView.loadUrl(url)
        }
    }
}