package com.sample.storyapp2.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sample.storyapp2.data.local.room.StoryDatabase
import com.sample.storyapp2.data.paging.StoryRemoteMediator
import com.sample.storyapp2.data.remote.ApiService
import com.sample.storyapp2.data.remote.Story
import com.sample.storyapp2.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getStoriesWithPaging(token: String): Flow<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                Story(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    photoUrl = entity.photoUrl,
                    createdAt = entity.createdAt,
                    lat = entity.lat,
                    lon = entity.lon
                )
            }
        }
    }

    fun register(name: String, email: String, password: String) = flow {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (!body.error) {
                    emit(Result.Success(body.message))
                } else {
                    emit(Result.Error(body.message))
                }
            } else {
                emit(Result.Error(response.message() ?: "Unknown error occurred"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Network error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    fun login(email: String, password: String) = flow {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (!body.error && body.loginResult != null) {
                    emit(Result.Success(body.loginResult))
                } else {
                    emit(Result.Error(body.message))
                }
            } else {
                emit(Result.Error(response.message() ?: "Unknown error occurred"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Network error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    fun getStories(token: String) = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getStories("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (!body.error) {
                    emit(Result.Success(body.listStory))
                } else {
                    emit(Result.Error(body.message))
                }
            } else {
                emit(Result.Error(response.message() ?: "Unknown error occurred"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Network error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ) = flow {
        emit(Result.Loading)
        try {
            val response = apiService.uploadStory("Bearer $token", file, description, lat, lon)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (!body.error) {
                    emit(Result.Success(body.message))
                } else {
                    emit(Result.Error(body.message))
                }
            } else {
                emit(Result.Error(response.message() ?: "Unknown error occurred"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Network error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    fun getStoriesWithLocation(token: String) = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getStories("Bearer $token", location = 1)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (!body.error) {
                    emit(Result.Success(body.listStory))
                } else {
                    emit(Result.Error(body.message))
                }
            } else {
                emit(Result.Error(response.message() ?: "Unknown error occurred"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Network error occurred"))
        }
    }.flowOn(Dispatchers.IO)
}
