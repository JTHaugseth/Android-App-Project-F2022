package com.example.eksamenandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class FavoritesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorites)

        val rv = findViewById<RecyclerView>(R.id.FavoritesRV)
        val allData = read()
        rv.adapter = RecipeAdapter(this@FavoritesActivity, allData)
    }

    private fun read() : ArrayList<RecipeItems>{
        val allData = ArrayList<RecipeItems>()
        val recipesDB = RecipesDB(this)
        val db = recipesDB.readableDatabase
        val cursor = db.query("Favorites", null, null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val dataItem = RecipeItems()
            dataItem.title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            dataItem.image = cursor.getString(cursor.getColumnIndexOrThrow("image"))
            dataItem.calories = cursor.getInt(cursor.getColumnIndexOrThrow("calories"))
            dataItem.dietLabel = cursor.getString(cursor.getColumnIndexOrThrow("dietLabel"))
            dataItem.healthLabel = cursor.getString(cursor.getColumnIndexOrThrow("healthLabel"))
            dataItem.cautions = cursor.getString(cursor.getColumnIndexOrThrow("cautions"))
            dataItem.url = cursor.getString(cursor.getColumnIndexOrThrow("url"))

            allData.add(dataItem)
        }
        cursor.close()
        db.close()
        return allData
    }

    fun mainMenuButton(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}