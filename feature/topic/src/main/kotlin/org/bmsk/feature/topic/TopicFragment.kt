package org.bmsk.feature.topic

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.bmsk.lifemash.feature.topic.R
import org.bmsk.lifemash.feature.topic.databinding.FragmentTopicBinding
import org.bmsk.core.model.NewsModel
import org.bmsk.feature.topic.adapter.NewsAdapter

@AndroidEntryPoint
class TopicFragment : Fragment() {

    private var _binding: FragmentTopicBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TopicViewModel by viewModels()
    private val newsAdapter = NewsAdapter(::navigateToWebFragment, ::expandBottomOption)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTopicBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBinding()
        setupRecyclerView()
        observeViewModel()
        setupSearchTextInputEditText()
        setupMotionLayoutTransitions()
        setupClickListeners()
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
        }
    }

    private fun setupMotionLayoutTransitions() {
        binding.motionLayout.apply {
            setTransition(R.id.hide, R.id.expand)
            setTransition(R.id.optionHide, R.id.optionExpand)
        }
    }

    private fun transitionToState(stateId: Int) {
        binding.motionLayout.transitionToState(stateId)
    }

    private fun setupClickListeners() {
        binding.root.setOnClickListener {
            if (binding.motionLayout.currentState == R.id.optionExpand) {
                transitionToState(R.id.optionHide)
            }
        }

        binding.bottomOptionLayout.setOnClickListener { }
    }

    private fun setupRecyclerView() {
        binding.newsRecyclerView.apply {
            adapter = newsAdapter
            addOnScrollListener(createScrollListener())
        }
    }

    private fun createScrollListener() = object : RecyclerView.OnScrollListener() {
        var isScrollingUp = false

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (binding.motionLayout.currentState == R.id.optionExpand) {
                transitionToState(R.id.optionHide)
                binding.motionLayout.setTransition(R.id.hide, R.id.expand)
            }

            if (dy > 0 && !isScrollingUp) {
                isScrollingUp = true
                transitionToState(R.id.hide)
            } else if (dy <= 0 && isScrollingUp) {
                isScrollingUp = false
                transitionToState(R.id.expand)
            }
        }
    }

    private fun setupSearchTextInputEditText() {
        binding.searchTextInputEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                processSearchAction(v)
                true
            } else {
                false
            }
        }
    }

    private fun processSearchAction(v: View) {
        binding.chipGroup.clearCheck()
        binding.searchTextInputEditText.clearFocus()
        hideKeyboard(v)
        viewModel.fetchNewsSearchResults(binding.searchTextInputEditText.text.toString())
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun expandBottomOption(newsItem: NewsModel) {
        transitionToState(R.id.optionExpand)
        binding.bookmarkButton.setOnClickListener {
            Log.e("TopicFragment", "bookmarkButton")
            viewModel.bookmark(newsItem)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.newsStateFlow.collect { newsList ->
                    newsAdapter.submitList(newsList)
                    binding.notFoundAnimationView.isVisible = newsList.isEmpty()
                }
            }
            launch {
                viewModel.newsImageLoadingFlow.collect { index ->
                    newsAdapter.notifyItemChanged(index)
                }
            }
        }
    }

    private fun navigateToWebFragment(url: String) {
        val action = TopicFragmentDirections.actionTopicFragmentToWebViewFragment(url)
        findNavController().navigate(action)
    }
}
