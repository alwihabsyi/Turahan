package com.turahan.dev.sign

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.turahan.dev.R
import com.turahan.dev.data.DataUser
import com.turahan.dev.databinding.FragmentSignInBinding
import com.turahan.dev.user.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class SignInFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var gsc: GoogleSignInClient
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSignInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("881490459068-1634ddiek6dlne2j6atscesdmlge7vta.apps.googleusercontent.com")
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(requireContext(), gso)

        binding.btnGooglesignin.setOnClickListener {
            googleSignIn()
        }

        binding.btnSignin.setOnClickListener {
            startSigningIn()
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
                val intent = Intent(this.context, MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            } else {
                Toast.makeText(this.context, "", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this.context, it.localizedMessage, Toast.LENGTH_SHORT)
                .show()
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_UP) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(ContentValues.TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(ContentValues.TAG, "Google sign in failed", e)
            }
        }
    }

    companion object {
        const val RC_SIGN_UP = 1000
    }

    private fun googleSignIn() {
        val signInIntent = gsc.signInIntent
        startActivityForResult(signInIntent, SignUpFragment.RC_SIGN_UP)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        database = FirebaseDatabase.getInstance().getReference("idTokenUser")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(ContentValues.TAG, "signInWithCredential:failed" + it.exception)
                    updateUI(null)
                }
            }
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    private fun updateUI(user: FirebaseUser?) {
        database = FirebaseDatabase.getInstance().getReference("User")
        database.child(user!!.uid).get().addOnSuccessListener {
            if(it.exists()){
                val intent = Intent(context, MainActivity::class.java)
                intent.putExtra(SignUpFragment.EXTRA_NAME, user.displayName)
                startActivity(intent)
                activity?.finish()
            }else{
                val date = Calendar.getInstance().time
                val tanggalBergabung = date.toString("dd MMMM YYYY")
                val datauser = DataUser(
                    user.uid,
                    user.displayName,
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "0",
                    "0",
                    "0",
                    tanggalBergabung)
                database.child(user.uid).setValue(datauser)
                val intent = Intent(context, MainActivity::class.java)
                intent.putExtra(SignUpFragment.EXTRA_NAME, user.displayName)
                startActivity(intent)
                activity?.finish()
            }
        }
    }
}