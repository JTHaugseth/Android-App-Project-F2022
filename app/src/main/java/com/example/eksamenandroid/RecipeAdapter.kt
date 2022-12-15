package com.example.eksamenandroid

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat.recreate
import androidx.core.app.ActivityCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recipe_items.view.*

class RecipeAdapter(private val activity: Activity, val allData: ArrayList<RecipeItems>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

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

        holder.itemView.apply {
            Picasso.get().load(currentItem.image).into(RecipeImage)
            RecipeName.text = currentItem.title
            CaloriesPerServing.text = "Calories per serving: ${currentItem.calories}"
            DietLabel.text = currentItem.dietLabel
            HealthLabel.text = currentItem.healthLabel
            Cautions.text = currentItem.cautions
            if(currentActivity is TodaysMeals) {selectButton.setText(R.string.REMOVE)}
        }

        selectButton.setOnClickListener {
            when (currentActivity) {
                is MainActivity -> {
                    val recipesDB = RecipesDB(activity)
                    val db = recipesDB.writableDatabase
                    val values = ContentValues()
                    values.put("title", currentItem.title)
                    values.put("image", currentItem.image)
                    values.put("calories", currentItem.calories)
                    values.put("dietLabel", currentItem.dietLabel)
                    values.put("healthLabel", currentItem.healthLabel)
                    values.put("cautions", currentItem.cautions)
                    db.insert("TodaysMeals", null, values)

                    Log.i("Activity access", "${currentItem.title} added")
                    db.close()

                    val intent = Intent(it.context, TodaysMeals::class.java)
                    it.context.startActivity(intent)

                    val openURLIntent = Intent(Intent.ACTION_VIEW, Uri.parse(currentItem.url))
                    it.context.startActivity(openURLIntent)
                }
                is TodaysMeals -> {
                    val recipesDB = RecipesDB(activity)
                    val db = recipesDB.writableDatabase
                    db.delete("TodaysMeals", "title = ?", arrayOf(currentItem.title))
                    db.close()
                    recreate(activity)
                }
                is SearchHistoryOnSelect -> {
                    val recipesDB = RecipesDB(activity)
                    val db = recipesDB.writableDatabase
                    val values = ContentValues()
                    values.put("title", currentItem.title)
                    values.put("image", currentItem.image)
                    values.put("calories", currentItem.calories)
                    values.put("dietLabel", currentItem.dietLabel)
                    values.put("healthLabel", currentItem.healthLabel)
                    values.put("cautions", currentItem.cautions)
                    db.insert("TodaysMeals", null, values)

                    Log.i("Activity access", "${currentItem.title} added")
                    db.close()

                    val intent = Intent(it.context, TodaysMeals::class.java)
                    it.context.startActivity(intent)

                    val openURLIntent = Intent(Intent.ACTION_VIEW, Uri.parse(currentItem.url))
                    it.context.startActivity(openURLIntent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return allData.size
    }
}














