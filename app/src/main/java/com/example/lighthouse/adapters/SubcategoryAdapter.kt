package com.example.lighthouse.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.lighthouse.R
import com.example.lighthouse.databinding.ItemSubcategoryBinding
import com.example.lighthouse.models.SubCategory

class SubcategoryAdapter(private val onSubcategoryClick: (SubCategory) -> Unit) :
    ListAdapter<SubCategory, SubcategoryAdapter.SubcategoryViewHolder>(SubcategoryDiffCallback()) {

    companion object {
        private const val TAG = "SubcategoryAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubcategoryViewHolder {
        val binding = ItemSubcategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SubcategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubcategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SubcategoryViewHolder(private val binding: ItemSubcategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onSubcategoryClick(getItem(position))
                }
            }
        }

        fun bind(subcategory: SubCategory) {
            binding.subcategoryName.text = subcategory.name
            
            try {
                Log.d(TAG, "Loading image for subcategory: ${subcategory.name}, URL: ${subcategory.imageUrl}")
                
                // Handle drawable references
                if (subcategory.imageUrl.startsWith("@drawable/")) {
                    val resourceName = subcategory.imageUrl.substringAfter("@drawable/")
                    val resourceId = binding.root.context.resources.getIdentifier(
                        resourceName,
                        "drawable",
                        binding.root.context.packageName
                    )
                    
                    if (resourceId != 0) {
                        Log.d(TAG, "Loading drawable resource: $resourceName (ID: $resourceId)")
                        Glide.with(binding.root.context)
                            .load(resourceId)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .error(R.drawable.logo)
                            .into(binding.subcategoryImage)
                    } else {
                        Log.e(TAG, "Could not find drawable resource: $resourceName")
                        binding.subcategoryImage.setImageResource(R.drawable.logo)
                    }
                } else {
                    // Handle regular URLs
                    Glide.with(binding.root.context)
                        .load(subcategory.imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .error(R.drawable.logo)
                        .into(binding.subcategoryImage)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading image for subcategory ${subcategory.name}: ${e.message}")
                binding.subcategoryImage.setImageResource(R.drawable.logo)
            }
        }
    }

    private class SubcategoryDiffCallback : DiffUtil.ItemCallback<SubCategory>() {
        override fun areItemsTheSame(oldItem: SubCategory, newItem: SubCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SubCategory, newItem: SubCategory): Boolean {
            return oldItem == newItem
        }
    }
}
