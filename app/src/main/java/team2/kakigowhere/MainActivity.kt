package team2.kakigowhere

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import team2.kakigowhere.data.model.ItineraryViewModel
import team2.kakigowhere.data.model.PlaceViewModel

class MainActivity : AppCompatActivity() {

    private val placeViewModel: PlaceViewModel by viewModels()
    private val itineraryViewModel: ItineraryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0) // no bottom padding to account for bottom nav bar
            insets
        }

        // TODO: use SharedPreferences to get email.
        val email = "cy@kaki.com"

        // make api call to load information
        placeViewModel.loadPlaces()
        itineraryViewModel.loadItineraries(email)

        // sets up navigation host and find navigation controller
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // sets bottom navigation view with the navigation controller
        val navView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(
                        R.id.homeFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(navController.graph.startDestinationId, false)
                            .setLaunchSingleTop(true)
                            .build()
                    )
                    true
                }
                R.id.mapFragment -> {
                    navController.navigate(
                        R.id.mapFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(navController.graph.startDestinationId, false)
                            .setLaunchSingleTop(true)
                            .build()
                    )
                    true
                }
                R.id.exploreFragment -> {
                    navController.navigate(
                        R.id.exploreFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(navController.graph.startDestinationId, false)
                            .setLaunchSingleTop(true)
                            .build()
                    )
                    true
                }
                R.id.savedFragment -> {
                    navController.navigate(
                        R.id.savedFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(navController.graph.startDestinationId, false)
                            .setLaunchSingleTop(true)
                            .build()
                    )
                    true
                }
                R.id.profileFragment -> {
                    navController.navigate(
                        R.id.profileFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(navController.graph.startDestinationId, false)
                            .setLaunchSingleTop(true)
                            .build()
                    )
                    true
                }
                else -> false
            }
        }
    }
}