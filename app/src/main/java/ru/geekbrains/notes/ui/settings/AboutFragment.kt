package ru.geekbrains.notes.ui.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import ru.geekbrains.notes.R
import ru.geekbrains.notes.ui.MainActivity

class AboutFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("Debug1", "AboutFragment onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_about, container, false)
        Log.v("Debug1", "AboutFragment onCreateView")
        //getActivity().en
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textView = view.findViewById<TextView>(R.id.textView3)
        textView.text = HtmlCompat.fromHtml(getString(R.string.textAbout), HtmlCompat.FROM_HTML_MODE_LEGACY)

        //textView.setText(Html.fromHtml(getString(R.string.textAbout),Html.FROM_HTML_MODE_LEGACY));
        //((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("О программе...");
        //getActivity().setTitle("О программе...");
        MainActivity.setTitle(activity, "О программе...")
    }

    override fun onStart() {
        super.onStart()
        Log.v("Debug1", "AboutFragment onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.v("Debug1", "AboutFragment onStop")
    }

    override fun onResume() {
        super.onResume()
        Log.v("Debug1", "AboutFragment onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.v("Debug1", "AboutFragment onPause")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.v("Debug1", "AboutFragment onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v("Debug1", "AboutFragment onDestroy")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.v("Debug1", "AboutFragment onAttach")
    }

    override fun onDetach() {
        super.onDetach()
        Log.v("Debug1", "AboutFragment onDetach")
    }

    companion object {
        const val TAG = "AboutFragment"
    }
}