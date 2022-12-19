package com.example.eksamenandroid

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.search_history_items.view.*

// SearchHistoryAdapter has 2 parameters -> Activity, to give current activity context, and allData with recipe Objects.
// SearchHistoryAdapter is used by -> SearchHistory.
class SearchHistoryAdapter(private val activity: Activity, val allData: ArrayList<SearchHistory.SearchHistoryItems>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    // Populates the Recycler Views with search_history_items.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.search_history_items,
                parent,
                false
            )
        )
    }

    // Uses allData's object information to change search_history_items.xml values. Button-comment below ->
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentActivity = activity
        val searchHistoryButton = holder.itemView.findViewById<Button>(R.id.SearchHistoryInputButton)
        val currentItem = allData[position]

        holder.itemView.apply {
            SearchHistoryInputText.text = currentItem.searchInput
        }

        // When the user presses search in SearchHistory, they will be sent to the SearchHistoryOnSelect activity.
        // The selected search history will also pass the selected objects title and search url through the intent. 
        searchHistoryButton.setOnClickListener {
            when (currentActivity) {
                is SearchHistory-> {
                    val intent = Intent(it.context, SearchHistoryOnSelect::class.java)
                    intent.putExtra("URL", currentItem.searchURL)
                    intent.putExtra("TITLE", currentItem.searchInput)
                    it.context.startActivity(intent)
                }                
            }
        }
    }

    // Sets the Recycler view size to allData size.
    override fun getItemCount(): Int {
        return allData.size
    }
}