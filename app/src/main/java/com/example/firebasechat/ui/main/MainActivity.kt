package com.example.firebasechat.ui.main

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.example.firebasechat.R
import com.example.firebasechat.databinding.ActivityMainBinding
import com.example.firebasechat.databinding.NavHeaderMainBinding
import com.example.firebasechat.ui.home.HomeFragmentDirections
import com.example.firebasechat.ui.settings.SettingsFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var headerBinding: NavHeaderMainBinding

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).also {
                it.viewModel = mainViewModel
                it.lifecycleOwner = this
            }

        headerBinding =
            NavHeaderMainBinding.bind(activityMainBinding.navView.getHeaderView(0)).also {
                it.viewModel = mainViewModel
                it.lifecycleOwner = this
            }

        setSupportActionBar(activityMainBinding.appBarMain.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_settings, R.id.authorizationFragment
            ), activityMainBinding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        activityMainBinding.navView.setupWithNavController(navController)

        mainViewModel.getUserLiveData().observe(this, Observer {
            if (it.uid == null) {
                activityMainBinding.apply {
                    drawerLayout.close()
                    // Hide items from menu
                    navView.menu.findItem(R.id.nav_home).isVisible = false
                    navView.menu.findItem(R.id.nav_settings).isVisible = false
                }
                // Navigate to Login screen
                when (navController.currentDestination?.label) {
                    getString(R.string.menu_home) -> navController.navigate(
                        HomeFragmentDirections.actionNavHomeToAuthorizationFragment()
                    )
                    getString(R.string.menu_settings) -> navController.navigate(
                        SettingsFragmentDirections.actionNavSettingsToAuthorizationFragment()
                    )
                }
            } else {
                activityMainBinding.apply {
                    navView.menu.findItem(R.id.nav_home).isVisible = true
                    navView.menu.findItem(R.id.nav_settings).isVisible = true
                }
            }
        })

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (activityMainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            activityMainBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}