package team2.kakigowhere.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import team2.kakigowhere.PlaceAdapter
import team2.kakigowhere.PlaceRowItem
import team2.kakigowhere.R
import team2.kakigowhere.data.api.MLRetrofitClient
import team2.kakigowhere.data.api.RecommendRequest
import team2.kakigowhere.data.api.RetrofitClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeFragment : Fragment() {
    // mock interests for now
    private val touristInterests = listOf("Wildlife and Zoos")
    private lateinit var adapter: PlaceAdapter

    private val prefs by lazy { requireContext().getSharedPreferences("recommendation_cache", 0) }
    private var loading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val greeting = view.findViewById<TextView>(R.id.tvGreeting)
        greeting.text = "Hi, Adrian!" // or dynamic

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerSuggestions)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = PlaceAdapter(emptyList())
        recycler.adapter = adapter

        if (hasLoadedOnce() && loadIds().isNotEmpty()) {
            restoreFromIds()
        } else {
            loadRecommendations()
        }
    }

    private fun loadRecommendations() {
        if (hasLoadedOnce()) return
        loading = true
        markLoaded()

        val root = view ?: return
        val overlay = root.findViewById<View>(R.id.loadingOverlay)
        val loadingText = root.findViewById<TextView>(R.id.loadingText)
        overlay.visibility = View.VISIBLE
        loadingText.text = "Loading recommendations..."

        viewLifecycleOwner.lifecycleScope.launch {
            val suggestionItems = mutableListOf<PlaceRowItem>()
            val fetchedIds = mutableListOf<Long>()

            val mlRecs = try {
                withContext(Dispatchers.IO) {
                    val request = RecommendRequest(interests = touristInterests)
                    MLRetrofitClient.api.getRecommendations(request)
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "recommend API failed", e)
                fadeOutOverlay(overlay)
                return@launch
            }

            for (rec in mlRecs) {
                val row = buildPlaceRowItem(rec.id)
                if (row != null) {
                    suggestionItems.add(row)
                    fetchedIds.add(rec.id)
                }
            }

            if (suggestionItems.isEmpty()) {
                fadeOutOverlay(overlay)
                return@launch
            }

            adapter.submitList(suggestionItems)
            saveIds(fetchedIds)
            markLoaded()
            fadeOutOverlay(overlay)
        }
    }

    private fun restoreFromIds() {
        val root = view ?: return
        val overlay = root.findViewById<View>(R.id.loadingOverlay)
        val loadingText = root.findViewById<TextView>(R.id.loadingText)
        overlay.visibility = View.VISIBLE
        loadingText.text = "Loading recommendations..."

        viewLifecycleOwner.lifecycleScope.launch {
            val ids = loadIds()
            if (ids.isEmpty()) {
                fadeOutOverlay(overlay)
                return@launch
            }

            val restored = mutableListOf<PlaceRowItem>()
            for (id in ids) {
                val row = buildPlaceRowItem(id)
                if (row != null) restored.add(row)
            }

            if (restored.isNotEmpty()) {
                adapter.submitList(restored)
            }
            fadeOutOverlay(overlay)
        }
    }

    private suspend fun buildPlaceRowItem(id: Long): PlaceRowItem? {
        return try {
            val placeDetail = withContext(Dispatchers.IO) {
                RetrofitClient.api.getPlaceDetail(id).body()
            }
            if (placeDetail == null) return null
            val ratingValue: Double = placeDetail.averageRating ?: 0.0
            PlaceRowItem(
                id = id,
                name = placeDetail.name,
                rating = ratingValue
            )
        } catch (e: Exception) {
            Log.w("HomeFragment", "detail fetch failed id=$id", e)
            null
        }
    }

    private fun fadeOutOverlay(overlay: View?) {
        overlay?.animate()
            ?.alpha(0f)
            ?.setDuration(200)
            ?.withEndAction {
                overlay.visibility = View.GONE
                overlay.alpha = 1f
            }
    }

    // prefs helpers
    private fun hasLoadedOnce(): Boolean =
        prefs.getBoolean("loaded_once", false)

    private fun markLoaded() {
        prefs.edit().putBoolean("loaded_once", true).apply()
    }

    private fun saveIds(ids: List<Long>) {
        prefs.edit()
            .putStringSet("cached_place_ids", ids.map { it.toString() }.toSet())
            .apply()
    }

    private fun loadIds(): List<Long> =
        prefs.getStringSet("cached_place_ids", emptySet())
            ?.mapNotNull { it.toLongOrNull() } ?: emptyList()
}
