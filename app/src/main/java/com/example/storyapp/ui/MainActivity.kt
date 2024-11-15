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
        StoryViewModelFactory(Injection.provideRepository(applicationContext))
    }
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun setupUI() {
        setupRecyclerView()
        observeViewModel()

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        lifecycleScope.launch {
            viewModel.fetchStories()
        }
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = storyAdapter
    }

    private fun observeViewModel() {
        viewModel.stories.observe(this, Observer { stories ->
            stories?.let {
                storyAdapter.submitList(it)
            }
        })

        viewModel.errorMessage.observe(this, Observer { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })

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
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            SessionManager.clearSession(this@MainActivity)

            Toast.makeText(this@MainActivity, "Logged out successfully", Toast.LENGTH_SHORT).show()

            navigateToLogin()
        }
    }


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
