package com.sample.storyapp2.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sample.storyapp2.databinding.ActivitySplashBinding
import com.sample.storyapp2.ui.auth.login.LoginActivity
import com.sample.storyapp2.ui.main.MainActivity
import com.sample.storyapp2.viewmodel.MainViewModel
import com.sample.storyapp2.viewmodel.ViewModelFactory
import kotlinx.coroutines.runBlocking

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            checkSession()
        }, 2000)
    }

    private fun checkSession() {
        runBlocking {
            val token = viewModel.getToken().value
            val intent = if (!token.isNullOrEmpty()) {
                Intent(this@SplashActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashActivity, LoginActivity::class.java)
            }
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
