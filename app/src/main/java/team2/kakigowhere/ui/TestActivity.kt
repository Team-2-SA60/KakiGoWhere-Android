package team2.kakigowhere.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
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
                    }
                } catch (e: Exception) {
                    Log.d("API Error", "Cannot fetch from API")
                    Log.d("API Error", e.toString())
                }

            }
        }
    }
}