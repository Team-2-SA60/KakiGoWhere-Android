package team2.kakigowhere.ui.account

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import team2.kakigowhere.R
import team2.kakigowhere.data.model.InterestCategory

class CategoryAdapter(
    private val categories: List<InterestCategory>,
    //change selected name to selected id
    private val selected: MutableSet<Long>
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val checkBox: CheckBox = view.findViewById(R.id.category_checkbox)
        private val textView: TextView = view.findViewById(R.id.category_name)

        fun bind(cat: InterestCategory) {
            // 1) Detach listener so setting isChecked doesn’t retrigger it
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = selected.contains(cat.id)

            // 2) Re-attach listener with max-3 logic
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // If selecting a new one beyond the limit, block it
                    if (selected.size >= 3 && !selected.contains(cat.id)) {
                        Toast.makeText(
                            checkBox.context,
                            "You can only select up to 3 categories",
                            Toast.LENGTH_SHORT
                        ).show()
                        checkBox.isChecked = false  // revert visual
                    } else {
                        selected.add(cat.id)
                    }
                } else {
                    // Always allow unchecking
                    selected.remove(cat.id)
                }
            }

            // 3) Finally update the label
            textView.text = cat.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    /** Expose the selected set so the fragment can save it */
    fun getSelectedIds(): List<Long> = selected.toList()
}