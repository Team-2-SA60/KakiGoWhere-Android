package team2.kakigowhere.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import team2.kakigowhere.R
import team2.kakigowhere.data.api.ApiConstants
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.PlaceDTO

class TestActivity : AppCompatActivity() {

    private lateinit var container: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_test)

        // Apply edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        container = findViewById(R.id.main)

        findViewById<Button>(R.id.test_api).setOnClickListener {
            // clear previous content
            container.removeAllViews()

            // fetch places from API
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.api.getPlaces()
                    if (response.isSuccessful) {
                        val places: List<PlaceDTO> = response.body()!!
                        places.forEach { dto ->
                            // create an ImageView for each place
                            val iv = ImageView(this@TestActivity).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    500  // px height, or wrap_content / dp-to-px conversion
                                )
                                scaleType = ImageView.ScaleType.CENTER_CROP
                            }

                            // load image via Glide
                            val imageUrl = ApiConstants.IMAGE_URL + dto.googleId
                            Glide.with(this@TestActivity)
                                .load(imageUrl)
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.error_image)
                                .into(iv)

                            // add to container
                            container.addView(iv)
                        }
                    } else {
                        Log.e("TestActivity", "API error code: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("TestActivity", "Network error fetching places", e)
                }
            }
        }
    }
}
