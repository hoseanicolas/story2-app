package com.sample.storyapp2.utils

import com.sample.storyapp2.data.remote.Story

object DataDummy {
    fun generateDummyStories(): List<Story> {
        val items = ArrayList<Story>()
        for (i in 1..20) {
            val story = Story(
                id = "story-$i",
                name = "User $i",
                description = "This is description for story $i",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-$i.jpg",
                createdAt = "2024-01-${i.toString().padStart(2, '0')}T10:00:00.000Z",
                lat = if (i % 2 == 0) -6.2 + (i * 0.01) else null,
                lon = if (i % 2 == 0) 106.8 + (i * 0.01) else null
            )
            items.add(story)
        }
        return items
    }
}
