package com.example.eksamenandroid

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner

class SettingsActivity() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        insert()
        readAndSetViews()
    }

    private fun insert() {
        val recipesDB = RecipesDB(this)
        val db = recipesDB.writableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM Settings", null)

        if(cursor.moveToFirst()) {
            val count = cursor.getInt(0)
            if(count == 0) {
                val values = ContentValues()
                values.put("id", 1)
                values.put("calories", 3000)
                values.put("history_items", 10)
                values.put("diet", "balanced")
                values.put("cuisine", "American")
                values.put("mealtype", "Dinner")
                db.insert("Settings", null, values)
            }
        }
        cursor.close()
        recipesDB.close()
    }

    private fun readAndSetViews() {
        val caloriesText = findViewById<EditText>(R.id.CaloriesInput)
        val maxHistoryItemsText = findViewById<EditText>(R.id.MaxHistoryItemsInput)
        val dietDropDownChoice = findViewById<Spinner>(R.id.DietDropDown)
        val cuisineDropDownChoice = findViewById<Spinner>(R.id.CuisineDropDown)
        val mealTypeDropDownChoice = findViewById<Spinner>(R.id.MealTypeDropDown)

        val recipesDB = RecipesDB(this)
        val db = recipesDB.readableDatabase
        val cursor = db.query("Settings", null, null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val calories = cursor.getString(cursor.getColumnIndexOrThrow("calories"))
            val history_items = cursor.getString(cursor.getColumnIndexOrThrow("history_items"))
            val diet = cursor.getString(cursor.getColumnIndexOrThrow("diet"))
            val cuisine = cursor.getString(cursor.getColumnIndexOrThrow("cuisine"))
            val mealtype = cursor.getString(cursor.getColumnIndexOrThrow("mealtype"))

            caloriesText.setText("$calories")
            maxHistoryItemsText.setText("$history_items")

            val dietAdapter = dietDropDownChoice.adapter as ArrayAdapter<String>
            val selectedDietIndex = dietAdapter.getPosition(diet)
            dietDropDownChoice.setSelection(selectedDietIndex)

            val cuisineAdapter = cuisineDropDownChoice.adapter as ArrayAdapter<String>
            val selectedCuisineIndex = cuisineAdapter.getPosition(cuisine)
            cuisineDropDownChoice.setSelection(selectedCuisineIndex)

            val mealtypeAdapter = mealTypeDropDownChoice.adapter as ArrayAdapter<String>
            val selectedMealtypeIndex = mealtypeAdapter.getPosition(mealtype)
            mealTypeDropDownChoice.setSelection(selectedMealtypeIndex)

            Log.i("Settings","id: $id, calories: $calories, History-Items: $history_items, Diet: $diet, Cuisine: $cuisine, Meal-Type: $mealtype")
        }
        cursor.close()
        recipesDB.close()
    }

    private fun update(caloriesInput: Int, maxHistoryItemsInput: Int, dietInput: String, cuisineInput: String, mealTypeInput: String) {
        val recipesDB = RecipesDB(this)
        val db = recipesDB.writableDatabase
        val values = ContentValues()
        values.put("calories", caloriesInput)
        values.put("history_items", maxHistoryItemsInput)
        values.put("diet", dietInput)
        values.put("cuisine", cuisineInput)
        values.put("mealtype", mealTypeInput)

        db.update("Settings", values, "id = ?", arrayOf("1"))

        recipesDB.close()
    }

    fun delete() {
        val recipesDB = RecipesDB(this)
        val db = recipesDB.writableDatabase
        db.delete("Settings", "id = ?", arrayOf("1"))
        recipesDB.close()
    }

    fun saveAndReturn(view: View) {
        val caloriesInput = findViewById<EditText>(R.id.CaloriesInput).text.toString().toInt()
        val maxHistoryItemsInput = findViewById<EditText>(R.id.MaxHistoryItemsInput).text.toString().toInt()
        val dietDropDown = findViewById<Spinner>(R.id.DietDropDown)
        val cuisineDropDown = findViewById<Spinner>(R.id.CuisineDropDown)
        val mealTypeDropDown = findViewById<Spinner>(R.id.MealTypeDropDown)

        var dietInput = dietDropDown.selectedItem as String
        var cuisineInput = cuisineDropDown.selectedItem as String
        var mealTypeInput = mealTypeDropDown.selectedItem as String

        update(caloriesInput, maxHistoryItemsInput, dietInput, cuisineInput, mealTypeInput)

        Log.i("Current update rapport", "Calories: $caloriesInput, Max History Items: $maxHistoryItemsInput, Diet: $dietInput, Cuisine: $cuisineInput, MealType: $mealTypeInput")

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}




