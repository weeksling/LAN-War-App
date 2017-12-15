package com.mweeksconsulting.lanwarapp

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.mweeksconsulting.lanwarapp.ui.HomeFragment
import com.mweeksconsulting.lanwarapp.ui.RaffleActivity
import com.mweeksconsulting.lanwarapp.ui.SponsorsActivity
import com.mweeksconsulting.lanwarapp.ui.StaffActivity
import kotlinx.android.synthetic.main.activity_navigation.*


open class NavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun onCreateDrawer() {
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        Log.i ("NavigationActivity", "onCreateDrawer()")
        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        onCreateDrawer()
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        onCreateDrawer()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigation, menu)
        return true
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        when (item.itemId) {
            R.id.nav_home -> {
                startActivity(Intent(this, HomeFragment::class.java))
            }
            R.id.nav_raffles -> {
                startActivity( Intent(this, RaffleActivity::class.java))
            }
            R.id.nav_staff -> {
                startActivity( Intent(this, StaffActivity::class.java))
            }
            R.id.nav_sponsors -> {
                startActivity( Intent(this, SponsorsActivity::class.java))
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}
