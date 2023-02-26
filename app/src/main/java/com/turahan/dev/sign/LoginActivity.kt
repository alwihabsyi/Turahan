package com.turahan.dev.sign

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.turahan.dev.databinding.ActivityLoginBinding
import com.turahan.dev.user.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var gsc: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        // Button Click
        binding.btnSignIn.setOnClickListener {
            SignInFragment().show(supportFragmentManager, "sign in fragment")
        }

        binding.btnSignUp.setOnClickListener {
            SignUpFragment().show(supportFragmentManager, "sign up fragment")
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
            finish()
        }
    }
}