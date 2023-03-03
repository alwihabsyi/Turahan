package com.turahan.dev.volunteer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.turahan.dev.databinding.FragmentVolunteerHomeBinding

class VolunteerHomeFragment : Fragment() {

    private lateinit var binding: FragmentVolunteerHomeBinding

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

        val viewPager = binding.viewPagerVolHome
        val tabLayout = binding.tabLayout

        val fragmentAdapter = FragmentVolHomeAdapter(activity?.supportFragmentManager!!)
        fragmentAdapter.addFragment(VolPendingOrder(), "Pending Orders")
        fragmentAdapter.addFragment(VolSuccessOrder(), "Success Orders")

        viewPager.adapter = fragmentAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

}