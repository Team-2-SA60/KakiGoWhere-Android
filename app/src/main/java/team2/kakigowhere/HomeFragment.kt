package team2.kakigowhere

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class HomeFragment : Fragment() {

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

        val suggestions = listOf(
            PlaceSuggestion("Marina Bay Sands", 4.5, "Entertainment, Shopping", R.drawable.marina_bay_sands),
            PlaceSuggestion("Singapore Zoo", 4.0, "Wildlife and Zoos", R.drawable.marina_bay_sands),
            PlaceSuggestion("Sentosa", 4.0, "Entertainment", R.drawable.marina_bay_sands)
        )

        recycler.adapter = PlaceAdapter(suggestions)
    }
}

