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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra(PUT_EXTRA_KEY_URL)
        if (url.isNullOrEmpty()) {
            showToastAndFinish()
        } else {
            setupWebView(url)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(url: String) {
        binding.newsWebView.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl(url)
        }
    }

    private fun showToastAndFinish() {
        Toast.makeText(this, R.string.invalid_url, Toast.LENGTH_SHORT).show()
        finish()
    }
}