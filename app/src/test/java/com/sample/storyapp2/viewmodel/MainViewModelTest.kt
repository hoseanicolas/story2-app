package com.sample.storyapp2.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.sample.storyapp2.data.local.UserPreferences
import com.sample.storyapp2.data.remote.Story
import com.sample.storyapp2.data.repository.StoryRepository
import com.sample.storyapp2.ui.main.StoryAdapter
import com.sample.storyapp2.utils.DataDummy
import com.sample.storyapp2.utils.MainDispatcherRule
import com.sample.storyapp2.utils.PagedTestDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var userPreferences: UserPreferences

    private lateinit var mainViewModel: MainViewModel

    private val dummyToken = "dummy_token"

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(storyRepository, userPreferences)
    }

    @Test
    fun `when Get Stories Should Not Null and Return Success`() = runTest {
        val dummyStories = DataDummy.generateDummyStories()
        val data: PagingData<Story> = PagedTestDataSource.snapshot(dummyStories)
        val expectedStories = flowOf(data)

        Mockito.`when`(userPreferences.getToken()).thenReturn(flowOf(dummyToken))
        Mockito.`when`(storyRepository.getStoriesWithPaging(dummyToken)).thenReturn(expectedStories)

        val actualStories = mainViewModel.getStoriesWithPaging()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )

        val job = launch {
            actualStories.collect {
                differ.submitData(it)
            }
        }

        advanceUntilIdle()

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])

        job.cancel()
    }

    @Test
    fun `when Get Stories Should Return Correct Number of Data`() = runTest {
        val dummyStories = DataDummy.generateDummyStories()
        val data: PagingData<Story> = PagedTestDataSource.snapshot(dummyStories)
        val expectedStories = flowOf(data)

        Mockito.`when`(userPreferences.getToken()).thenReturn(flowOf(dummyToken))
        Mockito.`when`(storyRepository.getStoriesWithPaging(dummyToken)).thenReturn(expectedStories)

        val actualStories = mainViewModel.getStoriesWithPaging()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )

        val job = launch {
            actualStories.collect {
                differ.submitData(it)
            }
        }

        advanceUntilIdle()

        Assert.assertEquals(dummyStories.size, differ.snapshot().size)

        job.cancel()
    }

    @Test
    fun `when Get Stories Returns Empty Should Have Zero Size`() = runTest {
        val emptyStories = emptyList<Story>()
        val data: PagingData<Story> = PagedTestDataSource.snapshot(emptyStories)
        val expectedStories = flowOf(data)

        Mockito.`when`(userPreferences.getToken()).thenReturn(flowOf(dummyToken))
        Mockito.`when`(storyRepository.getStoriesWithPaging(dummyToken)).thenReturn(expectedStories)

        val actualStories = mainViewModel.getStoriesWithPaging()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )

        val job = launch {
            actualStories.collect {
                differ.submitData(it)
            }
        }

        advanceUntilIdle()

        Assert.assertEquals(0, differ.snapshot().size)

        job.cancel()
    }

    @Test
    fun `when Get Token Should Return Correct Token`() {
        val expectedToken = MutableLiveData<String>()
        expectedToken.value = dummyToken

        Mockito.`when`(userPreferences.getToken()).thenReturn(flowOf(dummyToken))

        val actualToken = mainViewModel.getToken()
        Assert.assertNotNull(actualToken)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}
