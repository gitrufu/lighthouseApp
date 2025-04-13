package com.example.lighthouse.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lighthouse.databinding.ItemSubcategoryBinding
import com.example.lighthouse.models.SubCategory

class SubcategoryAdapter(private val onSubcategoryClick: (SubCategory) -> Unit) :
    ListAdapter<SubCategory, SubcategoryAdapter.SubcategoryViewHolder>(SubcategoryDiffCallback()) {

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
            Glide.with(binding.root.context)
                .load(subcategory.imageUrl)
                .into(binding.subcategoryImage)
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
