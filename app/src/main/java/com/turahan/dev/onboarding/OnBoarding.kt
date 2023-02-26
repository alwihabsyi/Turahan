package com.turahan.dev.onboarding

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.turahan.dev.R
import com.turahan.dev.sign.LoginActivity

class OnBoarding : AppCompatActivity() {
    private lateinit var onBoardingAdapter: OnBoardingAdapter
    private lateinit var onBoardingViewPager: ViewPager2
    private lateinit var layoutOnBoardingIndicators: LinearLayout
    private lateinit var btnNext: CardView
    private lateinit var tvBtnNext: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        onBoardingViewPager = findViewById(R.id.onBoardingViewPager)
        layoutOnBoardingIndicators = findViewById(R.id.dots)
        btnNext = findViewById(R.id.btn_next)
        tvBtnNext = findViewById(R.id.tv_btnNext)
        supportActionBar?.hide()

        setUpOnBoardingItems()
        onBoardingViewPager.adapter = onBoardingAdapter

        setUpOnBoardingItems()
        setUpOnBoardingIndicators()
        setCurrentOnBoardingIndicators(0)

        onBoardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentOnBoardingIndicators(position)
            }
        })

        btnNext.setOnClickListener {
            if (onBoardingViewPager.currentItem + 1 < onBoardingAdapter.itemCount) {
                onBoardingViewPager.currentItem = onBoardingViewPager.currentItem + 1
            } else {
                val intent = Intent(this@OnBoarding, LoginActivity::class.java)
                startActivity(intent)
                onBoardingFinished()
            }
        }

    }

    private fun setUpOnBoardingItems() {
        val onBoardingItems: MutableList<OnBoardingItem> = ArrayList()

        val onBoarding1 = OnBoardingItem(
            resources.getString(R.string.first_slide_title),
            resources.getString(R.string.first_slide_description),
            R.drawable.ob_1
        )
        val onBoarding2 = OnBoardingItem(
            resources.getString(R.string.second_slide_title),
            resources.getString(R.string.second_slide_description),
            R.drawable.ob_2
        )
        val onBoarding3 = OnBoardingItem(
            resources.getString(R.string.third_slide_title),
            resources.getString(R.string.third_slide_description),
            R.drawable.ob_3
        )

        onBoardingItems.add(onBoarding1)
        onBoardingItems.add(onBoarding2)
        onBoardingItems.add(onBoarding3)

        onBoardingAdapter = OnBoardingAdapter()
        onBoardingAdapter.setNewItem(onBoardingItems)
    }

    private fun setUpOnBoardingIndicators() {
        val indicators = arrayOfNulls<ImageView>(onBoardingAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            layoutOnBoardingIndicators.addView(indicators[i])
        }
    }

    private fun setCurrentOnBoardingIndicators(index: Int) {
        val childCount = layoutOnBoardingIndicators.childCount
        for (i in 0 until childCount) {
            val imageView = layoutOnBoardingIndicators[i] as ImageView

            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
        if (index == onBoardingAdapter.itemCount - 1) {
            tvBtnNext.text = resources.getString(R.string.let_get_started)
        } else {
            tvBtnNext.text = resources.getString(R.string.next_btn)
        }
    }

    private fun onBoardingFinished() {
        val sharedPref = getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }
}