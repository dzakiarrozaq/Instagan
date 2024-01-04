package com.bangkit.storyapp.ui.detail

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.storyapp.R
import com.bangkit.storyapp.data.response.withDateFormat
import com.bangkit.storyapp.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityDetailBinding
    companion object {
        const val NAME = "name"
        const val CREATE_AT = "create_at"
        const val DESCRIPTION = "description"
        const val PHOTO_URL = "photoUrl"
    }
    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detail_story)

        val photoUrl = intent.getStringExtra(PHOTO_URL)
        val name = intent.getStringExtra(NAME)
        val create = intent.getStringExtra(CREATE_AT)
        val description = intent.getStringExtra(DESCRIPTION)

        Glide.with(binding.root.context)
            .load(photoUrl)
            .into(binding.ivDetailPhoto)
        binding.tvDetailName.text = name
        binding.tvDetailCreatedTime.text = create?.withDateFormat()
        binding.tvDetailDescription.text = description

    }
}