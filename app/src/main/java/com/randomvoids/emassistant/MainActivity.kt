package com.randomvoids.emassistant

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.randomvoids.emassistant.base.DatabindingActivity
import com.randomvoids.emassistant.view.ui.profile.UserProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.security.MessageDigest


@AndroidEntryPoint
class MainActivity : DatabindingActivity() {
    //TODO figure out if there's a way to inject the repo directly and skip loading the view model
    //maybe an initial setup fake view model?
    @VisibleForTesting val userProfileViewModel : UserProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //kill switch
        if("com" + ".random" + "voids" + ".em" + "assistant" != packageName)
            userProfileViewModel.clearOut()
        userProfileViewModel.seed()

        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_incident_list,
                R.id.navigation_live_incident,
                R.id.navigation_my_ics_214s,
                R.id.navigation_personal_section_view_pager_container
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //to get the hash for microsoft
        /*
        val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        for (signature in info.signatures) {
            val md: MessageDigest = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())
            Timber.d("MY KEY HASH: " + Base64.encodeToString(md.digest(), Base64.DEFAULT))
        }*/

    }
}