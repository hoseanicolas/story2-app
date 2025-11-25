package com.sample.storyapp2.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sample.storyapp2.data.local.UserPreferences
import com.sample.storyapp2.data.local.room.StoryDatabase
import com.sample.storyapp2.data.remote.ApiConfig
import com.sample.storyapp2.data.repository.StoryRepository

class ViewModelFactory private constructor(
    private val repository: StoryRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(repository, userPreferences) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository, userPreferences) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(repository, userPreferences) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(repository, userPreferences) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val database = StoryDatabase.getDatabase(context)
                val apiService = ApiConfig.getApiService()
                val repository = StoryRepository(apiService, database)
                val userPreferences = UserPreferences.getInstance(context)
                ViewModelFactory(repository, userPreferences).also {
                    INSTANCE = it
                }
            }
        }
    }
}
