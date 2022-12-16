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
import android.widget.ImageButton
import androidx.core.app.ActivityCompat.recreate
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
        val favoriteButton = holder.itemView.findViewById<ImageButton>(R.id.FavoriteButton)
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
                is MainActivity, is SearchHistoryOnSelect, is FavoritesActivity -> {
                    populateTodaysMeals(currentItem.title, currentItem.image, currentItem.calories, currentItem.dietLabel,
                        currentItem.healthLabel, currentItem.cautions, currentItem.url, it)
                }
                is TodaysMeals -> {
                    val recipesDB = RecipesDB(activity)
                    val db = recipesDB.writableDatabase
                    db.delete("TodaysMeals", "title = ?", arrayOf(currentItem.title))
                    db.close()
                    recreate(activity)
                }
            }
        }
        favoriteButton.setOnClickListener {
            when (currentActivity) {
                is MainActivity, is SearchHistoryOnSelect, is TodaysMeals -> {
                    populateFavorites(currentItem.title, currentItem.image, currentItem.calories, currentItem.dietLabel,
                        currentItem.healthLabel, currentItem.cautions, currentItem.url)
                }
                is FavoritesActivity -> {
                    val recipesDB = RecipesDB(activity)
                    val db = recipesDB.writableDatabase
                    db.delete("Favorites", "title = ?", arrayOf(currentItem.title))
                    db.close()
                    recreate(activity)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return allData.size
    }

    private fun populateTodaysMeals(
        title: String?,
        image: String?,
        calories: Int?,
        dietLabel: String?,
        healthLabel: String?,
        cautions: String?,
        url: String?,
        view: View
    ) {
        val recipesDB = RecipesDB(activity)
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
        db.close()

        val intent = Intent(view.context, TodaysMeals::class.java)
        view.context.startActivity(intent)

        val openURLIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        view.context.startActivity(openURLIntent)
    }

    private fun populateFavorites(
        title: String?,
        image: String?,
        calories: Int?,
        dietLabel: String?,
        healthLabel: String?,
        cautions: String?,
        url: String?
    ) {
        val recipesDB = RecipesDB(activity)
        val db = recipesDB.writableDatabase
        val values = ContentValues()
        values.put("title", title)
        values.put("image", image)
        values.put("calories", calories)
        values.put("dietLabel", dietLabel)
        values.put("healthLabel", healthLabel)
        values.put("cautions", cautions)
        values.put("url", url)
        db.insert("Favorites", null, values)

        Log.i("Activity access", "$title to favorites")
        db.close()
    }
}














