package com.example.storyapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.databinding.ActivityWelcomeBinding
import com.example.storyapp.helper.SessionManager
import kotlinx.coroutines.runBlocking

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is logged in
        val isLoggedIn = runBlocking { SessionManager.getAuthTokenSync(this@WelcomeActivity) != null }

        if (isLoggedIn) {
            // If logged in, navigate to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Prevent going back to WelcomeActivity
            return
        }

        // Otherwise, show WelcomeActivity
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        binding.btnRegister.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }
    }
}