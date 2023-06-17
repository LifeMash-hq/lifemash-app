package org.bmsk.lifemash_newsapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.bmsk.lifemash_newsapp.adapter.NewsAdapter
import org.bmsk.lifemash_newsapp.databinding.ActivityMainBinding
import org.bmsk.lifemash_newsapp.ui.WebViewActivity
import org.bmsk.model.section.SbsSection.SECTION_ECONOMICS
import org.bmsk.model.section.SbsSection.SECTION_ENTERTAINMENT_BROADCAST
import org.bmsk.model.section.SbsSection.SECTION_INTERNATIONAL_GLOBAL
import org.bmsk.model.section.SbsSection.SECTION_LIFE_CULTURE
import org.bmsk.model.section.SbsSection.SECTION_POLITICS
import org.bmsk.model.section.SbsSection.SECTION_SOCIAL
import org.bmsk.model.section.SbsSection.SECTION_SPORT
import org.bmsk.network.model.NewsRss
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var newsAdapter: NewsAdapter
    private val viewModel: MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            lifecycleOwner = this@MainActivity
        }

        newsAdapter = NewsAdapter { url ->
            startActivity(
                Intent(this, WebViewActivity::class.java).apply {
                    putExtra(PUT_EXTRA_KEY_URL, url)
                }
            )
        }

        binding.newsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
        }

        setupChipListeners()

        binding.searchTextInputEditText.setOnEditorActionListener { v, actionId, event ->
            handleEditorAction(v, actionId)
        }

        binding.economyChip.isChecked = true
        sbsNewsService.getNews().submitList()
    }

    private fun setupChipListeners() {
        val chipSectionMap = mapOf(
            binding.economyChip to SECTION_ECONOMICS,
            binding.politicsChip to SECTION_POLITICS,
            binding.socialChip to SECTION_SOCIAL,
            binding.lifeCultureChip to SECTION_LIFE_CULTURE,
            binding.internationalGlobalChip to SECTION_INTERNATIONAL_GLOBAL,
            binding.entertainmentBroadcastChip to SECTION_ENTERTAINMENT_BROADCAST,
            binding.sportChip to SECTION_SPORT
        )

        for ((chip, section) in chipSectionMap) {
            chip.setOnClickListener {
                binding.chipGroup.clearCheck()
                chip.isChecked = true

                sbsNewsService.getNews(section).submitList()
            }
        }
    }

    private fun handleEditorAction(v: android.view.View, actionId: Int): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            binding.chipGroup.clearCheck()

            binding.searchTextInputEditText.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)

            googleNewsService.search(binding.searchTextInputEditText.text.toString())
                .submitList()

            return true
        }
        return false
    }

    private fun Call<NewsRss>.submitList() {
        enqueue(object : Callback<NewsRss> {
            override fun onResponse(call: Call<NewsRss>, response: Response<NewsRss>) {

                val list = response.body()?.channel?.items.orEmpty().transform()
                newsAdapter.submitList(list)

                binding.notFoundAnimationView.isVisible = list.isEmpty()

                list.forEachIndexed { index, news ->
                    Thread {
                        try {
                            val jsoup = Jsoup.connect(news.link).get()
                            val elements = jsoup.select("meta[property^=og:]")
                            val ogImageNode = elements.find { node ->
                                node.attr("property") == "og:image"
                            }

                            news.imageUrl = ogImageNode?.attr("content")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        runOnUiThread {
                            newsAdapter.notifyItemChanged(index)
                        }
                    }.start()
                }
            }

            override fun onFailure(call: Call<NewsRss>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}