package com.example.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.StoryViewModelFactory
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.helper.SessionManager
import com.example.storyapp.viemodel.StoryViewModel
import com.example.storyapp.adapter.StoryAdapter
import com.example.storyapp.api.Injection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: StoryViewModel by viewModels {
        StoryViewModelFactory(Injection.provideRepository(applicationContext))  // Using the custom factory
    }
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding before any conditional logic
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            try {
                val token = SessionManager.getAuthToken(this@MainActivity).first()
                if (token.isNullOrEmpty()) {
                    navigateToWelcome()
                } else {
                    setupUI()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error retrieving session. Please log in again.", Toast.LENGTH_SHORT).show()
                navigateToWelcome()
            }
        }

        // Optional: set the window to edge-to-edge layout
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun setupUI() {
        setupRecyclerView()
        observeViewModel()

        // Navigate to AddStoryActivity when the FAB is clicked
        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        // Fetch stories from the API
        lifecycleScope.launch {
            viewModel.fetchStories()
        }
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter() // No need to pass context or stories
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = storyAdapter
    }

    private fun observeViewModel() {
        // Observe stories from ViewModel
        viewModel.stories.observe(this, Observer { stories ->
            stories?.let {
                storyAdapter.submitList(it) // Now ListStoryItem is passed correctly
            }
        })

        // Observe error messages
        viewModel.errorMessage.observe(this, Observer { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })

        // Observe story add success
        viewModel.addStorySuccess.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Story added successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to add story", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToWelcome() {
        startActivity(Intent(this@MainActivity, WelcomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish() // Close MainActivity
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Clear session and redirect to welcome
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            // Menghapus data sesi menggunakan SessionManager
            SessionManager.clearSession(this@MainActivity)

            // Menampilkan pesan berhasil logout
            Toast.makeText(this@MainActivity, "Logged out successfully", Toast.LENGTH_SHORT).show()

            // Mengarahkan pengguna ke LoginActivity
            navigateToLogin()
        }
    }


    // Call fetchStories when the activity is resumed, to refresh the list of stories
    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.fetchStories()
        }
    }
    private fun navigateToLogin() {
        startActivity(Intent(this@MainActivity, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

}
