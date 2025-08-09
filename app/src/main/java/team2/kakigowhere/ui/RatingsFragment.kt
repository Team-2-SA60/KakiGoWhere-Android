package team2.kakigowhere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.adapters.RatingsAdapter
import team2.kakigowhere.databinding.FragmentRatingsBinding
import android.content.Context
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import java.util.Locale.*

class RatingsFragment : Fragment() {
    private var _binding: FragmentRatingsBinding? = null
    private val binding get() = _binding!!

    private val args: RatingsFragmentArgs by navArgs()
    private val placeId: Long get() = args.placeId
    private val placeTitle: String get() = args.placeTitle

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

        binding.backButton.setOnClickListener { findNavController().navigateUp() }
        binding.placeTitle.text = placeTitle

        binding.rvOtherRatings.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ratingsAdapter
        }

        if (!alreadyLoaded) loadRatings()

        binding.btnWriteEdit.setOnClickListener {
            // navigate to write/edit screen
            findNavController().navigate(
                RatingsFragmentDirections.actionRatingsToWriteRating(
                    placeId = placeId,
                    placeTitle = placeTitle
                )
            )
        }

        // handle result from fragment
        parentFragmentManager.setFragmentResultListener("rating_updated", viewLifecycleOwner) { _, _ ->
            alreadyLoaded = false
            loadRatings()
        }
    }

    private fun currentUserId(): Long =
        requireContext().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
            .getLong("user_id", -1L)

    // api calls to load ratings
    private fun loadRatings() {
        alreadyLoaded = true

        val userId = currentUserId() // defaults to -1 if not logged in

        viewLifecycleOwner.lifecycleScope.launch {
            val summaryDeferred = async(Dispatchers.IO) {
                RetrofitClient.api.getRatingSummary(placeId)
            }
            val myDeferred = if (userId > 0) {
                async(Dispatchers.IO) { RetrofitClient.api.getMyRating(placeId, userId) }
            } else null
            val othersDeferred = async(Dispatchers.IO) {
                RetrofitClient.api.getOtherRatings(placeId, if (userId > 0) userId else 0L)
            }

            // ratings summary
            val summaryResp = summaryDeferred.await()
            if (summaryResp.isSuccessful) {
                summaryResp.body()?.let { summary ->
                    binding.averageRating.text = formatRating(summary.averageRating)
                    binding.ratingCount.text = "${summary.ratingCount} rating(s)"
                }
            }

            // load my rating (only if logged in)
            if (myDeferred != null) {
                val myResp = myDeferred.await()
                if (myResp.isSuccessful) {
                    val my = myResp.body()
                    if (my != null) {
                        binding.tvMyName.text = my.touristName
                        binding.tvMyRating.text = formatRating(my.rating.toDouble())
                        binding.tvMyComment.text = my.comment ?: ""
                        binding.myRating.visibility = View.VISIBLE
                    } else {
                        binding.myRating.visibility = View.GONE
                    }
                } else {
                    binding.myRating.visibility = View.GONE
                }
            } else {
                binding.myRating.visibility = View.GONE
            }

            // load other ratings
            val othersResp = othersDeferred.await()
            if (othersResp.isSuccessful) {
                ratingsAdapter.submitList(othersResp.body().orEmpty())
            }
        }
    }

    private fun formatRating(v: Double): String {
        val i = v.toInt()
        return if (v == i.toDouble()) "$i / 5" else String.format(US, "%.1f / 5", v)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}