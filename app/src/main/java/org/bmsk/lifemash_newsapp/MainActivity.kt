package org.bmsk.lifemash_newsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import org.bmsk.lifemash_newsapp.adapter.NewsAdapter
import org.bmsk.lifemash_newsapp.data.NewsService
import org.bmsk.lifemash_newsapp.data.model.NewsRss
import org.bmsk.lifemash_newsapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var newsAdapter: NewsAdapter

    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(
            TikXmlConverterFactory.create(
                TikXml.Builder().exceptionOnUnreadXml(false)    // 필요한 데이터만 골라서 매핑할 것이기 때문에 이 속성을 추가
                    .build()
            )
        ).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newsAdapter = NewsAdapter()

        binding.newsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
        }

        val newsService = retrofit.create(NewsService::class.java)
        newsService.getMainFeed().enqueue(object : Callback<NewsRss> {
            override fun onResponse(call: Call<NewsRss>, response: Response<NewsRss>) {
                Log.d("MainActivity", "${response.body()?.channel?.items}")

                newsAdapter.submitList(response.body()?.channel?.items.orEmpty())
            }

            override fun onFailure(call: Call<NewsRss>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }
}