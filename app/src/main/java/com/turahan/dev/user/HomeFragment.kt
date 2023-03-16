package com.turahan.dev.user

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.turahan.dev.*
import com.turahan.dev.data.Artikel
import com.turahan.dev.data.ListArtikel
import com.turahan.dev.databinding.FragmentHomeBinding
import com.turahan.dev.sign.SignUpFragment.Companion.EXTRA_NAME
import com.turahan.dev.user.home.*
import com.turahan.dev.volunteer.VolunteerMain
import me.relex.circleindicator.CircleIndicator3

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewPager: ViewPager2
    lateinit var recyclerView: RecyclerView
    lateinit var rvAdapter: RvArticleHomeAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var gsc: GoogleSignInClient
    private lateinit var database: DatabaseReference
    lateinit var prefEditor: SharedPreferences.Editor
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        sharedPreferences = requireContext().getSharedPreferences("didShowGuideline", Context.MODE_PRIVATE)
        prefEditor = sharedPreferences.edit()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("793143099222-j5r05tbkdqilsken13gv4f9jj3u3s5bj.apps.googleusercontent.com")
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(requireContext(), gso)
        profname()
        showFeaturesGuidelines()

        //ViewPager Start
        viewPager = view.findViewById(R.id.viewPager)

        val images = listOf(
            R.drawable.poster_three,
            R.drawable.poster_one,
            R.drawable.poster_two
        )

        val adapter = ViewPagerAdapter(images)
        viewPager.adapter = adapter

        val indicator3: CircleIndicator3 = view.findViewById(R.id.circleIndicator3)
        indicator3.setViewPager(viewPager)
        //ViewPager End

        //Intent Button Start
        binding.btnPickUp.setOnClickListener {
            startActivity(Intent(requireContext(), PickUp::class.java))
        }
        binding.btnDropOff.setOnClickListener {
            startActivity(Intent(requireContext(), DropOff::class.java))
        }
        binding.btnDonateCash.setOnClickListener {
            startActivity(Intent(requireContext(), PaymentMidtrans::class.java))
        }
        binding.btnArticleExpand.setOnClickListener {
            startActivity(Intent(requireContext(), ArticleExpand::class.java))
        }
        binding.notificon.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }
        //Intent Button End

        //rvArticle Start
        recyclerView = binding.rvMiniArticle
        var data = ArrayList<Artikel>()
        data.addAll(ListArtikel.list)
        rvAdapter = RvArticleHomeAdapter(data)
        onArticleItemClicked()
        
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = rvAdapter
        //rvArticle End
    }

    private fun profname() {
        val currentUser = auth.currentUser
        if(currentUser != null){
            database = FirebaseDatabase.getInstance().getReference("User")
            database.child(auth.currentUser!!.uid).get().addOnSuccessListener {
                if(it.exists()){
                    val tvuser = it.child("uname").value.toString()
                    val poin = it.child("totalPoin").value.toString()
                    binding.tvUsername.text = tvuser
                    binding.tvPoints.text = poin
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

    private fun onArticleItemClicked() {
        rvAdapter.onItemClick = { artikel ->
            val intent = Intent(requireContext(), ArticleDetailActivity::class.java)
            intent.putExtra("ARTIKEL_TITLE", artikel.title)
            intent.putExtra("ARTIKEL_DATE", artikel.date)
            intent.putExtra("ARTIKEL_CONTENT", artikel.content)
            intent.putExtra("ARTIKEL_IMAGE", artikel.image.toString())
            startActivity(intent)
        }
    }

    private fun promptFinished(): Boolean {
        return sharedPreferences.getBoolean("promptFinished", false)
    }

    private fun showFeaturesGuidelines() {
        if (!promptFinished()) {
            showPickUpPrompt()
        }
    }
    private fun showPickUpPrompt() {
        TapTargetView.showFor(
            activity,
            TapTarget.forView(binding.btnPickUp, "Pickup Food Donation", "Donate your food donation from anywhere")
                .tintTarget(false)
                .outerCircleColor(R.color.BrownDark)
                .cancelable(false)
                .textColor(R.color.white)
                .titleTextSize(24),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                    showDropOffPrompt()
                }
            }
        )
    }
    private fun showDropOffPrompt() {
        TapTargetView.showFor(
            activity,
            TapTarget.forView(binding.btnDropOff, "Drop Off Food Donation", "Deliver your food donation in our several drop points")
                .tintTarget(false)
                .outerCircleColor(R.color.BrownDark)
                .cancelable(false)
                .textColor(R.color.white)
                .titleTextSize(24),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                    showCashDonatePrompt()
                }
            }
        )
    }

    private fun showCashDonatePrompt() {
        TapTargetView.showFor(
            activity,
            TapTarget.forView(binding.btnDonateCash, "Cash Donation", "Besides Food Donate, you can donate cash for needy people")
                .tintTarget(false)
                .cancelable(false)
                .outerCircleColor(R.color.BrownDark)
                .textColor(R.color.white)
                .titleTextSize(24),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                    prefEditor = sharedPreferences.edit()
                    prefEditor.putBoolean("promptFinished", true)
                    prefEditor.apply()
                }
            }
        )
    }
}