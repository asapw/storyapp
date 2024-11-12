package com.example.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.StoryViewModelFactory
import com.example.storyapp.adapter.StoryAdapter
import com.example.storyapp.api.Injection
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.helper.SessionManager
import com.example.storyapp.viemodel.StoryViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private val viewModel: StoryViewModel by viewModels {
        StoryViewModelFactory(Injection.provideRepository(applicationContext))  // Using the custom factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            // Asynchronously get the token from DataStore
            val token = SessionManager.getAuthToken(this@MainActivity).first()
            Log.d("MainActivity", "Retrieved Token: $token")

            if (token.isNullOrEmpty()) {
                // Token is invalid or missing, redirect to login
                startActivity(Intent(this@MainActivity, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish() // Close MainActivity
            } else {
                // Token is valid, proceed with MainActivity setup
                binding = ActivityMainBinding.inflate(layoutInflater)
                setContentView(binding.root)

                // Set up RecyclerView with LayoutManager
                storyAdapter = StoryAdapter()
                binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)  // Add LayoutManager here
                binding.recyclerView.adapter = storyAdapter

                // Observe stories from ViewModel
                viewModel.stories.observe(this@MainActivity) { stories ->
                    stories?.let {
                        storyAdapter.submitList(it) // Update the adapter's list
                    }
                }

                // Observe error messages
                viewModel.errorMessage.observe(this@MainActivity) { error ->
                    Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                }

                // Fetch stories from the API via the repository (No arguments needed)
                viewModel.fetchStories()

                // Optional: set the window to edge-to-edge layout
                WindowCompat.setDecorFitsSystemWindows(window, false)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Clear session and redirect to login
                lifecycleScope.launch {
                    SessionManager.clearSession(this@MainActivity)
                    Toast.makeText(this@MainActivity, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
