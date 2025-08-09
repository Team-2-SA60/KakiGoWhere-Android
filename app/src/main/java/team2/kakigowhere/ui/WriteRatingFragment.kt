package team2.kakigowhere.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.RatingItem
import team2.kakigowhere.data.model.RatingRequest
import team2.kakigowhere.databinding.FragmentWriteRatingBinding

class WriteRatingFragment : Fragment() {
    private var _binding: FragmentWriteRatingBinding? = null
    private val binding get() = _binding!!

    private val args: WriteRatingFragmentArgs by navArgs()
    private val placeId: Long get() = args.placeId
    private val placeTitle: String? get() = args.placeTitle

    private var existingRatingId: Long? = null // to know if update vs create

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWriteRatingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Title from args; fallback to API if missing
        if (!placeTitle.isNullOrBlank()) {
            binding.placeTitle.text = placeTitle
        } else {
            loadPlaceTitle()
        }

        val userId = currentUserId()

        loadExistingRating(userId)

        binding.backButton.setOnClickListener { findNavController().navigateUp() }
        binding.btnSubmit.setOnClickListener { submitRating(userId) }
    }

    private fun currentUserId(): Long =
        requireContext().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
            .getLong("user_id", -1L)

    private fun loadPlaceTitle() {
        viewLifecycleOwner.lifecycleScope.launch {
            val placeResp = withContext(Dispatchers.IO) { RetrofitClient.api.getPlaceDetail(placeId) }
            if (placeResp.isSuccessful) {
                binding.placeTitle.text = placeResp.body()?.name ?: ""
            }
        }
    }

    private fun loadExistingRating(userId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            val resp = withContext(Dispatchers.IO) { RetrofitClient.api.getMyRating(placeId, userId) }
            if (resp.isSuccessful) {
                val ratingItem: RatingItem? = resp.body()
                if (ratingItem != null) {
                    existingRatingId = ratingItem.ratingId
                    binding.ratingBar.rating = ratingItem.rating.toFloat()
                    binding.comment.setText(ratingItem.comment ?: "")
                }
            }
        }
    }

    private fun submitRating(userId: Long) {
        val ratingValue = binding.ratingBar.rating.toInt().coerceIn(1, 5)
        if (binding.ratingBar.rating != ratingValue.toFloat()) {
            binding.ratingBar.rating = ratingValue.toFloat()
        }
        val commentText = binding.comment.text?.toString()?.trim().orEmpty()

        if (commentText.isEmpty()) {
            Toast.makeText(requireContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // construct request DTO
        val request = RatingRequest(
            rating = ratingValue,
            comment = commentText
        )

        viewLifecycleOwner.lifecycleScope.launch {
            val resp = withContext(Dispatchers.IO) {
                RetrofitClient.api.submitOrUpdateRating(placeId, userId, request)
            }
            if (resp.isSuccessful) {
                parentFragmentManager.setFragmentResult("rating_updated", Bundle.EMPTY)
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Failed to save rating", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}