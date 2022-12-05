package com.example.eksamenandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recipe_items.view.*

class RecipeAdapter(val allData: ArrayList<RecipeItems>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recipe_items,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = allData[position]
        holder.itemView.apply {
            RecipeName.text = currentItem.title
            DietLabel.text = currentItem.dietLabel
            HealthLabel.text = currentItem.healthLabel
            Cautions.text = currentItem.cautions
        }
    }

    override fun getItemCount(): Int {
        return allData.size
    }
}
