package com.example.eksamenandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodaysMeals : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todays_meals)
        setValues()

        val rv = findViewById<RecyclerView>(R.id.TodaysMealsRV)
        val allData = read()
        rv.adapter = RecipeAdapter(this, allData)
        Log.i("AlldataArray", allData.toString())
    }

    fun read() : ArrayList<RecipeItems>{
        val allData = ArrayList<RecipeItems>()
        val recipesDB = RecipesDB(this)
        val db = recipesDB.readableDatabase
        val cursor = db.query("TodaysMeals", null, null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val dataItem = RecipeItems()
            dataItem.title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            dataItem.image = cursor.getString(cursor.getColumnIndexOrThrow("image"))
            dataItem.calories = cursor.getInt(cursor.getColumnIndexOrThrow("calories"))
            dataItem.dietLabel = cursor.getString(cursor.getColumnIndexOrThrow("dietLabel"))
            dataItem.healthLabel = cursor.getString(cursor.getColumnIndexOrThrow("healthLabel"))
            dataItem.cautions = cursor.getString(cursor.getColumnIndexOrThrow("cautions"))

            allData.add(dataItem)
        }
        return allData
        Log.i("AlldataCLEANINFO", allData.toString())
    }

    fun setValues() {
        val calories = getCaloriesFromDb()
        val maxCalories = findViewById<TextView>(R.id.TodaysCalories)
        maxCalories.text = "0/"+calories
    }

    fun getCaloriesFromDb(): String {
        val recipesDB = RecipesDB(this)
        val db = recipesDB.readableDatabase
        val cursor = db.query("Settings", null, null, null, null, null, null, null)

        cursor.moveToFirst()
        val calories = cursor.getString(cursor.getColumnIndexOrThrow("calories"))

        cursor.close()
        db.close()

        return  calories
    }

    fun returnToMainMenu(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}