package org.bmsk.topic.ui.topic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bmsk.topic.R
import org.bmsk.topic.adapter.NewsAdapter
import org.bmsk.topic.databinding.FragmentTopicBinding

@AndroidEntryPoint
class TopicFragment : Fragment() {

    private var _binding: FragmentTopicBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TopicViewModel by viewModels()
    private val newsAdapter = NewsAdapter { url ->
        navigateToWebFragment(url)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate<FragmentTopicBinding>(
            inflater,
            R.layout.fragment_topic,
            container,
            false
        ).apply {
            lifecycleOwner = this@TopicFragment.viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBinding()
        setupRecyclerView()
        setupSearchTextInputEditText()
        observeNewsList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun onChipClicked(view: View) {
        if (view is Chip) {
            binding.chipGroup.clearCheck()
            view.isChecked = true
            val section = ChipSection.getSectionByChipId(view.id)
            viewModel.fetchNews(section)
        }
    }

    private fun setupBinding() {
        with(binding) {
            fragment = this@TopicFragment
            binding.motionLayout.setTransition(R.id.hide, R.id.expand)
        }
    }

    private fun setupRecyclerView() {
        binding.newsRecyclerView.adapter = newsAdapter
        binding.newsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var isScrollingUp = false

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    // Scrolling Up
                    if (!isScrollingUp) {
                        isScrollingUp = true
                        binding.motionLayout.transitionToStart()
                    }
                } else {
                    // Scrolling down
                    if (isScrollingUp) {
                        isScrollingUp = false
                        binding.motionLayout.transitionToEnd()
                    }
                }
            }
        })
    }

    private fun setupSearchTextInputEditText() {
        binding.searchTextInputEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.chipGroup.clearCheck()
                binding.searchTextInputEditText.clearFocus()
                hideKeyboard(v)
                viewModel.fetchNewsSearchResults(binding.searchTextInputEditText.text.toString())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun observeNewsList() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newsStateFlow.collectLatest { newsList ->
                    newsAdapter.submitList(newsList)
                    binding.notFoundAnimationView.isVisible = newsList.isEmpty()

                    viewModel.fetchOpenGraphImage().collect {
                        newsAdapter.notifyItemChanged(it)
                    }
                }
            }
        }
    }

    private fun navigateToWebFragment(url: String) {
        val action = TopicFragmentDirections.actionTopicFragmentToWebViewFragment(url)
        findNavController().navigate(action)
    }
}