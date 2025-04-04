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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private companion object {
        private const val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth and Firestore
        auth = Firebase.auth
        db = Firebase.firestore

        val nameInput = findViewById<TextInputEditText>(R.id.name_input)
        val emailInput = findViewById<TextInputEditText>(R.id.email_input)
        val passwordInput = findViewById<TextInputEditText>(R.id.password_input)
        val confirmPasswordInput = findViewById<TextInputEditText>(R.id.confirm_password_input)
        val registerButton = findViewById<MaterialButton>(R.id.register_button)
        val loginLink = findViewById<TextView>(R.id.login_link)

        registerButton.setOnClickListener {
            val name = nameInput.text?.toString()?.trim() ?: ""
            val email = emailInput.text?.toString()?.trim() ?: ""
            val password = passwordInput.text?.toString()?.trim() ?: ""
            val confirmPassword = confirmPasswordInput.text?.toString()?.trim() ?: ""

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable button to prevent multiple clicks
            registerButton.isEnabled = false

            // Create user with email and password
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Create user document in Firestore
                        val user = auth.currentUser
                        if (user != null) {
                            val userDoc = hashMapOf(
                                "name" to name,
                                "email" to email,
                                "createdAt" to System.currentTimeMillis(),
                                "isAdmin" to false, // Default to regular user
                                "cart" to listOf<Map<String, Any>>() // Initialize empty cart array
                            )

                            db.collection("users").document(user.uid)
                                .set(userDoc)
                                .addOnSuccessListener {
                                    // Start HomeActivity
                                    startActivity(Intent(this, HomeActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error creating user profile: ${e.message}", Toast.LENGTH_SHORT).show()
                                    registerButton.isEnabled = true
                                }
                        }
                    } else {
                        // If registration fails, display a message to the user.
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        registerButton.isEnabled = true
                    }
                }
        }

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}