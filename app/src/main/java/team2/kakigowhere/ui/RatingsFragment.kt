package team2.kakigowhere.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.adapters.RatingsAdapter
import team2.kakigowhere.databinding.FragmentRatingsBinding

class RatingsFragment : Fragment() {
    private var _binding: FragmentRatingsBinding? = null
    private val binding get() = _binding!!

    // hardcoded values for testing
    private val placeId = 7L
    private val touristId = 1L

    private lateinit var ratingsAdapter: RatingsAdapter
    private var alreadyLoaded = false // prevents re-fetching when view is recreated quickly

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRatingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ratingsAdapter = RatingsAdapter(emptyList())
        binding.rvOtherRatings.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ratingsAdapter
        }

        if (!alreadyLoaded) {
            loadRatings()
        }
    }

    // api calls to load ratings
    private fun loadRatings() {
        alreadyLoaded = true

        lifecycleScope.launch {
            // run parallel and wait for all
            val summaryDeferred = async(Dispatchers.IO) { RetrofitClient.api.getRatingSummary(placeId) }
            val myDeferred = async(Dispatchers.IO) { RetrofitClient.api.getMyRating(placeId, touristId) }
            val othersDeferred = async(Dispatchers.IO) { RetrofitClient.api.getOtherRatings(placeId, touristId) }
            val placeDetailDeferred = async(Dispatchers.IO) { RetrofitClient.api.getPlaceDetail(placeId) }

            // place title
            val placeResp = placeDetailDeferred.await()
            if (placeResp.isSuccessful) {
                placeResp.body()?.let { dto ->
                    binding.placeTitle.text = dto.name
                    Log.d("RatingsFragment", "Loaded place title: ${dto.name}")
                }
            } else {
                Log.w("RatingsFragment", "Place detail failed: ${placeResp.code()}")
            }

            // summary
            val summaryResp = summaryDeferred.await()
            if (summaryResp.isSuccessful) {
                summaryResp.body()?.let { summary ->
                    binding.averageRating.text = String.format("%.1f / 5", summary.averageRating)
                    binding.ratingCount.text = "${summary.ratingCount} rating(s)"
                    Log.d("RatingsFragment", "Summary loaded: avg=${summary.averageRating}, count=${summary.ratingCount}")
                }
            } else {
                Log.w("RatingsFragment", "Summary failed: ${summaryResp.code()}")
            }

            // my rating
            val myResp = myDeferred.await()
            if (myResp.isSuccessful) {
                val my = myResp.body()
                if (my != null) {
                    binding.tvMyName.text = my.touristName
                    binding.tvMyRating.text = "${my.rating} / 5"
                    binding.tvMyComment.text = my.comment ?: ""
                    binding.myRating.visibility = View.VISIBLE
                    Log.d("RatingsFragment", "My rating: id=${my.ratingId}, rating=${my.rating}, comment=${my.comment}")
                } else {
                    binding.myRating.visibility = View.GONE
                    Log.d("RatingsFragment", "No personal rating found")
                }
            } else {
                binding.myRating.visibility = View.GONE
                Log.w("RatingsFragment", "My rating request failed: ${myResp.code()}")
            }

            // others
            val othersResp = othersDeferred.await()
            if (othersResp.isSuccessful) {
                othersResp.body()?.let { list ->
                    ratingsAdapter.submitList(list)
                    Log.d("RatingsFragment", "Other ratings loaded count=${list.size}")
                }
            } else {
                Log.w("RatingsFragment", "Other ratings failed: ${othersResp.code()}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}