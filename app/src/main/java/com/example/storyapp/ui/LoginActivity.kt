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
                        // Save the token and userId
                        SessionManager.saveAuthToken(this@LoginActivity, token, userId)

                        // Ensure the token is saved before proceeding
                        val savedToken = SessionManager.getAuthTokenSync(this@LoginActivity)
                        Log.d("LoginActivity", "Saved Token: $savedToken")

                        if (!savedToken.isNullOrEmpty()) {
                            Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish() // Close the LoginActivity
                        } else {
                            Toast.makeText(this@LoginActivity, "Token saving failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            result.onFailure {
                Toast.makeText(
                    this@LoginActivity,
                    "Login failed: ${it.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
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

    private fun validateLogin(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.length >= 8
    }
}
