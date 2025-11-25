package com.sample.storyapp2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sample.storyapp2.data.local.UserPreferences
import com.sample.storyapp2.data.remote.Story
import com.sample.storyapp2.data.repository.StoryRepository
import com.sample.storyapp2.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: StoryRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    suspend fun getStoriesWithPaging(): Flow<PagingData<Story>> {
        val token = userPreferences.getToken().first() ?: ""
        return repository.getStoriesWithPaging(token).cachedIn(viewModelScope)
    }

    suspend fun getStories(): LiveData<Result<List<Story>>> {
        val token = userPreferences.getToken().first() ?: ""
        return repository.getStories(token).asLiveData()
    }

    fun getToken(): LiveData<String?> {
        return userPreferences.getToken().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.clearSession()
        }
    }
}
