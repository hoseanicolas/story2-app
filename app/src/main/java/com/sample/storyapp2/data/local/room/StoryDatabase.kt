package com.sample.storyapp2.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sample.storyapp2.data.local.entity.RemoteKeys
import com.sample.storyapp2.data.local.entity.StoryEntity

@Database(
    entities = [StoryEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        fun getDatabase(context: Context): StoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java,
                    "story_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
