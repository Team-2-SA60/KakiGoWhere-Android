package team2.kakigowhere.ui

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.MaterialToolbar
import team2.kakigowhere.R

class WebViewFragment : Fragment(R.layout.fragment_web_view) {

    private val args: WebViewFragmentArgs by navArgs()
    private lateinit var webView: WebView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webView)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.webToolbar)

        // close with the X
        toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        // WebView setup
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        webView.loadUrl(args.url)

        // when back is pressed, go back in page history first
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (webView.canGoBack()) webView.goBack()
                    else findNavController().popBackStack()
                }
            }
        )
    }

    override fun onDestroyView() {
        webView.destroy()
        super.onDestroyView()
    }
}