package com.bangkit.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.storyapp.R
import com.bangkit.storyapp.adapter.StoryAdapter
import com.bangkit.storyapp.databinding.ActivityMainBinding
import com.bangkit.storyapp.data.response.ListStory
import com.bangkit.storyapp.data.response.UserPreferenceDatastore
import com.bangkit.storyapp.data.response.dataStore
import com.bangkit.storyapp.ui.ViewModelFactory
import com.bangkit.storyapp.ui.signin.SignInActivity
import com.bangkit.storyapp.ui.signin.SignInModel
import com.bangkit.storyapp.ui.uploadstory.UploadStoryActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainModel
    private lateinit var signViewModel: SignInModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.dashboard_story)

        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferenceDatastore.getInstance(dataStore))
        )[MainModel::class.java]

        signViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferenceDatastore.getInstance(dataStore))
        )[SignInModel::class.java]

        signViewModel.getUser().observe(this){user->
            if (user.userId.isEmpty()){
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                mainViewModel.getListStory(user.token)
            }
        }


        val layoutManager = LinearLayoutManager(this)
        binding.rvListStory.layoutManager = layoutManager

        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvListStory.addItemDecoration(itemDecoration)


        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        mainViewModel.storyList.observe(this) { listStory ->
            setReviewData(listStory)
        }


        binding.btnAddStory.setOnClickListener {
            val i = Intent(this@MainActivity, UploadStoryActivity::class.java)
            startActivity(i)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                signViewModel.signout()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }



    private fun setReviewData(listStory: List<ListStory>) {
        val adapter = StoryAdapter(listStory as ArrayList<ListStory>)
        binding.rvListStory.adapter = adapter
    }



    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}