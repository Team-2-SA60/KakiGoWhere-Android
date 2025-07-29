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
import team2.kakigowhere.data.model.Place

class TestActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val apiBtn = findViewById<Button>(R.id.test_api)
        apiBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.test_api -> {
                lifecycleScope.launch {
                    try {
                        // api call to get list of all places
                        val response = RetrofitClient.api.getPlaces()

                        if (response.isSuccessful && (response.body() != null)) {
                            var places: List<Place> = response.body()!!
                            val layoutContainer = findViewById<ViewGroup>(R.id.main)

                            places.forEach { place ->
                                // initialise Image View for each place
                                val imageView = ImageView(this@TestActivity).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        700, // set layout width (px)
                                        500  // set layout height (px)
                                    )
                                }

                                // use Glide to call image and set Image View
                                val imagePath = ApiConstants.IMAGE_URL + place.imagePath
                                Glide.with(this@TestActivity)
                                    .load(imagePath)
                                    .into(imageView)

                                // add Image View to our layout
                                layoutContainer.addView(imageView)
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("API Error", "Cannot fetch from API")
                        Log.d("API Error", e.toString())
                    }
                }
            }
        }
    }
}
