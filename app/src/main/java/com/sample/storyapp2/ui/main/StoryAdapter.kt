package com.sample.storyapp2.ui.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sample.storyapp2.data.remote.Story
import com.sample.storyapp2.databinding.ItemStoryBinding
import com.sample.storyapp2.ui.detail.DetailActivity

class StoryAdapter : PagingDataAdapter<Story, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        story?.let { holder.bind(it) }
    }

    class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            binding.tvItemName.text = story.name
            binding.tvItemDescription.text = story.description

            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.ivItemPhoto)

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_STORY_ID, story.id)
                intent.putExtra(DetailActivity.EXTRA_STORY_NAME, story.name)
                intent.putExtra(DetailActivity.EXTRA_STORY_DESCRIPTION, story.description)
                intent.putExtra(DetailActivity.EXTRA_STORY_PHOTO_URL, story.photoUrl)

                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    binding.root.context as Activity,
                    Pair(binding.ivItemPhoto, "photo"),
                    Pair(binding.tvItemName, "name")
                )

                binding.root.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }
}
