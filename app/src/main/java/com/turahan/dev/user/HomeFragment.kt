package com.turahan.dev.user

import android.content.Intent
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
import com.turahan.dev.databinding.FragmentHomeBinding
import com.turahan.dev.sign.SignUpFragment.Companion.EXTRA_NAME
import com.turahan.dev.user.home.*
import me.relex.circleindicator.CircleIndicator3

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewPager: ViewPager2
    lateinit var recyclerView: RecyclerView
    lateinit var rvAdapter: rvArticleHomeAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var gsc: GoogleSignInClient
    private lateinit var database: DatabaseReference

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
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("793143099222-j5r05tbkdqilsken13gv4f9jj3u3s5bj.apps.googleusercontent.com")
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(requireContext(), gso)
        profname()

        //ViewPager Start
        viewPager = view.findViewById(R.id.viewPager)

        val images = listOf(
            R.drawable.poster_one,
            R.drawable.poster_two,
            R.drawable.poster_three
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
            IsiDonasi().show(requireActivity().supportFragmentManager, "isi donasi")
        }
        binding.btnArticleExpand.setOnClickListener {
            startActivity(Intent(requireContext(), ArticleExpand::class.java))
        }
        binding.notificon.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }
        //Intent Button End

        //rvArticle Start
        init()
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


    private fun init() {
        recyclerView = binding.rvMiniArticle

        var data = ArrayList<ArticleExpand.News>()
        data.add(
            ArticleExpand.News(
                R.drawable.news_image,
                "Mengapa Bayar Pajak?",
                "Februari, 17 2023"
            )
        )
        data.add(
            ArticleExpand.News(
                R.drawable.news_image,
                "Mengapa Bayar Pajak?",
                "Januari, 14 2023"
            )
        )
        data.add(
            ArticleExpand.News(
                R.drawable.news_image,
                "Mengapa Bayar Pajak?",
                "Agustus, 10 2022"
            )
        )
        data.add(
            ArticleExpand.News(
                R.drawable.news_image,
                "Mengapa Bayar Pajak?",
                "Februari, 25 2023"
            )
        )

        data.add(
            ArticleExpand.News(
                R.drawable.news_image,
                "Mengapa Bayar Pajak?",
                "Februari, 17 2023"
            )
        )
        data.add(
            ArticleExpand.News(
                R.drawable.news_image,
                "Mengapa Bayar Pajak?",
                "Januari, 14 2023"
            )
        )
        data.add(
            ArticleExpand.News(
                R.drawable.news_image,
                "Mengapa Bayar Pajak?",
                "Agustus, 10 2022"
            )
        )
        data.add(
            ArticleExpand.News(
                R.drawable.news_image,
                "Mengapa Bayar Pajak?",
                "Februari, 25 2023"
            )
        )

        data.add(
            ArticleExpand.News(
                R.drawable.news_image,
                "Mengapa Bayar Pajak?",
                "Februari, 17 2023"
            )
        )
        data.add(
            ArticleExpand.News(
                R.drawable.news_image,
                "Mengapa Bayar Pajak?",
                "Januari, 14 2023"
            )
        )
        data.add(
            ArticleExpand.News(
                R.drawable.news_image,
                "Mengapa Bayar Pajak?",
                "Agustus, 10 2022"
            )
        )
        data.add(
            ArticleExpand.News(
                R.drawable.news_image,
                "Mengapa Bayar Pajak?",
                "Februari, 25 2023"
            )
        )

        data.add(
            ArticleExpand.News(
                R.drawable.news_image,
                "Mengapa Bayar Pajak?",
                "Februari, 17 2023"
            )
        )
        data.add(
            ArticleExpand.News(
                R.drawable.news_image,
                "Mengapa Bayar Pajak?",
                "Januari, 14 2023"
            )
        )
        data.add(
            ArticleExpand.News(
                R.drawable.news_image,
                "Mengapa Bayar Pajak?",
                "Agustus, 10 2022"
            )
        )
        data.add(
            ArticleExpand.News(
                R.drawable.news_image,
                "Mengapa Bayar Pajak?",
                "Februari, 25 2023"
            )
        )

        rvAdapter = rvArticleHomeAdapter(data)
    }

}