package team2.kakigowhere.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import team2.kakigowhere.R
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.Place

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initButtons()
    }

    private fun initButtons() {
        val apiBtn = findViewById<Button>(R.id.test_api)
        apiBtn.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.api.getPlaces()
                    if (response.isSuccessful && (response.body() != null)) {
                        var places: List<Place> = response.body()!!
                        places.forEach { place ->
                            Log.i("API Success", "----------------------")
                            Log.i("API Success", place.toString())
                            Log.i("API Success", "----------------------")
                        }
                        var testPlace = places.get(0)
                        testPlace.image?.let { image ->
                            val bitmap = BitmapFactory.decodeByteArray(image.data, 0, image.data.size)
                            withContext(Dispatchers.Main) {
                                findViewById<ImageView>(R.id.test_image).setImageBitmap(bitmap)
                            }
                        }
                    } else {
                        Log.d("API Error", "Other issues with api call")
                    }
                } catch (e: Exception) {
                    Log.d("API Error", "Cannot fetch from API")
                    Log.d("API Error", e.toString())
                }
            }
        }
    }
}
