package team2.kakigowhere.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import team2.kakigowhere.R
import team2.kakigowhere.data.api.MLRetrofitClient
import team2.kakigowhere.data.api.RecommendRequest
import team2.kakigowhere.data.model.PlaceViewModel

class HomeFragment : Fragment() {

    private val placeViewModel: PlaceViewModel by activityViewModels()
    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Retrieve stored user name
        val prefs = requireContext().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val userName = prefs.getString("user_name", "Guest") ?: "Guest"
        view.findViewById<TextView>(R.id.tvGreeting).text = "Hi, $userName"

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerSuggestions)
        val emptyView = view.findViewById<TextView>(R.id.tvEmpty)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = PlaceAdapter(emptyList()) { place ->
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToDetailFragment(place.id)
            )
        }
        recyclerView.adapter = adapter

        // observe live data from Place view model
        placeViewModel.places.observe(viewLifecycleOwner) { places ->
            if (places == null) return@observe

            viewLifecycleOwner.lifecycleScope.launch {
                // interests from SharedPrefs (what ML expects)
                val interests = prefs.getStringSet("user_interest_names", emptySet())
                    ?.toList()
                    .orEmpty()

                if (interests.isEmpty()) {
                    showEmpty(emptyView, recyclerView)
                    adapter.update(emptyList())
                    return@launch
                }

                // stable key for these interests
                val interestsKey = interests.sorted().joinToString(",")
                // reuse cache if interests unchanged, else refetch + cache
                val recIds = loadRecIds(prefs, interestsKey)
                    ?: fetchAndCacheRecIds(prefs, interestsKey, interests)

                val byId = places.associateBy { it.id } // build a Map of <Long, PlaceDTO>
                val recPlaces = recIds.mapNotNull { byId[it] } // for each id, look up in the Map

                if (recPlaces.isEmpty()) {
                    showEmpty(emptyView, recyclerView)
                    adapter.update(emptyList())
                } else {
                    adapter.update(recPlaces)
                    showList(recyclerView, emptyView)
                }
            }
        }

        // observe errors from Place view model
        placeViewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }
        }
    }

    private fun showEmpty(emptyView: View, list: View) {
        emptyView.visibility = View.VISIBLE
        list.visibility = View.GONE
    }

    private fun showList(list: View, emptyView: View) {
        list.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
    }

    private fun loadRecIds(prefs: SharedPreferences, interestsKey: String): List<Long>? {
        val cachedKey = prefs.getString("reco_interests_key", null)
        val json = prefs.getString("reco_ids_json", null)
        if (cachedKey != interestsKey || json.isNullOrBlank()) return null
        return runCatching {
            val arr = JSONArray(json)
            List(arr.length()) { i -> arr.getLong(i) }
        }.getOrNull()
    }

    private suspend fun fetchAndCacheRecIds(
        prefs: SharedPreferences,
        interestsKey: String,
        interests: List<String>
    ): List<Long> {
        val ids = runCatching {
            withContext(Dispatchers.IO) {
                MLRetrofitClient.api.getRecommendations(RecommendRequest(interests))
            }.map { it.id }
        }.getOrElse { emptyList() }

        if (ids.isNotEmpty()) {
            prefs.edit()
                .putString("reco_interests_key", interestsKey)
                .putString("reco_ids_json", JSONArray(ids).toString())
                .apply()
        }
        return ids
    }
}



