package com.sample.storyapp2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.sample.storyapp2.data.local.UserPreferences
import com.sample.storyapp2.data.remote.Story
import com.sample.storyapp2.data.repository.StoryRepository
import com.sample.storyapp2.utils.Result
import kotlinx.coroutines.flow.first

class MapsViewModel(
    private val repository: StoryRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    suspend fun getStoriesWithLocation(): LiveData<Result<List<Story>>> {
        val token = userPreferences.getToken().first() ?: ""
        return repository.getStoriesWithLocation(token).asLiveData()
    }
}
