package com.example.eksamenandroid

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodaysMeals : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todays_meals)

        val rv = findViewById<RecyclerView>(R.id.TodaysMealsRV)
        val allData = read()
        rv.adapter = RecipeAdapter(this@TodaysMeals, allData)

        val currentCalories = getCurrentCalories()
        val caloriesAllowance = getCalorieAllowance()
        editCalorieAllowance(currentCalories, caloriesAllowance)
    }

    private fun read() : ArrayList<RecipeItems>{
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
        cursor.close()
        recipesDB.close()
        return allData
    }

    private fun getCalorieAllowance(): Int {
        var currentCalories = 0
        val recipesDB = RecipesDB(this)
        val db = recipesDB.readableDatabase
        val cursor = db.query("Settings", null, null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val calories = cursor.getString(cursor.getColumnIndexOrThrow("calories")).toInt()
            currentCalories += calories
        }
        recipesDB.close()
        return currentCalories
    }

    private fun getCurrentCalories(): Int {
        var currentCalories = 0
        val recipesDB = RecipesDB(this)
        val db = recipesDB.readableDatabase
        val cursor = db.query("TodaysMeals", null, null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val calories = cursor.getString(cursor.getColumnIndexOrThrow("calories")).toInt()
            currentCalories += calories
        }
        recipesDB.close()
        return currentCalories
    }

    private fun editCalorieAllowance(currentCalories: Int, caloriesAllowance: Int) {
        val calorieAllowanceText = findViewById<TextView>(R.id.TodaysCalories)
        calorieAllowanceText.setText("$currentCalories/$caloriesAllowance")

        if(currentCalories < caloriesAllowance) {
            calorieAllowanceText.setTextColor(Color.GREEN)
        }else {
            calorieAllowanceText.setTextColor(Color.RED)
        }
    }


    fun returnToMainMenu(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}