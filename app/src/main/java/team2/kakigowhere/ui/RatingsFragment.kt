package team2.kakigowhere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import team2.kakigowhere.R
import team2.kakigowhere.adapters.RatingsAdapter
import team2.kakigowhere.data.model.RatingItem

class RatingsFragment : Fragment() {

    // mock values
    private val mockRatingAverage = 4.3
    private val mockRatingCount = 10
    private val mockMyRating: RatingItem? = RatingItem(
        id = 100,
        touristId = 1,
        touristName = "You",
        rating = 5,
        comment = "Loved it!"
    )
    private val mockOthers = listOf(
        RatingItem(101, 2, "Bob", 4, "Nice spot"),
        RatingItem(102, 3, "Carol", 3, "A bit crowded")
    )

    private lateinit var otherAdapter: RatingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_ratings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // rating summary
        view.findViewById<TextView>(R.id.averageRating).text =
            String.format("%.1f / 5", mockRatingAverage)
        view.findViewById<TextView>(R.id.ratingCount).text =
            "$mockRatingCount rating(s)"

        // tourist rating
        val myContainer = view.findViewById<View>(R.id.myRating)
        if (mockMyRating != null) {
            view.findViewById<TextView>(R.id.tvMyName).text = mockMyRating.touristName
            view.findViewById<TextView>(R.id.tvMyRating).text = "${mockMyRating.rating} / 5"
            view.findViewById<TextView>(R.id.tvMyComment).text = mockMyRating.comment
            myContainer.visibility = View.VISIBLE
        } else {
            myContainer.visibility = View.GONE
        }

        // other ratings
        val rv = view.findViewById<RecyclerView>(R.id.rvOtherRatings)
        rv.layoutManager = LinearLayoutManager(requireContext())
        otherAdapter = RatingsAdapter(mockOthers)
        rv.adapter = otherAdapter
    }
}