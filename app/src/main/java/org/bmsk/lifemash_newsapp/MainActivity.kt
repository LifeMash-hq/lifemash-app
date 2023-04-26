package org.bmsk.lifemash_newsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.bmsk.lifemash_newsapp.adapter.NewsAdapter
import org.bmsk.lifemash_newsapp.data.NewsService
import org.bmsk.lifemash_newsapp.data.model.NewsRss
import org.bmsk.lifemash_newsapp.data.model.transform
import org.bmsk.lifemash_newsapp.databinding.ActivityMainBinding
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var newsAdapter: NewsAdapter

    private val retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                TikXmlConverterFactory.create(
                    TikXml.Builder()
                        .exceptionOnUnreadXml(false)    // 필요한 데이터만 골라서 매핑할 것이기 때문에 이 속성을 추가
                        .build()
                )
            )
            .client(OkHttpClient.Builder().apply {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }.build())
            .build()
    private val newsService = retrofit.create(NewsService::class.java)

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

            newsService.getNews(SECTION_ECONOMICS).submitList()
        }

        binding.politicsChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.politicsChip.isChecked = true

            newsService.getNews(SECTION_POLITICS).submitList()
        }

        binding.socialChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.socialChip.isChecked = true

            newsService.getNews(SECTION_SOCIAL).submitList()
        }

        binding.lifeCultureChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.lifeCultureChip.isChecked = true

            newsService.getNews(SECTION_LIFE_CULTURE).submitList()
        }

        binding.internationalGlobalChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.internationalGlobalChip.isChecked = true

            newsService.getNews(SECTION_INTERNATIONAL_GLOBAL).submitList()
        }

        binding.entertainmentBroadcastChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.entertainmentBroadcastChip.isChecked = true

            newsService.getNews(SECTION_ENTERTAINMENT_BROADCAST).submitList()
        }

        binding.sportChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.sportChip.isChecked = true

            newsService.getNews(SECTION_SPORT).submitList()
        }

        binding.economyChip.isChecked = true
        newsService.getNews().submitList()
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