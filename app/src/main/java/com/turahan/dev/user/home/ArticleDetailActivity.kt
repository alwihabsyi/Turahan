package com.turahan.dev.user.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.turahan.dev.databinding.ActivityArticleDetailBinding

class ArticleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleDetailBinding
    private lateinit var artikelTitle: String
    private lateinit var artikelDate: String
    private lateinit var artikelContent: String
    private lateinit var artikelImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getArticleDetail()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.imgDetailArticle.setImageResource(artikelImage.toInt())
        binding.tvDetailArticleTitle.text = artikelTitle
        binding.tvDetailArticleDescription.text = artikelContent
        binding.tvDate.text = artikelDate
    }

    private fun getArticleDetail() {
        val intent = intent
        artikelTitle = intent.getStringExtra("ARTIKEL_TITLE").toString()
        artikelDate = intent.getStringExtra("ARTIKEL_DATE").toString()
        artikelContent = intent.getStringExtra("ARTIKEL_CONTENT").toString()
        artikelImage = intent.getStringExtra("ARTIKEL_IMAGE").toString()
    }

}