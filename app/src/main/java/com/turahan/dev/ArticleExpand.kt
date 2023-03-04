package com.turahan.dev

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.turahan.dev.data.Artikel
import com.turahan.dev.data.ListArtikel
import com.turahan.dev.databinding.ActivityArticleExpandBinding
import com.turahan.dev.user.MainActivity
import com.turahan.dev.user.home.ArticleDetailActivity

class ArticleExpand : AppCompatActivity() {

    private lateinit var binding: ActivityArticleExpandBinding
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: rvArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleExpandBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.recyclerView
        var data = ArrayList<Artikel>()
        data.addAll(ListArtikel.list)
        adapter = rvArticleAdapter(data)
        onArticleItemClicked()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onArticleItemClicked() {
        adapter.onItemClick = { artikel ->
            val intent = Intent(this, ArticleDetailActivity::class.java)
            intent.putExtra("ARTIKEL_TITLE", artikel.title)
            intent.putExtra("ARTIKEL_DATE", artikel.date)
            intent.putExtra("ARTIKEL_CONTENT", artikel.content)
            intent.putExtra("ARTIKEL_IMAGE", artikel.image.toString())
            startActivity(intent)
        }
    }


}
