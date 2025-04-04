package com.example.lighthouse

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Create admin user if not exists
        createAdminUser()

        val emailInput = findViewById<TextInputEditText>(R.id.email_input)
        val passwordInput = findViewById<TextInputEditText>(R.id.password_input)
        val loginButton = findViewById<MaterialButton>(R.id.login_button)
        val registerLink = findViewById<TextView>(R.id.register_link)

        loginButton.setOnClickListener {
            val email = emailInput.text?.toString()?.trim() ?: ""
            val password = passwordInput.text?.toString()?.trim() ?: ""

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show loading state
            loginButton.isEnabled = false
            loginButton.text = "Logging in..."

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        // Sign in failed
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", 
                            Toast.LENGTH_SHORT).show()
                        // Reset button state
                        loginButton.isEnabled = true
                        loginButton.text = "Login"
                    }
                }
        }

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun createAdminUser() {
        val adminEmail = "admin@lighthouse.com"
        val adminPassword = "admin123"

        auth.fetchSignInMethodsForEmail(adminEmail).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods ?: emptyList<String>()
                if (signInMethods.isEmpty()) {
                    // Admin user doesn't exist, create it
                    auth.createUserWithEmailAndPassword(adminEmail, adminPassword)
                        .addOnCompleteListener { createTask ->
                            if (createTask.isSuccessful) {
                                println("Admin user created successfully")
                            } else {
                                println("Failed to create admin user: ${createTask.exception?.message}")
                            }
                        }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in and update UI accordingly
        auth.currentUser?.let {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}