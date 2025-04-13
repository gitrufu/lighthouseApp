package com.example.lighthouse

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lighthouse.adapters.OrdersAdapter
import com.example.lighthouse.database.DatabaseHelper
import com.example.lighthouse.databinding.ActivityOrdersBinding

class OrdersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrdersBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: OrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        setupRecyclerView()
        loadOrders()
    }

    private fun setupRecyclerView() {
        adapter = OrdersAdapter()
        binding.recyclerOrders.apply {
            layoutManager = LinearLayoutManager(this@OrdersActivity)
            adapter = this@OrdersActivity.adapter
        }
    }

    private fun loadOrders() {
        try {
            val orders = dbHelper.getOrders()
            if (orders.isEmpty()) {
                binding.emptyView.visibility = android.view.View.VISIBLE
                binding.recyclerOrders.visibility = android.view.View.GONE
            } else {
                binding.emptyView.visibility = android.view.View.GONE
                binding.recyclerOrders.visibility = android.view.View.VISIBLE
                adapter.submitList(orders)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading orders: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
