package com.turahan.dev.sign

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.turahan.dev.R
import com.turahan.dev.databinding.FragmentLoginVolunteerBinding
import com.turahan.dev.user.MainActivity
import com.turahan.dev.volunteer.VolunteerMain

class LoginVolunteerFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentLoginVolunteerBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginVolunteerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().getReference("UserVolunteer")

        binding.btnSignin.setOnClickListener {
            if (binding.etEmail.text!!.isEmpty() || binding.etPassword.text!!.isEmpty()) {
                Toast.makeText(this.context, R.string.isi_field, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.etEmail.text.toString() == "alwihbsyi.ah@gmail.com" || binding.etEmail.text.toString() == "Moh.Azam121@gmail.com") {
                startSigningIn()
            }else{
                Toast.makeText(requireContext(), "Akun tidak terdaftar sebagai Volunteer, silahkan masuk sebagai user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startSigningIn() {
        val email = binding.etEmail.text
        val password = binding.etPassword.text
        val emInput = email.toString()
        val pwInput = password.toString()

        if (email!!.isEmpty() || password!!.isEmpty()) {
            Toast.makeText(this.context, R.string.isi_field, Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(emInput, pwInput).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this.context, VolunteerMain::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }.addOnFailureListener {
            Toast.makeText(this.context, it.localizedMessage, Toast.LENGTH_SHORT)
                .show()
        }
    }
}