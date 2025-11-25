package com.sample.storyapp2.ui.auth.register

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sample.storyapp2.R
import com.sample.storyapp2.databinding.ActivityRegisterBinding
import com.sample.storyapp2.utils.Result
import com.sample.storyapp2.viewmodel.AuthViewModel
import com.sample.storyapp2.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAnimation()
        setupAction()
    }

    private fun setupAnimation() {
        ObjectAnimator.ofFloat(binding.tvTitle, View.TRANSLATION_Y, -100f, 0f).apply {
            duration = 500
            start()
        }
        ObjectAnimator.ofFloat(binding.tilName, View.ALPHA, 0f, 1f).apply {
            duration = 500
            startDelay = 100
            start()
        }
        ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 0f, 1f).apply {
            duration = 500
            startDelay = 200
            start()
        }
        ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 0f, 1f).apply {
            duration = 500
            startDelay = 300
            start()
        }
        ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 0f, 1f).apply {
            duration = 500
            startDelay = 400
            start()
        }
    }

    private fun setupAction() {
        binding.tvLogin.setOnClickListener {
            finish()
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            when {
                name.isEmpty() -> {
                    binding.edRegisterName.error = getString(R.string.error_empty_name)
                }
                email.isEmpty() -> {
                    binding.edRegisterEmail.error = getString(R.string.error_empty_email)
                }
                password.isEmpty() -> {
                    binding.edRegisterPassword.error = getString(R.string.error_empty_password)
                }
                binding.edRegisterPassword.error != null -> {
                    return@setOnClickListener
                }
                else -> {
                    performRegister(name, email, password)
                }
            }
        }
    }

    private fun performRegister(name: String, email: String, password: String) {
        viewModel.register(name, email, password).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
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
        binding.btnRegister.isEnabled = !isLoading
    }
}
