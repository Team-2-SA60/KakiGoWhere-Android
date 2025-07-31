package team2.kakigowhere

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

class MainFragment : Fragment() {

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        setupBottomNavBar(view)
    }

    private fun setupBottomNavBar(root: View) {
        root.findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            navigateSafe(R.id.homeFragment)
        }

        val notImplemented = {
            Toast.makeText(requireContext(), "Coming soon!", Toast.LENGTH_SHORT).show()
        }

        root.findViewById<LinearLayout>(R.id.nav_map).setOnClickListener { notImplemented() }
        root.findViewById<LinearLayout>(R.id.nav_explore).setOnClickListener { notImplemented() }
        root.findViewById<LinearLayout>(R.id.nav_saved).setOnClickListener { notImplemented() }
        root.findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener { notImplemented() }
    }

    private fun navigateSafe(destinationId: Int) {
        if (navController.currentDestination?.id != destinationId) {
            navController.navigate(destinationId)
        }
    }
}
