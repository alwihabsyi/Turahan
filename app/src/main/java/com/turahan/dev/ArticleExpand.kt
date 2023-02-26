package com.turahan.dev

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.turahan.dev.databinding.ActivityArticleExpandBinding
import com.turahan.dev.user.MainActivity

class ArticleExpand : AppCompatActivity() {

    private lateinit var binding: ActivityArticleExpandBinding
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: rvArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleExpandBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun init() {
        recyclerView = binding.recyclerView

        var data = ArrayList<News>()
        data.add(News(R.drawable.news_image, "Mengapa Bayar Pajak?", "Februari, 17 2023"))
        data.add(News(R.drawable.news_image, "Mengapa Bayar Pajak?", "Januari, 14 2023"))
        data.add(News(R.drawable.news_image, "Mengapa Bayar Pajak?", "Agustus, 10 2022"))
        data.add(News(R.drawable.news_image, "Mengapa Bayar Pajak?", "Februari, 25 2023"))

        data.add(News(R.drawable.news_image, "Mengapa Bayar Pajak?", "Februari, 17 2023"))
        data.add(News(R.drawable.news_image, "Mengapa Bayar Pajak?", "Januari, 14 2023"))
        data.add(News(R.drawable.news_image, "Mengapa Bayar Pajak?", "Agustus, 10 2022"))
        data.add(News(R.drawable.news_image, "Mengapa Bayar Pajak?", "Februari, 25 2023"))

        data.add(News(R.drawable.news_image, "Mengapa Bayar Pajak?", "Februari, 17 2023"))
        data.add(News(R.drawable.news_image, "Mengapa Bayar Pajak?", "Januari, 14 2023"))
        data.add(News(R.drawable.news_image, "Mengapa Bayar Pajak?", "Agustus, 10 2022"))
        data.add(News(R.drawable.news_image, "Mengapa Bayar Pajak?", "Februari, 25 2023"))

        data.add(News(R.drawable.news_image, "Mengapa Bayar Pajak?", "Februari, 17 2023"))
        data.add(News(R.drawable.news_image, "Mengapa Bayar Pajak?", "Januari, 14 2023"))
        data.add(News(R.drawable.news_image, "Mengapa Bayar Pajak?", "Agustus, 10 2022"))
        data.add(News(R.drawable.news_image, "Mengapa Bayar Pajak?", "Februari, 25 2023"))

        adapter = rvArticleAdapter(data)
    }

    data class News(
        val imgView: Int,
        val txtTitle: String,
        val txtDate: String
    )
}
