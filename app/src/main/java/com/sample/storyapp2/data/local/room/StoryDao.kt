package com.sample.storyapp2.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sample.storyapp2.data.local.entity.StoryEntity

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Query("SELECT * FROM story ORDER BY createdAt DESC")
    fun getAllStories(): PagingSource<Int, StoryEntity>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}
