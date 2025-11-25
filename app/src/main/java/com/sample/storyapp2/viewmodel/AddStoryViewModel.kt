package com.sample.storyapp2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.sample.storyapp2.data.local.UserPreferences
import com.sample.storyapp2.data.repository.StoryRepository
import com.sample.storyapp2.utils.Result
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(
    private val repository: StoryRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    suspend fun uploadStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): LiveData<Result<String>> {
        val token = userPreferences.getToken().first() ?: ""
        return repository.uploadStory(token, file, description, lat, lon).asLiveData()
    }
}
