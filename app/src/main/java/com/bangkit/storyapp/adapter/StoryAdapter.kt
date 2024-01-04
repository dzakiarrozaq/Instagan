package com.bangkit.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.storyapp.databinding.ViewItemStoryBinding
import com.bangkit.storyapp.data.response.ListStory
import com.bangkit.storyapp.data.response.withDateFormat
import com.bangkit.storyapp.ui.detail.DetailActivity
import com.bumptech.glide.Glide

class StoryAdapter(private val listReview: List<ListStory>) : RecyclerView.Adapter<StoryAdapter.MyViewHolderStory>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderStory {
        val binding = ViewItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolderStory(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolderStory, position: Int) {

        val data = listReview[position]
        holder.bind(data)
    }
    override fun getItemCount() = listReview.size

    class MyViewHolderStory(private val binding: ViewItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStory) {
            Glide.with(binding.root.context)
                .load(data.photoUrl)
                .into(binding.imgItemPhoto)

            binding.tvItemName.text = data.name
            binding.tvItemCreated.text = data.createdAt.withDateFormat()
            binding.tvItemDescription.text = data.description
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.NAME, data.name)
                intent.putExtra(DetailActivity.CREATE_AT, data.createdAt)
                intent.putExtra(DetailActivity.DESCRIPTION, data.description)
                intent.putExtra(DetailActivity.PHOTO_URL, data.photoUrl)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        androidx.core.util.Pair(binding.imgItemPhoto, "photo"),
                        androidx.core.util.Pair(binding.tvItemName, "name"),
                        androidx.core.util.Pair(binding.tvItemCreated, "createdate"),
                        androidx.core.util.Pair(binding.tvItemDescription, "description"),
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }
}