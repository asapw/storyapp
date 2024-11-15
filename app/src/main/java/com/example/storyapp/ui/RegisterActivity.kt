package com.example.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            if (validateRegistration(name, email, password)) {
                binding.progressBarRegister.visibility = android.view.View.VISIBLE

                lifecycleScope.launch {
                    try {
                        val token = "your_token_here"

                        val apiService = ApiConfig.getApiService(token)

                        val response = apiService.register(name, email, password)

                        binding.progressBarRegister.visibility = android.view.View.GONE

                        if (response.isSuccessful && response.body() != null && !response.body()!!.error!!) {
                            Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            val errorMessage = response.body()?.message ?: "Registration failed"
                            Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        binding.progressBarRegister.visibility = android.view.View.GONE
                        Toast.makeText(this@RegisterActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please check your input", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateRegistration(name: String, email: String, password: String): Boolean {
        if (name.isEmpty()) {
            binding.edRegisterName.error = "Name cannot be empty"
            return false
        }

        if (email.isEmpty()) {
            binding.edRegisterEmail.error = "Email cannot be empty"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edRegisterEmail.error = "Please enter a valid email"
            return false
        }

        if (password.isEmpty()) {
            binding.edRegisterPassword.error = "Password cannot be empty"
            return false
        }

        if (password.length < 8) {
            binding.edRegisterPassword.error = "Password must be at least 8 characters"
            return false
        }

        return true
    }
}