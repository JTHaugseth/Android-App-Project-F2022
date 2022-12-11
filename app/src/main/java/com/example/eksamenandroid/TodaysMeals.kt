package com.example.eksamenandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class TodaysMeals : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todays_meals)
        setValues()
    }

    fun returnToMainMenu(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
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

}