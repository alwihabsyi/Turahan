package com.turahan.dev.user

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.turahan.dev.ChangeProfileActivity
import com.turahan.dev.user.profile.AboutActivity
import com.turahan.dev.user.profile.DonationHistory
import com.turahan.dev.user.profile.DonationReport
import com.turahan.dev.user.profile.SecurityActivity
import com.turahan.dev.databinding.FragmentProfileBinding
import com.turahan.dev.sign.LoginActivity

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var gsc: GoogleSignInClient
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("793143099222-j5r05tbkdqilsken13gv4f9jj3u3s5bj.apps.googleusercontent.com")
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(requireContext(), gso)
        profname()

        //Intent Button Start
        binding.tvChangeprofile.setOnClickListener {
            startActivity(Intent(requireContext(), ChangeProfileActivity::class.java))
        }
        binding.btnDonationReport.setOnClickListener {
            startActivity(Intent(requireContext(), DonationReport::class.java))
        }
        binding.btnDonationHistory.setOnClickListener {
            startActivity(Intent(requireContext(), DonationHistory::class.java))
        }
        binding.btnHelp.setOnClickListener {
            startActivity(Intent(requireContext(), SecurityActivity::class.java))
        }
        binding.btnAboutApp.setOnClickListener {
            startActivity(Intent(requireContext(), AboutActivity::class.java))
        }
        binding.btnLogOut.setOnClickListener {
            auth.signOut()
            gsc.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity?.finish()
        }
        //Intent Button End
    }

    private fun profname() {
        val currentUser = auth.currentUser
        if(currentUser != null){
            database = FirebaseDatabase.getInstance().getReference("User")
            database.child(auth.currentUser!!.uid).get().addOnSuccessListener {
                if(it.exists()){
                    val tvuser = it.child("uname").value.toString()
                    binding.tvUsername.text = tvuser
                }
            }
            val profil = binding.ivProfil
            val datalink = FirebaseDatabase.getInstance().getReference("userImages")
            datalink.child(auth.currentUser!!.uid).get().addOnSuccessListener {
                if(it.exists()){
                    val url = it.child("url").value.toString()
                    Picasso
                        .get()
                        .load(url)
                        .into(profil)
                }
            }
        }else{
            binding.tvUsername.text = "null"
        }
    }

}