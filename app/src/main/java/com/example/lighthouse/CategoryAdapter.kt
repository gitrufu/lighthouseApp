package com.example.lighthouse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.lighthouse.R
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CategoryAdapter(
    private val categories: List<Category>,
    private val onItemClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryImage: ImageView = view.findViewById(R.id.category_image)
        val categoryName: TextView = view.findViewById(R.id.category_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        
        Glide.with(holder.itemView.context)
            .load(category.image)
            .centerCrop()
            .into(holder.categoryImage)
            
        holder.categoryName.text = category.name
        holder.itemView.setOnClickListener { onItemClick(category) }
    }

    override fun getItemCount() = categories.size
} 