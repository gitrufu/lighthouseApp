package com.example.lighthouse.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lighthouse.R

class ImageSliderAdapter : RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {
    private var images: List<String> = emptyList()

    fun setImages(newImages: List<String>) {
        images = newImages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        return ImageViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = images[position]
        val context = holder.imageView.context
        if (imageUrl.startsWith("@drawable/")) {
            val resourceName = imageUrl.substringAfter("@drawable/")
            val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
            if (resourceId != 0) {
                Glide.with(context)
                    .load(resourceId)
                    .error(R.drawable.logo)
                    .into(holder.imageView)
            }
        } else {
            Glide.with(context)
                .load(imageUrl)
                .error(R.drawable.logo)
                .into(holder.imageView)
        }
    }

    override fun getItemCount(): Int = images.size

    class ImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
}
