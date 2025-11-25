package com.sample.storyapp2.ui.auth.login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sample.storyapp2.R
import com.sample.storyapp2.databinding.ActivityLoginBinding
import com.sample.storyapp2.ui.auth.register.RegisterActivity
import com.sample.storyapp2.ui.main.MainActivity
import com.sample.storyapp2.utils.Result
import com.sample.storyapp2.viewmodel.AuthViewModel
import com.sample.storyapp2.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAnimation()
        setupAction()
    }

    private fun setupAnimation() {
        ObjectAnimator.ofFloat(binding.tvTitle, View.TRANSLATION_Y, -100f, 0f).apply {
            duration = 500
            start()
        }
        ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 0f, 1f).apply {
            duration = 500
            startDelay = 100
            start()
        }
        ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 0f, 1f).apply {
            duration = 500
            startDelay = 200
            start()
        }
        ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 0f, 1f).apply {
            duration = 500
            startDelay = 300
            start()
        }
    }

    private fun setupAction() {
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = getString(R.string.error_empty_email)
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.error = getString(R.string.error_empty_password)
                }
                binding.edLoginPassword.error != null -> {
                    return@setOnClickListener
                }
                else -> {
                    performLogin(email, password)
                }
            }
        }
    }

    private fun performLogin(email: String, password: String) {
        viewModel.login(email, password).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    val loginResult = result.data
                    viewModel.saveSession(loginResult.token, loginResult.userId, loginResult.name)
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
    }
}
