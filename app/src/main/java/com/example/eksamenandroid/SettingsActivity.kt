package com.example.eksamenandroid

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner

class SettingsActivity() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        // Bruka onCreate til å kjøre testa mot functions.

        //insert()
        //read()
        //update()
        //delete()
    }

    private val recipesDB = RecipesDB(this)


    // Dinne funka sann den skal.
    fun insert() {
        val db = recipesDB.writableDatabase
        val values = ContentValues()
        values.put("id", 1)
        values.put("calories", 3000)
        values.put("history_items", 20)
        values.put("diet", "Vegetarian")
        values.put("cuisine", "Norwegian")
        values.put("mealtype", "Dinner")
        values.put("url", "ThisURL")
        db.insert("Settings", null, values)
    }

    // Dinne funka sann den skal.
    fun read() {
        val db = recipesDB.readableDatabase
        val cursor = db.query("Settings", null, null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val calories = cursor.getString(cursor.getColumnIndexOrThrow("calories"))
            val history_items = cursor.getString(cursor.getColumnIndexOrThrow("history_items"))
            val diet = cursor.getString(cursor.getColumnIndexOrThrow("diet"))
            val cuisine = cursor.getString(cursor.getColumnIndexOrThrow("cuisine"))
            val mealtype = cursor.getString(cursor.getColumnIndexOrThrow("mealtype"))
            val url = cursor.getString(cursor.getColumnIndexOrThrow("url"))

            Log.i("Settings","id: $id, calories: $calories, History-Items: $history_items, Diet: $diet, Cuisine: $cuisine, Meal-Type: $mealtype, URL: $url")
        }
    }

    // Dinne funka sann den skal.
    fun update() {
        val db = recipesDB.writableDatabase
        val values = ContentValues()
        values.put("calories", 5000)
        values.put("history_items", 10)
        values.put("mealtype", "Breakfast")

        db.update("Settings", values, "id = ?", arrayOf("1"))
    }

    // Dinne funka sann den skal.
    fun delete() {
        val db = recipesDB.writableDatabase
        db.delete("Settings", "id = ?", arrayOf("1"))
    }

    fun saveAndReturn(view: View) {
        val caloriesInput = findViewById<EditText>(R.id.CaloriesInput).text.toString().toInt()
        val maxHistoryItemsInput = findViewById<EditText>(R.id.MaxHistoryItemsInput).text.toString().toInt()
        val dietDropDown = findViewById<Spinner>(R.id.DietDropDown)
        val cuisineDropDown = findViewById<Spinner>(R.id.CuisineDropDown)
        val mealTypeDropDown = findViewById<Spinner>(R.id.MealTypeDropDown)

        var dietInput: String? = null
        var cuisineInput: String? = null
        var mealTypeInput: String? = null

        dietDropDown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                dietInput = parent.getItemAtPosition(position) as String
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        cuisineDropDown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                cuisineInput = parent.getItemAtPosition(position) as String
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        mealTypeDropDown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                mealTypeInput = parent.getItemAtPosition(position) as String
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        Log.i("Current update rapport", "Calories: $caloriesInput, Max History Items: $maxHistoryItemsInput, Diet: $dietInput, Cuisine: $cuisineInput, MealType: $mealTypeInput")

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}




