package com.turahan.dev.volunteer

import android.os.Bundle
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.turahan.dev.R
import com.turahan.dev.databinding.ActivityVolunteerMainBinding

class VolunteerMain : AppCompatActivity() {

    private lateinit var binding: ActivityVolunteerMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolunteerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.vol_nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        setupVolSmoothBottomMenu()
    }

    private fun setupVolSmoothBottomMenu() {
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.volunteer_menu_bottom)
        val menu = popupMenu.menu
        binding.botNavView.setupWithNavController(menu, navController)
    }
}