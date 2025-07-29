package team2.kakigowhere

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load the default fragment (e.g., Home)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()

        // Set up custom bottom nav bar interactions
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

//        findViewById<LinearLayout>(R.id.nav_map).setOnClickListener {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, MapFragment())
//                .commit()
//        }
//
//        findViewById<LinearLayout>(R.id.nav_explore).setOnClickListener {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, ExploreFragment())
//                .commit()
//        }
//
//        findViewById<LinearLayout>(R.id.nav_saved).setOnClickListener {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, SavedFragment())
//                .commit()
//        }
//
//        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, ProfileFragment())
//                .commit()
//        }

    }
}
