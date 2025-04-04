package com.example.lighthouse.adapters

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.lighthouse.R
import com.example.lighthouse.models.Product
import com.google.android.material.card.MaterialCardView
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private val products: List<Product>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: MaterialCardView = view.findViewById(R.id.product_card)
        val imageView: ImageView = view.findViewById(R.id.product_image)
        val nameText: TextView = view.findViewById(R.id.product_name)
        val priceText: TextView = view.findViewById(R.id.product_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        val context = holder.itemView.context
        val imageSource = product.getFirstImageResId()
        
        Log.d("ProductAdapter", "Loading image for ${product.name}: $imageSource")
        
        Glide.with(context)
            .load(imageSource)
            .placeholder(R.drawable.logo)
            .error(R.drawable.logo)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e("ProductAdapter", "Failed to load image for ${product.name}: ${e?.message}")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("ProductAdapter", "Successfully loaded image for ${product.name}")
                    return false
                }
            })
            .centerCrop()
            .override(300, 300)
            .into(holder.imageView)

        holder.nameText.text = product.name
        holder.priceText.text = NumberFormat.getCurrencyInstance(Locale.US)
            .format(product.price)

        holder.cardView.setOnClickListener { onProductClick(product) }
    }

    override fun getItemCount() = products.size
}
