package com.example.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.helper.SessionManager
import com.example.storyapp.viemodel.LoginViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel.loginResult.observe(this) { result ->
            result.onSuccess {
                val loginResult = it.loginResult
                loginResult?.let {
                    val token = it.token ?: ""
                    val userId = it.userId ?: ""
                    lifecycleScope.launch {
                        SessionManager.saveAuthToken(this@LoginActivity, token, userId)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                }
            }
            result.onFailure {
                // Display toast message
                Toast.makeText(
                    this@LoginActivity,
                    "Login failed: ${it.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()

                // Optionally highlight fields if the issue is with email/password
                binding.edLoginEmail.error = "Invalid email or password"
                binding.edLoginPassword.error = "Invalid email or password"
            }
        }


        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (validateLogin(email, password)) {
                lifecycleScope.launch {
                    // Pass email and password, no token needed here
                    loginViewModel.login(email, password)
                }
            } else {
                Toast.makeText(this, "Please check your input", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    // Updated validateLogin function
    private fun validateLogin(email: String, password: String): Boolean {
        var isValid = true

        // Validate email
        if (email.isEmpty()) {
            binding.edLoginEmail.error = "Email cannot be empty"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edLoginEmail.error = "Please enter a valid email"
            isValid = false
        } else {
            binding.edLoginEmail.error = null // Clear error if valid
        }

        // Validate password
        if (password.isEmpty()) {
            binding.edLoginPassword.error = "Password cannot be empty"
            isValid = false
        } else if (password.length < 8) {
            binding.edLoginPassword.error = "Password must be at least 8 characters"
            isValid = false
        } else {
            binding.edLoginPassword.error = null // Clear error if valid
        }

        return isValid
    }

}
