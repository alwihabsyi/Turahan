package com.turahan.dev.volunteer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import com.turahan.dev.databinding.FragmentVolunteerHomeBinding

class VolunteerHomeFragment : Fragment() {

    private lateinit var binding: FragmentVolunteerHomeBinding
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: FragmentVolHomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVolunteerHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager2 = binding.viewPagerVolHome
        tabLayout = binding.tabLayout

        adapter = FragmentVolHomeAdapter(requireActivity().supportFragmentManager, lifecycle)
        tabLayout.addTab(tabLayout.newTab().setText("Pending Orders"))
        tabLayout.addTab(tabLayout.newTab().setText("Processed Orders"))
        tabLayout.addTab(tabLayout.newTab().setText("Success Orders"))

        viewPager2.adapter = adapter

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: Tab?) {
                if(tab != null){
                    viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: Tab?) {
            }

            override fun onTabReselected(tab: Tab?) {
            }

        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }
}