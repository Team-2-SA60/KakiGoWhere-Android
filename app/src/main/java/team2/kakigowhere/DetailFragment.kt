package team2.kakigowhere

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class DetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val backButton: TextView = view.findViewById(R.id.backButton)
        val websiteLink: TextView = view.findViewById(R.id.websiteLink)


        backButton.setOnClickListener {
            // Pops the current fragment off the back stack
            parentFragmentManager.popBackStack()
        }

        websiteLink.setOnClickListener {
            val url = websiteLink.text.toString()
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
    }
}
