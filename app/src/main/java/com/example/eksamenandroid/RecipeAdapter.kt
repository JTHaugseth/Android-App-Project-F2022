package com.example.eksamenandroid

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
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

// RecipeAdapter has 2 parameters -> Activity, to give current activity context, and allData with recipe Objects.
// RecipeAdapter is used by -> MainActivity, FavoritesActivity, TodaysMeals and SearchHistoryOnSelect.
class RecipeAdapter(private val activity: Activity, val allData: ArrayList<RecipeItems>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    // Reference to Report: Page 4-5 Recycler View
    // Populates the Recycler Views with recipe_items.xml.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recipe_items,
                parent,
                false
            )
        )
    }

    // Reference to Report: Page 4-5 Recycler View
    // Uses allData's object information to change recipe_items.xml values. Button-comments below ->
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentActivity = activity
        val selectButton = holder.itemView.findViewById<Button>(R.id.SelectButton)
        val favoriteButton = holder.itemView.findViewById<ImageButton>(R.id.FavoriteButton)
        val currentItem = allData[position]

        // Reference to Report: Page 3 What we did and the alternatives
        holder.itemView.apply {
            Picasso.get().load(currentItem.image).into(RecipeImage)
            RecipeName.text = currentItem.title
            CaloriesPerServing.text = "Calories per serving: ${currentItem.calories}"
            DietLabel.text = currentItem.dietLabel
            HealthLabel.text = currentItem.healthLabel
            Cautions.text = currentItem.cautions
            if(currentActivity is TodaysMeals) {selectButton.setText(R.string.REMOVE)}
            validateIfFavorite(favoriteButton, currentItem.title)
        }

        // SelectButton on each activity will populate TodaysMeals-database.
        // When the user is in the TodaysMeals Activity, the selectButton will change to "Remove", and will delete the selected item from the database
        // and re-render the activity.
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
        // FavoriteButton on each activity will populate FavoritesActivity-database
        // When the user in in the Favorites Activity, the favorite button will remove the items from the favorites database and re-render the activity.
        // The Favorites button will be red, when the selected item is liked, and grey when not.
        favoriteButton.setOnClickListener {
            when (currentActivity) {
                is MainActivity, is SearchHistoryOnSelect, is TodaysMeals -> {
                    val recipesDB = RecipesDB(activity)
                    val db = recipesDB.readableDatabase
                    val cursor = db.rawQuery("SELECT * FROM Favorites WHERE title = ?", arrayOf(currentItem.title))

                    if (!cursor.moveToFirst()) {
                        populateFavorites(currentItem.title, currentItem.image, currentItem.calories, currentItem.dietLabel,
                            currentItem.healthLabel, currentItem.cautions, currentItem.url)
                        favoriteButton.setImageTintList(ColorStateList.valueOf(Color.RED))
                    } else {
                        db.delete("Favorites", "title = ?", arrayOf(currentItem.title))
                        favoriteButton.setImageTintList(ColorStateList.valueOf(Color.DKGRAY))
                    }
                    cursor.close()
                    db.close()
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

    // This function checks each object in allData. If the current object matches an object in favorites-database,
    // it will change the favorite icon to red.
    private fun validateIfFavorite(favoriteButton: ImageButton, title: String?) {
        val recipesDB = RecipesDB(activity)
        val db = recipesDB.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Favorites WHERE title = ?", arrayOf(title))

        if (cursor.moveToFirst()) {
            favoriteButton.setImageTintList(ColorStateList.valueOf(Color.RED))
        } else {
            favoriteButton.setImageTintList(ColorStateList.valueOf(Color.DKGRAY))
        }
        cursor.close()
        db.close()
    }

    // Sets the Recycler view size to allData size.
    override fun getItemCount(): Int {
        return allData.size
    }

    // When this function is called, it will add the current items data to the Todays meals database. 
    // It will then send the user to the TodaysMeals activity right before it opens a new browser window with the corresponding recipe url. 
    // When the user returns to the app, they will already be in the Todays Meals activity. 
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
        
        db.close()

        val intent = Intent(view.context, TodaysMeals::class.java)
        view.context.startActivity(intent)

        val openURLIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        view.context.startActivity(openURLIntent)
    }

    // When the user clicks the favorite button, it will populate the Favorites database with the object information. 
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
        
        db.close()
    }
}














