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
        
        readAndSetViews()
    }

    // This function reads the Settings-database and sets all the View-values to the current settings-values. 
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
            
        }
        cursor.close()
        db.close()
    }

    // Updates the settings-database
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

        db.close()
    }

    // When the user clicks the SAVE-button, it will read all View-values, and call the update() function to update the settings database. 
    // After that, it will send the user to the Main Menu. 
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
        
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}




