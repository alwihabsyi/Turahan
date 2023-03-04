package com.turahan.dev.volunteer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.turahan.dev.databinding.FragmentViewPagerBinding

class FragmentVolHomeAdapter(fm: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return if (position == 0)
            VolPendingOrder()
        else if (position == 1)
            VolProcessedOrder()
        else
            VolSuccessOrder()
    }

}