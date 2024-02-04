package org.bmsk.feature.topic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.bmsk.lifemash.feature.topic.databinding.ItemNewsBinding
import org.bmsk.model.NewsModel

class NewsAdapter(
    private val onClickItem: (String) -> Unit,
    private val onClickOption: (NewsModel) -> Unit,
) : ListAdapter<NewsModel, NewsAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NewsModel) {
            with(binding) {
                this.item = item
                root.setOnClickListener {
                    onClickItem(item.link)
                }
                moreOptionButton.setOnClickListener {
                    onClickOption(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<NewsModel>() {
            override fun areItemsTheSame(oldItem: NewsModel, newItem: NewsModel): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: NewsModel, newItem: NewsModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
