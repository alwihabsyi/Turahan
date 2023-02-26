package com.turahan.dev.user.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.turahan.dev.R
import com.turahan.dev.databinding.ActivitySecurityBinding
import com.turahan.dev.sign.LoginActivity

class SecurityActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecurityBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecurityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.btnUpdatepw.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {
        val pwlama = binding.etPwnow.text
        val pwbaru = binding.etPwbaru.text
        val pwkonf = binding.etPwconf.text

        if(pwlama.isNotEmpty() && pwbaru.isNotEmpty() && pwkonf.isNotEmpty()){
            if(pwbaru.toString() == pwkonf.toString()){
                val user = auth.currentUser
                if(user != null){
                    val credential = EmailAuthProvider.getCredential(user.email!!, pwlama.toString())
                    user.reauthenticate(credential).addOnCompleteListener {
                        if(it.isSuccessful){
                            user.updatePassword(pwbaru.toString()).addOnCompleteListener {task ->
                                if(task.isSuccessful){
                                    Toast.makeText(this, "Berhasil ubah password", Toast.LENGTH_SHORT).show()
                                    auth.signOut()
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish()
                                }
                            }.addOnFailureListener { task ->
                                Toast.makeText(this, task.localizedMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                    }

                }else{
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }else{
                Toast.makeText(this, "Password Tidak Sama!", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "Harap isi semua field!", Toast.LENGTH_SHORT).show()
        }

    }
}