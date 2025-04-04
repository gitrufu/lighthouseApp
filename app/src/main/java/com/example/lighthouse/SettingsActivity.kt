package com.example.lighthouse

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SettingsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize Firebase
        auth = Firebase.auth
        db = Firebase.firestore

        val userName = findViewById<TextView>(R.id.user_name)
        val userEmail = findViewById<TextView>(R.id.user_email)
        val logoutButton = findViewById<MaterialButton>(R.id.logout_button)

        // Load user data
        auth.currentUser?.let { user ->
            // Set email immediately since we have it
            userEmail.text = user.email

            // Get additional user data from Firestore
            db.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        userName.text = document.getString("name") ?: "User"
                    }
                }
        }

        // Handle logout
        logoutButton.setOnClickListener {
            auth.signOut()
            // Navigate back to login
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}