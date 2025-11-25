package com.sample.storyapp2.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.sample.storyapp2.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra(EXTRA_STORY_NAME)
        val description = intent.getStringExtra(EXTRA_STORY_DESCRIPTION)
        val photoUrl = intent.getStringExtra(EXTRA_STORY_PHOTO_URL)

        binding.tvDetailName.text = name
        binding.tvDetailDescription.text = description

        Glide.with(this)
            .load(photoUrl)
            .into(binding.ivDetailPhoto)
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
        const val EXTRA_STORY_NAME = "extra_story_name"
        const val EXTRA_STORY_DESCRIPTION = "extra_story_description"
        const val EXTRA_STORY_PHOTO_URL = "extra_story_photo_url"
    }
}
