package org.bmsk.lifemash_newsapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import org.bmsk.lifemash_newsapp.Retrofits.googleRetrofit
import org.bmsk.lifemash_newsapp.Retrofits.sbsRetrofit
import org.bmsk.lifemash_newsapp.adapter.NewsAdapter
import org.bmsk.lifemash_newsapp.data.GoogleNewsService
import org.bmsk.lifemash_newsapp.data.SbsNewsService
import org.bmsk.lifemash_newsapp.data.model.NewsRss
import org.bmsk.lifemash_newsapp.data.model.transform
import org.bmsk.lifemash_newsapp.databinding.ActivityMainBinding
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var newsAdapter: NewsAdapter

    private val sbsNewsService = sbsRetrofit.create(SbsNewsService::class.java)
    private val googleNewsService = googleRetrofit.create(GoogleNewsService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newsAdapter = NewsAdapter()

        binding.newsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
        }

        binding.economyChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.economyChip.isChecked = true

            sbsNewsService.getNews(SECTION_ECONOMICS).submitList()
        }

        binding.politicsChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.politicsChip.isChecked = true

            sbsNewsService.getNews(SECTION_POLITICS).submitList()
        }

        binding.socialChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.socialChip.isChecked = true

            sbsNewsService.getNews(SECTION_SOCIAL).submitList()
        }

        binding.lifeCultureChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.lifeCultureChip.isChecked = true

            sbsNewsService.getNews(SECTION_LIFE_CULTURE).submitList()
        }

        binding.internationalGlobalChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.internationalGlobalChip.isChecked = true

            sbsNewsService.getNews(SECTION_INTERNATIONAL_GLOBAL).submitList()
        }

        binding.entertainmentBroadcastChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.entertainmentBroadcastChip.isChecked = true

            sbsNewsService.getNews(SECTION_ENTERTAINMENT_BROADCAST).submitList()
        }

        binding.sportChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.sportChip.isChecked = true

            sbsNewsService.getNews(SECTION_SPORT).submitList()
        }

        // v: View, actionId: 어떤 액션,
        binding.searchTextInputEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.chipGroup.clearCheck()

                binding.searchTextInputEditText.clearFocus()

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)

                googleNewsService.search(binding.searchTextInputEditText.text.toString()).submitList()

                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.economyChip.isChecked = true
        sbsNewsService.getNews().submitList()
    }

    private fun Call<NewsRss>.submitList() {
        enqueue(object : Callback<NewsRss> {
            override fun onResponse(call: Call<NewsRss>, response: Response<NewsRss>) {
                Log.d("MainActivity", "${response.body()?.channel?.items}")

                val list = response.body()?.channel?.items.orEmpty().transform()
                newsAdapter.submitList(list)

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
                Log.d("MainActivity", "FAIL")
                t.printStackTrace()
            }
        })
    }
}