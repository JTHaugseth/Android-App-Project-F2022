package com.example.eksamenandroid

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.search_history_items.view.*

class SearchHistoryAdapter(private val activity: Activity, val allData: ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.search_history_items,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = allData[position]

        holder.itemView.apply {
            SearchHistoryInputText.text = currentItem
        }
    }

    override fun getItemCount(): Int {
        return allData.size
    }
}