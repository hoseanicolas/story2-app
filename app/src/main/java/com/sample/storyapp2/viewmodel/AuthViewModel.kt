package com.sample.storyapp2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sample.storyapp2.data.local.UserPreferences
import com.sample.storyapp2.data.remote.LoginResult
import com.sample.storyapp2.data.repository.StoryRepository
import com.sample.storyapp2.utils.Result
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: StoryRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    fun register(name: String, email: String, password: String): LiveData<Result<String>> {
        return repository.register(name, email, password).asLiveData()
    }

    fun login(email: String, password: String): LiveData<Result<LoginResult>> {
        return repository.login(email, password).asLiveData()
    }

    fun saveSession(token: String, userId: String, name: String) {
        viewModelScope.launch {
            userPreferences.saveSession(token, userId, name)
        }
    }
}