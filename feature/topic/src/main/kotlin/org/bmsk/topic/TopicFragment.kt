package org.bmsk.topic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bmsk.lifemash_newsapp.databinding.FragmentTopicBinding
import org.bmsk.model.section.SbsSection
import org.bmsk.topic.adapter.NewsAdapter

@AndroidEntryPoint
class TopicFragment : Fragment() {

    private var _binding: FragmentTopicBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TopicViewModel by viewModels()
    private val newsAdapter = NewsAdapter { url ->
        findNavController().navigate(
            TopicFragmentDirections
                .actionTopicFragmentToWebViewFragment(url)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopicBinding.inflate(inflater).apply {
            lifecycleOwner = this@TopicFragment.viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newsRecyclerView.adapter = newsAdapter
        setupChipListeners()
        observeNewsList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupChipListeners() {
        val chipSectionMap = mapOf(
            binding.economyChip to SbsSection.SECTION_ECONOMICS,
            binding.politicsChip to SbsSection.SECTION_POLITICS,
            binding.socialChip to SbsSection.SECTION_SOCIAL,
            binding.lifeCultureChip to SbsSection.SECTION_LIFE_CULTURE,
            binding.internationalGlobalChip to SbsSection.SECTION_INTERNATIONAL_GLOBAL,
            binding.entertainmentBroadcastChip to SbsSection.SECTION_ENTERTAINMENT_BROADCAST,
            binding.sportChip to SbsSection.SECTION_SPORT
        )

        for ((chip, section) in chipSectionMap) {
            chip.setOnClickListener {
                binding.chipGroup.clearCheck()
                chip.isChecked = true

                viewModel.getNews(section)
            }
        }
    }

    private fun observeNewsList() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newsStateFlow.collectLatest { newsList ->
                    newsAdapter.submitList(newsList)
                    binding.notFoundAnimationView.isVisible = newsList.isEmpty()

//                    newsList.forEachIndexed { index, news ->
//                        withContext(Dispatchers.IO) {
//                            val jsoup = Jsoup.connect(news.link).get()
//                            val elements = jsoup.select("meta[property^=og:]")
//                            val ogImageNode = elements.find { node ->
//                                node.attr("property") == "og:image"
//                            }
//                            news.imageUrl = ogImageNode?.attr("content")
//
//                            withContext(Dispatchers.Main) {
//                                newsAdapter.notifyItemChanged(index)
//                            }
//                        }
//                    }
                }
            }
        }
    }
}