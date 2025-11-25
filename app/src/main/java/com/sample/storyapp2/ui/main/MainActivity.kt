package com.sample.storyapp2.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.storyapp2.R
import com.sample.storyapp2.databinding.ActivityMainBinding
import com.sample.storyapp2.ui.addstory.AddStoryActivity
import com.sample.storyapp2.ui.auth.login.LoginActivity
import com.sample.storyapp2.viewmodel.MainViewModel
import com.sample.storyapp2.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        checkSession()
        setupRecyclerView()
        setupAction()
        setupBackPressHandler()
    }

    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }

    private fun checkSession() {
        viewModel.getToken().observe(this) { token ->
            if (token.isNullOrEmpty()) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter()
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { storyAdapter.retry() }
            )
        }

        storyAdapter.addLoadStateListener { loadState ->
            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            binding.rvStories.isVisible = loadState.source.refresh is LoadState.NotLoading
            binding.tvEmpty.isVisible = loadState.source.refresh is LoadState.NotLoading && storyAdapter.itemCount == 0

            val errorState = loadState.source.refresh as? LoadState.Error
                ?: loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(this@MainActivity, it.error.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupAction() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

        binding.swipeRefresh.setOnRefreshListener {
            storyAdapter.refresh()
            binding.swipeRefresh.isRefreshing = false
        }

        loadStories()
    }

    override fun onResume() {
        super.onResume()
        storyAdapter.refresh()
    }

    private fun loadStories() {
        lifecycleScope.launch {
            viewModel.getStoriesWithPaging().collectLatest { pagingData ->
                storyAdapter.submitData(pagingData)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_maps -> {
                startActivity(Intent(this, com.sample.storyapp2.ui.maps.MapsActivity::class.java))
                true
            }
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.logout_confirm))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.logout()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }
}