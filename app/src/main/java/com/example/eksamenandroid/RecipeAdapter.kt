package com.example.eksamenandroid

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recipe_items.view.*

class RecipeAdapter(private val activity: Activity, val allData: ArrayList<RecipeItems>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        val currentActivity = activity
        val selectButton = holder.itemView.findViewById<Button>(R.id.SelectButton)
        val currentItem = allData[position]

        selectButton.setOnClickListener {
            runSelect(currentActivity, currentItem.title, currentItem.image, currentItem.calories, currentItem.dietLabel, currentItem.healthLabel, currentItem.cautions)
        }

        holder.itemView.apply {
            Picasso.get().load(currentItem.image).into(RecipeImage)
            RecipeName.text = currentItem.title
            DietLabel.text = currentItem.dietLabel
            HealthLabel.text = currentItem.healthLabel
            Cautions.text = currentItem.cautions
        }
    }

    override fun getItemCount(): Int {
        return allData.size
    }

    private fun runSelect(currentActivity: Activity, title: String?, image: String?, calories: Int?, dietLabel: String?, healthLabel: String?, cautions: String?) {
        when (currentActivity) {
            is MainActivity -> {
                val recipesDB = RecipesDB(context)
                val db = recipesDB.writableDatabase
                val values = ContentValues()
                values.put("title", title)
                values.put("image", image)
                values.put("calories", calories)
                values.put("dietLabel", dietLabel)
                values.put("healthLabel", healthLabel)
                values.put("cautions", cautions)
                db.insert("TodaysMeals", null, values)

                Log.i("Activity access", "$title added")
            }
            //is TodaysMeals -> {

            //}
        }
    }
    }










